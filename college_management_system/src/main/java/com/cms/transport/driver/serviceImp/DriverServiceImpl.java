package com.cms.transport.driver.serviceImp;

import com.cms.transport.driver.dto.ScanResultDTO;

import com.cms.transport.driver.enums.DriverStatus;
import com.cms.transport.driver.model.Driver;
import com.cms.transport.driver.model.QRScanLog;
import com.cms.transport.driver.repository.DriverRepository;
import com.cms.transport.driver.service.DriverService;
import com.cms.transport.driver.service.QRScanLogService;
import com.cms.transport.enums.TransportStatus;
import com.cms.transport.repositories.TransportAssignmentRepository;
import com.cms.busPass.BusPass;
import com.cms.busPass.BusPassService;
import com.cms.college.models.Contact;
import com.cms.college.models.User;
import com.cms.college.reporitories.AddressRepository;
import com.cms.college.reporitories.ContactRepository;
import com.cms.common.CommonUserService;
import com.cms.common.enums.RoleEnum;
import com.cms.common.exceptions.ResourceNotFoundException;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

	private final QRScanLogService qrScanLogService;
	private final TransportAssignmentRepository assignmentRepository;
    private final BusPassService busPassService;

	// ---------------- DRIVER CRUD ----------------
	@Override
	public Driver createDriver(Driver driver, MultipartFile photo) throws IOException {
		if (driver == null)
			throw new IllegalArgumentException("Driver object cannot be null");
		if (driver.getFullName() == null || driver.getFullName().isBlank())
			throw new IllegalArgumentException("Driver full name is required");
		if (driver.getLicenseNumber() == null || driver.getLicenseNumber().isBlank())
			throw new IllegalArgumentException("License number is required");
		if (driver.getLicenseExpiryDate() == null)
			throw new IllegalArgumentException("License expiry date is required");
		if (driver.getContact() == null || driver.getContact().getPhone() == null
				|| driver.getContact().getPhone().isBlank())
			throw new IllegalArgumentException("Contact with phone number is required");
		if (driverRepository.existsByLicenseNumber(driver.getLicenseNumber()))
			throw new IllegalStateException("Driver with same license number already exists");

		Contact contact = driver.getContact();
		if (contact.getAddress() != null && contact.getAddress().getId() == null)
			contact.setAddress(addressRepository.save(contact.getAddress()));
		if (contact.getId() == null)
			contact = contactRepository.save(contact);
		driver.setContact(contact);

	
			driver.setStatus(DriverStatus.ACTIVE);

		Driver savedDriver = driverRepository.save(driver);

		if (photo != null && !photo.isEmpty()) {
			String photoUrl = driverImageService.storePhoto(savedDriver.getDriverId(), photo);
			savedDriver.setPhotoUrl(photoUrl);
			savedDriver = driverRepository.save(savedDriver);
		}

		User user = commonUserService.createUser(savedDriver.getLicenseNumber(), RoleEnum.DRIVER,
				savedDriver.getDriverId(), contact.getPhone(), contact);
		savedDriver.setUserAccount(user);
		savedDriver = driverRepository.save(savedDriver);

		log.info("Driver created successfully with ID: {} and User ID: {}", savedDriver.getDriverId(),
				user.getUserId());
		return savedDriver;
	}

	@Override
	public Driver updateDriverPhoto(Long driverId, MultipartFile photo) throws IOException {
		Driver driver = getDriverById(driverId);
		if (photo == null || photo.isEmpty())
			throw new IllegalArgumentException("No photo provided for update");

		String updatedPhotoUrl = driverImageService.updatePhoto(driver.getPhotoUrl(), driver.getDriverId(), photo);
		driver.setPhotoUrl(updatedPhotoUrl);
		return driverRepository.save(driver);
	}

	@Override
	public Driver getDriverById(Long driverId) {
		return driverRepository.findById(driverId)
				.orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));
	}

	@Override
	public void deleteDriver(Long driverId) {
		Driver driver = getDriverById(driverId);

		boolean isAssigned = assignmentRepository.existsByDriver_DriverIdAndStatus(driverId, TransportStatus.ACTIVE);
		if (isAssigned) {
			driver.setStatus(DriverStatus.INACTIVE);
			driverRepository.save(driver);
			log.warn("Driver ID {} is assigned to a transport. Marked as INACTIVE only.", driverId);
			return;
		}

		driverRepository.delete(driver);
		log.info("Driver {} deleted successfully.", driverId);
	}

	@Override
	public List<Driver> getAllDrivers() {
		return driverRepository.findAll();
	}

	@Override
	public List<Driver> searchDrivers(String keyword) {
		if (keyword == null || keyword.isBlank())
			return driverRepository.findAll();
		return driverRepository.findByFullNameContainingIgnoreCaseOrLicenseNumberContainingIgnoreCase(keyword, keyword);
	}

	@Override
	public Driver updateDriver(Long driverId, Driver updatedDriver) {
		Driver existing = getDriverById(driverId);

		if (updatedDriver.getFullName() != null)
			existing.setFullName(updatedDriver.getFullName());
		if (updatedDriver.getLicenseNumber() != null)
			existing.setLicenseNumber(updatedDriver.getLicenseNumber());
		if (updatedDriver.getLicenseExpiryDate() != null)
			existing.setLicenseExpiryDate(updatedDriver.getLicenseExpiryDate());
		if (updatedDriver.getStatus() != null)
			existing.setStatus(updatedDriver.getStatus());

		return driverRepository.save(existing);
	}

	@Override
	public Driver updateLicenseDetails(Long driverId, String licenseNumber, LocalDate expiry) {
		Driver driver = getDriverById(driverId);
		if (licenseNumber != null && !licenseNumber.isBlank())
			driver.setLicenseNumber(licenseNumber);
		if (expiry != null)
			driver.setLicenseExpiryDate(expiry);
		return driverRepository.save(driver);
	}

	@Override
	public Driver assignContact(Long driverId, Long contactId) {
		Driver driver = getDriverById(driverId);
		Contact contact = contactRepository.findById(contactId)
				.orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
		driver.setContact(contact);
		return driverRepository.save(driver);
	}

	@Override
	public Driver assignAddress(Long driverId, Long addressId) {
		Driver driver = getDriverById(driverId);
		Contact contact = driver.getContact();
		if (contact == null)
			throw new IllegalStateException("Driver has no contact assigned");

		contact.setAddress(addressRepository.findById(addressId)
				.orElseThrow(() -> new ResourceNotFoundException("Address not found")));
		contactRepository.save(contact);
		return driverRepository.save(driver);
	}

	@Override
	public Driver assignUser(Long driverId, Long userId) {
		Driver driver = getDriverById(driverId);
		User user = commonUserService.getUserById(userId);
		driver.setUserAccount(user);
		return driverRepository.save(driver);
	}

	@Override
	public List<Driver> getDriversWithExpiredLicense() {
		return driverRepository.findAll().stream()
				.filter(d -> d.getLicenseExpiryDate() != null && d.getLicenseExpiryDate().isBefore(LocalDate.now()))
				.toList();
	}

	@Override
	public void deleteAllDrivers() {
		driverRepository.deleteAll();
		log.info("All drivers deleted successfully");
	}

	@Override
	public Driver updateStatus(Long driverId, String status) {
	    Driver driver = getDriverById(driverId);

	    // Convert the input status to uppercase for safety
	    String normalizedStatus = status.toUpperCase();

	    // Validate status
	    if (!normalizedStatus.equals("ACTIVE") && !normalizedStatus.equals("INACTIVE")) {
	        throw new IllegalArgumentException("Invalid status: " + status);
	    }

	    // Update driver status
	    driver.setStatus(DriverStatus.valueOf(normalizedStatus));

	    // Update user account status if exists
	    if (driver.getUserAccount() != null) {
	        driver.getUserAccount().setStatus(com.cms.common.enums.Status.valueOf(normalizedStatus));
	        commonUserService.updateUser(driver.getUserAccount());
	    }

	    // Save and return updated driver
	    return driverRepository.save(driver);
	}


	// ---------------- QR SCAN ----------------
	@Override
	public BusPass driverScanResult(String qrUid, Long driverId) throws Exception {


	    // 1️⃣ Validate input
	    if (qrUid == null ) {
	        throw new IllegalArgumentException("Invalid QR data received");
	    }
	 

	    // 3️⃣ Fetch the bus pass using BusPassService
	    BusPass busPass = busPassService.scanBusPass(qrUid);
	    
	    System.out.println(busPass.toString());

	    // 4️⃣ Optionally log the scan
	    qrScanLogService.createLog(busPass.getRollNumber(), driverId);

	    // 5️⃣ Return the BusPass entity directly
	    return busPass;
	}



	@Override
	public List<QRScanLog> getScanLogsByDriver(Long driverId) {
		getDriverById(driverId); // verify driver exists
		return qrScanLogService.getLogsByDriver(driverId);
	}

	@Override
	public Driver updatePhoto(Long driverId, MultipartFile photo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
