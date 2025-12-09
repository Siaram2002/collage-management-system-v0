package com.cms.college.service.imp;

import com.cms.college.dto.AdminImageService;
import com.cms.college.dto.AdminMasterDTO;
import com.cms.college.models.AdminMaster;
import com.cms.college.models.Address;
import com.cms.college.models.Contact;
import com.cms.college.models.User;
import com.cms.college.reporitories.AdminMasterRepository;
import com.cms.college.reporitories.AddressRepository;
import com.cms.college.reporitories.ContactRepository;
import com.cms.college.services.AdminMasterService;
import com.cms.common.CommonUserService;
import com.cms.common.enums.RoleEnum;
import com.cms.common.enums.Status;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AdminMasterServiceImpl implements AdminMasterService {

    private final AdminMasterRepository adminRepo;
    private final ContactRepository contactRepo;
    private final AddressRepository addressRepo;
    private final AdminImageService adminImageService;
    private final CommonUserService commonUserService;

    // ---------------- CREATE ADMIN ----------------
    @Override
    public AdminMasterDTO createAdmin(AdminMasterDTO dto, MultipartFile photo) throws Exception {
        log.info("Creating admin: {}", dto.getFullName());

        // 1️⃣ Save Address
        Address address = AdminMasterMapper.toAddressEntity(dto);
        addressRepo.save(address);

        // 2️⃣ Save Contact
        Contact contact = AdminMasterMapper.toContactEntity(dto, address);
        contactRepo.save(contact);

        // 3️⃣ Save Admin
        AdminMaster admin = AdminMasterMapper.toEntity(dto, contact);
        // Set adminStatus from DTO or default ACTIVE
        admin.setAdminStatus(dto.getAdminStatus() != null ? dto.getAdminStatus() : Status.ACTIVE);
        adminRepo.save(admin);

        // 4️⃣ Handle photo upload
        if (photo != null && !photo.isEmpty()) {
            String photoUrl = adminImageService.storePhoto(admin.getId(), photo);
            admin.setPhotoUrl(photoUrl);
            adminRepo.save(admin);
            log.info("Admin photo uploaded: {}", photoUrl);
        }

        // 5️⃣ Optional: create or activate User
        createOrActivateUser(admin, contact);

        return AdminMasterMapper.toDTO(admin);
    }

    // ---------------- UPDATE ADMIN ----------------
    @Override
    public AdminMasterDTO updateAdmin(Long id, AdminMasterDTO dto, MultipartFile photo) throws Exception {
        log.info("Updating admin with id: {}", id);

        AdminMaster admin = adminRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + id));

        // Update Address
        Address address = admin.getContact().getAddress();
        address.setLine1(dto.getLine1());
        address.setLine2(dto.getLine2());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setState(dto.getState());
        address.setCountry(dto.getCountry());
        address.setPin(dto.getPin());
        address.setLatitude(dto.getLatitude());
        address.setLongitude(dto.getLongitude());
        addressRepo.save(address);

        // Update Contact
        Contact contact = admin.getContact();
        contact.setPhone(dto.getPhone());
        contact.setEmail(dto.getEmail());
        contact.setAltPhone(dto.getAltPhone());
        contact.setAddress(address);
        contactRepo.save(contact);

        // Update Admin
        admin.setFullName(dto.getFullName());
        admin.setContact(contact);
        // Set adminStatus from DTO or keep existing
        admin.setAdminStatus(dto.getAdminStatus() != null ? dto.getAdminStatus() : admin.getAdminStatus());

        // Update photo if provided
        if (photo != null && !photo.isEmpty()) {
            String photoUrl = adminImageService.updatePhoto(admin.getPhotoUrl(), admin.getId(), photo);
            admin.setPhotoUrl(photoUrl);
            log.info("Admin photo updated: {}", photoUrl);
        }

        // Optional: create or activate User
        createOrActivateUser(admin, contact);

        adminRepo.save(admin);
        return AdminMasterMapper.toDTO(admin);
    }

    // ---------------- GET ADMIN BY ID ----------------
    @Override
    public AdminMaster getAdminById(Long id) {
        AdminMaster admin = adminRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + id));
        return admin;
    }

    // ---------------- GET ALL ADMINS ----------------
    @Override
    public List<AdminMasterDTO> getAllAdmins() {
        return adminRepo.findAll().stream()
                .map(AdminMasterMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ---------------- DELETE ADMIN ----------------
    @Override
    public void deleteAdmin(Long id) throws Exception {
        AdminMaster admin = adminRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + id));

        // Delete photo if exists
        if (admin.getPhotoUrl() != null) {
            adminImageService.deletePhoto(admin.getPhotoUrl());
            log.info("Deleted photo for admin: {}", id);
        }

        // Optional: deactivate user
        if (admin.getUser() != null) {
            admin.getUser().setStatus(Status.INACTIVE);
            commonUserService.updateUser(admin.getUser());
            log.info("Deactivated user for admin: {}", id);
        }

        adminRepo.deleteById(id);
        log.info("Admin deleted with id: {}", id);
    }

    // ---------------- ACTIVATE USER ----------------
    @Override
    public AdminMasterDTO activateUser(Long adminId) {
        AdminMaster admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + adminId));

        if (admin.getUser() == null) {
            throw new RuntimeException("No user associated with this admin");
        }

        admin.getUser().setStatus(Status.ACTIVE);
        commonUserService.updateUser(admin.getUser());
        log.info("Activated user for admin: {}", adminId);

        return AdminMasterMapper.toDTO(admin);
    }

    // ---------------- PRIVATE HELPER: CREATE OR ACTIVATE USER ----------------
    private void createOrActivateUser(AdminMaster admin, Contact contact) {
        if (admin.getUser() == null) {
            String defaultPassword = "admin@123"; // Can replace with secure random
            User user = commonUserService.createUser(
                    contact.getEmail(),
                    RoleEnum.ADMIN,
                    admin.getId(),
                    defaultPassword,
                    contact
            );
            admin.setUser(user);
            log.info("Created user for admin: {}", admin.getId());
        } else {
            // Activate existing user
            if (admin.getUser().getStatus() != Status.ACTIVE) {
                admin.getUser().setStatus(Status.ACTIVE);
                commonUserService.updateUser(admin.getUser());
                log.info("Activated existing user for admin: {}", admin.getId());
            }
        }
    }
    
    @Override
    @Transactional
    public AdminMasterDTO setAdminAndUserStatus(Long adminId, Status status) {
        AdminMaster admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + adminId));

        // Update Admin status
        admin.setAdminStatus(status);

        // Update User status if exists
        if (admin.getUser() != null) {
            admin.getUser().setStatus(status);
            commonUserService.updateUser(admin.getUser());
        }

        adminRepo.save(admin);

        return AdminMasterMapper.toDTO(admin);
    }

}
