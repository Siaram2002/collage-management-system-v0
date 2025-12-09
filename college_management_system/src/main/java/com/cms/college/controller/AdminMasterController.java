package com.cms.college.controller;

import com.cms.college.dto.AdminMasterDTO;
import com.cms.college.service.imp.AdminMasterMapper;
import com.cms.college.services.AdminMasterService;
import com.cms.common.enums.Status;
import com.cms.common.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminMasterController {

	private final AdminMasterService adminService;

	// ---------------- Create Admin ----------------
	@PostMapping(value = "/register", consumes = { "multipart/form-data" })
	public ResponseEntity<AdminMasterDTO> createAdmin(@RequestPart("admin") String adminJson,
			@RequestPart(value = "photo", required = false) MultipartFile photo) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		AdminMasterDTO dto = mapper.readValue(adminJson, AdminMasterDTO.class);

		AdminMasterDTO createdAdmin = adminService.createAdmin(dto, photo);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdAdmin);
	}

	// ---------------- Update Admin ----------------
	@PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
	public ResponseEntity<AdminMasterDTO> updateAdmin(@PathVariable Long id, @RequestPart("admin") String adminJson,
			@RequestPart(value = "photo", required = false) MultipartFile photo) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		AdminMasterDTO dto = mapper.readValue(adminJson, AdminMasterDTO.class);

		AdminMasterDTO updatedAdmin = adminService.updateAdmin(id, dto, photo);
		return ResponseEntity.ok(updatedAdmin);
	}

	// ---------------- Get Admin by ID ----------------
	@GetMapping("/{id}")
	public ResponseEntity<AdminMasterDTO> getAdminById(@PathVariable Long id) {
		AdminMasterDTO admin = AdminMasterMapper.toDTO(adminService.getAdminById(id));
		if (admin == null)
			throw new ResourceNotFoundException("Admin not found with id: " + id);
		return ResponseEntity.ok(admin);
	}

	// ---------------- Get All Admins ----------------
	@GetMapping
	public ResponseEntity<List<AdminMasterDTO>> getAllAdmins() {
		List<AdminMasterDTO> admins = adminService.getAllAdmins();
		return ResponseEntity.ok(admins);
	}

	// ---------------- Delete Admin ----------------
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteAdmin(@PathVariable Long id) throws Exception {
		adminService.deleteAdmin(id);
		return ResponseEntity.ok("Admin deleted successfully");
	}

	@PostMapping("/{id}/set-status")
	public ResponseEntity<AdminMasterDTO> setAdminStatus(@PathVariable Long id,
			@RequestParam("status") String statusStr) {

		Status status;
		try {
			status = Status.valueOf(statusStr.toUpperCase());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(null); // or return a proper error DTO/message
		}

		AdminMasterDTO admin = adminService.setAdminAndUserStatus(id, status);
		return ResponseEntity.ok(admin);
	}

}
