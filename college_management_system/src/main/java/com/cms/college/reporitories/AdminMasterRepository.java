package com.cms.college.reporitories;






import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cms.college.models.AdminMaster;

@Repository
public interface AdminMasterRepository extends JpaRepository<AdminMaster, Long> {

    // Find Admin by full name (exact match)
    Optional<AdminMaster> findByFullName(String fullName);

    // Search Admins containing part of name (case insensitive)
    List<AdminMaster> findByFullNameContainingIgnoreCase(String name);



    // Find by contact id
    Optional<AdminMaster> findByContactId(Long contactId);



    // Check if admin exists by contact id
    boolean existsByContactId(Long contactId);

    // Check if fullName exists
    boolean existsByFullName(String fullName);
}