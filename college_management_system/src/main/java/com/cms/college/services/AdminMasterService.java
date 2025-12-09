package com.cms.college.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cms.college.dto.AdminMasterDTO;
import com.cms.college.models.AdminMaster;
import com.cms.common.enums.Status;

@Service
public interface AdminMasterService {

    /**
     * Create a new admin with optional photo
     *
     * @param dto   AdminMasterDTO containing admin, contact, and address details
     * @param photo MultipartFile photo of the admin (optional)
     * @return AdminMasterDTO with saved data and photo URL
     * @throws Exception if saving fails
     */
    AdminMasterDTO createAdmin(AdminMasterDTO dto, MultipartFile photo) throws Exception;

    /**
     * Update an existing admin by ID with optional photo
     *
     * @param id    Admin ID to update
     * @param dto   AdminMasterDTO containing updated data
     * @param photo MultipartFile photo of the admin (optional)
     * @return AdminMasterDTO with updated data and photo URL
     * @throws Exception if admin not found or saving fails
     */
    AdminMasterDTO updateAdmin(Long id, AdminMasterDTO dto, MultipartFile photo) throws Exception;

    /**
     * Get admin by ID
     *
     * @param id Admin ID
     * @return AdminMasterDTO
     */
    AdminMaster getAdminById(Long id);

    /**
     * Get all admins
     *
     * @return List of AdminMasterDTO
     */
    List<AdminMasterDTO> getAllAdmins();

    /**
     * Delete admin by ID (also deletes photo if exists)
     *
     * @param id Admin ID
     * @throws Exception if admin not found or deletion fails
     */
    void deleteAdmin(Long id) throws Exception;

	AdminMasterDTO activateUser(Long adminId);

	AdminMasterDTO setAdminAndUserStatus(Long adminId, Status status);
}
