package com.cms.transport.driver.serviceImp;


import  com.cms.common.repositories.UserRepository;
import com.cms.college.reporitories.AddressRepository;
import com.cms.college.reporitories.ContactRepository;
import com.cms.common.repositories.RoleRepository;
import com.cms.common.exceptions.ResourceNotFoundException;
import com.cms.transport.driver.dto.DriverRegisterRequest;
import com.cms.transport.driver.enums.DriverStatus;
import com.cms.transport.driver.model.Driver;
import com.cms.transport.driver.repository.DriverRepository;
import com.cms.transport.driver.service.DriverService;
import com.cms.college.models.User;
import com.cms.college.models.Role;
import com.cms.college.models.Contact;
import com.cms.college.models.Address;

import com.cms.common.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ContactRepository contactRepository;
    private final AddressRepository addressRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Map<String, Object> registerDriver(DriverRegisterRequest req) {

        Map<String, Object> response = new HashMap<>();

        if (req.getRole() == null || !req.getRole().equalsIgnoreCase("DRIVER"))
            throw new IllegalArgumentException("Role must be DRIVER for this registration");

        if (req.getLicenseNumber() == null || req.getLicenseExpiryDate() == null)
            throw new IllegalArgumentException("License number and expiry date are required");

        Optional<Driver> existingDriverOpt = driverRepository.findByLicenseNumber(req.getLicenseNumber());
        if (existingDriverOpt.isPresent()) {

            Driver existingDriver = existingDriverOpt.get();
            User existingUser = existingDriver.getUserAccount();

            response.put("status", "EXISTING");
            response.put("message", "Driver already registered");
            Map<String, Object> data = new HashMap<>();
            data.put("driverId", existingDriver.getDriverId());
            data.put("fullName", existingDriver.getFullName());
            data.put("username", existingUser.getUsername());
            data.put("status", existingDriver.getStatus().name());
            data.put("photoUrl", existingDriver.getPhotoUrl());
            response.put("data", data);
            return response;
        }

        Contact contact = Contact.builder()
                .phone(req.getContact().getPhone())
                .email(req.getContact().getEmail())
                .altPhone(req.getContact().getAltPhone())
                .build();
        contact = contactRepository.save(contact);

        Address address = new Address();
        address.setLine1(req.getAddress().getLine1());
        address.setLine2(req.getAddress().getLine2());
        address.setDistrict(req.getAddress().getDistrict());
        address.setCity(req.getAddress().getCity());
        address.setState(req.getAddress().getState());
        address.setCountry(req.getAddress().getCountry());
        address.setPin(req.getAddress().getPin());
        address = addressRepository.save(address);

        String baseName = req.getFullName().toLowerCase().replaceAll("\\s+", "");
        String username = baseName + (int)(Math.random() * 9000 + 1000);
        while (userRepository.existsByUsername(username)) {
            username = baseName + (int)(Math.random() * 9000 + 1000);
        }

        String firstThree = baseName.substring(0, Math.min(3, baseName.length()));
        String last4 = contact.getPhone().substring(contact.getPhone().length() - 4);
        String tempPassword = firstThree + "@" + last4;
        String encryptedPassword = passwordEncoder.encode(tempPassword);

        Driver driver = new Driver();
        driver.setFullName(req.getFullName());
        driver.setLicenseNumber(req.getLicenseNumber());
        driver.setLicenseExpiryDate(req.getLicenseExpiryDate());
        driver.setContact(contact);
        driver.setAddress(address);
        driver.setPhotoUrl(req.getPhotoUrl());
        driver.setStatus(req.getLicenseExpiryDate().isBefore(LocalDate.now()) ? DriverStatus.SUSPENDED : DriverStatus.ACTIVE);
        driver = driverRepository.save(driver);

        User user = new User();
        user.setUsername(username);
        user.setPassword(encryptedPassword);
        user.setContact(contact);
        user.setReferenceId(driver.getDriverId());
        Role roleEntity = roleRepository.findByRoleName("DRIVER")
                .orElseThrow(() -> new ResourceNotFoundException("Role DRIVER not configured"));
        user.getRoles().add(roleEntity);
        user.setStatus(req.getLicenseExpiryDate().isBefore(LocalDate.now()) ? Status.INACTIVE : Status.ACTIVE);
        user = userRepository.save(user);

        driver.setUserAccount(user);
        driverRepository.save(driver);

        response.put("status", "SUCCESS");
        response.put("message", "Driver registered successfully");
        Map<String, Object> data = new HashMap<>();
        data.put("driverId", driver.getDriverId());
        data.put("username", username);
        data.put("tempPassword", tempPassword);
        data.put("status", driver.getStatus().name());
        data.put("photoUrl", driver.getPhotoUrl());
        response.put("data", data);

        return response;
    }


    // -------------------- Bulk Upload Excel ---------------------
    @Override
    @Transactional
    public Map<String, Object> bulkUploadDriversExcel(MultipartFile file) {

        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> successList = new ArrayList<>();
        List<Map<String, Object>> errorList = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean header = true;

            for (Row row : sheet) {
                if (header) { header = false; continue; }

                try {
                    // --- Handle License Expiry as date or string ---
                    Cell expiryCell = row.getCell(2);
                    LocalDate expiryDate;
                    if (expiryCell.getCellType() == CellType.NUMERIC) {
                        expiryDate = expiryCell.getLocalDateTimeCellValue().toLocalDate();
                    } else {
                        expiryDate = LocalDate.parse(expiryCell.getStringCellValue());
                    }

                    DriverRegisterRequest req = DriverRegisterRequest.builder()
                            .role("DRIVER")
                            .fullName(getStringCell(row.getCell(0)))
                            .licenseNumber(getStringCell(row.getCell(1)))
                            .licenseExpiryDate(expiryDate)
                            .contact(new com.cms.transport.dto.ContactDTO(
                                    getStringCell(row.getCell(3)),
                                    getStringCell(row.getCell(4)),
                                    getStringCell(row.getCell(5))
                            ))
                            .address(new com.cms.transport.dto.AddressDTO(
                                    getStringCell(row.getCell(6)),
                                    getStringCell(row.getCell(7)),
                                    getStringCell(row.getCell(8)),
                                    getStringCell(row.getCell(9)),
                                    getStringCell(row.getCell(10)),
                                    getStringCell(row.getCell(11)),
                                    getStringCell(row.getCell(12))
                            ))
                            .photoUrl(getStringCell(row.getCell(13)))
                            .build();

                    Map<String, Object> result = registerDriver(req);
                    successList.add(result);

                } catch (Exception e) {
                    Map<String, Object> err = new HashMap<>();
                    err.put("rowNumber", row.getRowNum());
                    err.put("error", e.getMessage());
                    errorList.add(err);
                }
            }

            response.put("successCount", successList.size());
            response.put("errorCount", errorList.size());
            response.put("success", successList);
            response.put("errors", errorList);

        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("message", e.getMessage());
        }

        return response;
    }



    // -------------------- Bulk Upload Excel ---------------------





    // -------------------- Helper ---------------------
    private String getStringCell(Cell cell) {
        return cell == null ? "" : cell.toString().trim();
    }
}

