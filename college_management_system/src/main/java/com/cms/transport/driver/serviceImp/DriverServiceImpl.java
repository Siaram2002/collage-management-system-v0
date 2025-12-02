package com.cms.transport.driver.serviceImp;

import com.cms.transport.driver.config.DriverImageConfig;
import com.cms.transport.driver.dto.ScanResultDTO;
import com.cms.transport.driver.enums.DriverStatus;
import com.cms.transport.driver.model.Driver;
import com.cms.transport.driver.repository.DriverRepository;
import com.cms.transport.driver.service.DriverService;

import com.cms.college.models.Address;
import com.cms.college.models.Contact;
import com.cms.college.models.User;
import com.cms.college.reporitories.AddressRepository;
import com.cms.college.reporitories.ContactRepository;
import com.cms.common.CommonUserService;
import com.cms.common.exceptions.ResourceNotFoundException;
import com.cms.common.repositories.UserRepository;
import com.cms.qr.QRUtils;
import com.cms.students.models.Student;
import com.cms.students.services.StudentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DriverServiceImpl implements DriverService {

	private final DriverRepository driverRepository;
	private final ContactRepository contactRepository;
	private final AddressRepository addressRepository;

	private final CommonUserService commonUserService;
	private final DriverImageService driverImageService;

	private final StudentService studentService;
	private final QRUtils qrUtil;

	@Override
	@Transactional
	public Driver createDriverWithPhoto(Driver driver, MultipartFile photo) throws IOException {

		log.info("Creating new driver with photo...");

		// -------------------------------------------------------------------------
		// 1. Basic null checks and validations
		// -------------------------------------------------------------------------
		if (driver == null) {
			log.error("Driver object is null");
			throw new IllegalArgumentException("Driver object cannot be null");
		}

		if (isBlank(driver.getFullName())) {
			throw new IllegalArgumentException("Driver full name is required");
		}

		if (isBlank(driver.getLicenseNumber())) {
			throw new IllegalArgumentException("License number is required");
		}

		if (driver.getLicenseExpiryDate() == null) {
			throw new IllegalArgumentException("License expiry date is required");
		}

		if (driver.getContact() == null || isBlank(driver.getContact().getPhone())) {
			throw new IllegalArgumentException("Contact with phone number is required");
		}

		if (driverRepository.existsByLicenseNumber(driver.getLicenseNumber())) {
			log.warn("Duplicate license number found: {}", driver.getLicenseNumber());
			throw new IllegalStateException("Driver with same license number already exists");
		}

		// -------------------------------------------------------------------------
		// 2. Save contact (and address if exists)
		// -------------------------------------------------------------------------
		Contact contact = driver.getContact();
		if (contact.getAddress() != null && contact.getAddress().getId() == null) {
			contact.setAddress(addressRepository.save(contact.getAddress()));
		}
		if (contact.getId() == null) {
			contact = contactRepository.save(contact);
		}
		driver.setContact(contact);

		// -------------------------------------------------------------------------
		// 3. Set default status if not provided
		// -------------------------------------------------------------------------
		if (driver.getStatus() == null) {
			driver.setStatus(DriverStatus.INACTIVE);
		}

		// -------------------------------------------------------------------------
		// 4. Save driver first to get driverId
		// -------------------------------------------------------------------------
		Driver savedDriver = driverRepository.save(driver);

		// -------------------------------------------------------------------------
		// 5. Handle photo upload using DriverImageService
		// -------------------------------------------------------------------------
		if (photo != null && !photo.isEmpty()) {
			String photoUrl = driverImageService.storePhoto(savedDriver.getDriverId(), photo);
			savedDriver.setPhotoUrl(photoUrl);
			savedDriver = driverRepository.save(savedDriver);
		}

		// -------------------------------------------------------------------------
		// 6. Create system user account for driver
		// -------------------------------------------------------------------------
		User user = commonUserService.createUser(savedDriver.getLicenseNumber(), "DRIVER", savedDriver.getDriverId(), // referenceId
				contact.getPhone(), contact);
		savedDriver.setUserAccount(user);

		savedDriver = driverRepository.save(savedDriver);

		log.info("Driver created successfully with ID: {} and User ID: {}", savedDriver.getDriverId(),
				user.getUserId());
		return savedDriver;
	}

	@Override
	@Transactional
	public Driver updateDriverPhoto(Long driverId, MultipartFile photo) throws IOException {

		log.info("Updating driver photo for driverId: {}", driverId);

		// 1. Fetch driver
		Driver driver = driverRepository.findById(driverId)
				.orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));

		if (photo == null || photo.isEmpty()) {
			throw new IllegalArgumentException("No photo provided for update");
		}

		// 2. Delegate photo update to DriverImageService
		String updatedPhotoUrl = driverImageService.updatePhoto(driver.getPhotoUrl(), driver.getDriverId(), photo);

		// 3. Update driver entity
		driver.setPhotoUrl(updatedPhotoUrl);
		Driver updatedDriver = driverRepository.save(driver);

		log.info("Driver photo updated successfully for driverId: {}", driverId);
		return updatedDriver;
	}

	// QR Scanning Results

	@Override
	public ScanResultDTO driverScanResult(String qrData) throws Exception {
	    log.info("Processing QR scan data");

	    // ---------------------------
	    // 1. Validate QR Input
	    // ---------------------------
	    if (qrData == null || qrData.trim().isEmpty()) {
	        log.error("QR Data is empty or null");
	        throw new IllegalArgumentException("Invalid QR data received");
	    }

	    String decrypted;
	    String studentRollNumber;

	    try {
	        decrypted = qrUtil.decryptAES(qrData);
	        studentRollNumber = getRightSideData(decrypted);

	    } catch (Exception e) {
	        log.error("Failed to decrypt QR data", e);
	        throw new RuntimeException("QR decryption failed, invalid or tampered QR code");
	    }

	    log.info("Extracted Roll Number from QR: {}", studentRollNumber);

	    // ---------------------------
	    // 2. Fetch Student
	    // ---------------------------
	    Student student;
	    try {
	        student = studentService.getByRollNumber(studentRollNumber);
	        if (student == null) {
	            throw new ResourceNotFoundException("No student found for roll number: " + studentRollNumber);
	        }
	    } catch (ResourceNotFoundException rnfe) {
	        log.warn("Student not found: {}", studentRollNumber);
	        throw rnfe;
	    } catch (Exception e) {
	        log.error("Unexpected error while fetching student {}", studentRollNumber, e);
	        throw new RuntimeException("Failed to fetch student details");
	    }

	    // ---------------------------
	    // 3. Map to DTO
	    // ---------------------------
	    ScanResultDTO dto = new ScanResultDTO();
	    try {
	        dto.setFirstName(student.getFirstName());
	        dto.setMiddleName(student.getMiddleName());
	        dto.setLastName(student.getLastName());

	        dto.setRollNumber(student.getRollNumber());
	        dto.setAdmissionNumber(student.getAdmissionNumber());
	        dto.setAdmissionYear(student.getAdmissionYear());

	        dto.setDepartment(student.getDepartment().getShortCode());
	        dto.setCourse(student.getCourse().getCourseCode());

	        dto.setStatus(student.getStatus());

	        dto.setPhotoUrl(student.getPhotoUrl());

	        // TODO: replace with actual logic
	        dto.setFeePaid(true);

	    } catch (Exception e) {
	        log.error("Failed to map student to ScanResultDTO", e);
	        throw new RuntimeException("Error mapping student data");
	    }

	    log.info("QR Scan processed successfully for {}", studentRollNumber);
	    return dto;
	}


	private String getRightSideData(String input) {
		if (input == null || !input.contains("-")) {
			return input; // or return null based on your need
		}
		return input.substring(input.indexOf("-") + 1);
	}

	// Helper method to get file extension
	private String getExtension(String originalFileName) {
		if (originalFileName == null)
			return "";
		int dotIndex = originalFileName.lastIndexOf(".");
		return (dotIndex == -1) ? "" : originalFileName.substring(dotIndex);
	}

	// Helper method
	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	@Override
	public Driver updateDriver(Long driverId, Driver updatedDriver) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Driver getDriverById(Long driverId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteDriver(Long driverId) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Driver> getAllDrivers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Driver> searchDrivers(String keyword) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Driver updateStatus(Long driverId, String status) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Driver updateLicenseDetails(Long driverId, String licenseNumber, LocalDate expiry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Driver updatePhoto(Long driverId, MultipartFile photo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Driver assignContact(Long driverId, Long contactId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Driver assignAddress(Long driverId, Long addressId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Driver assignUser(Long driverId, Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Driver> getDriversWithExpiredLicense() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteAllDrivers() {
		// TODO Auto-generated method stub

	}

}
