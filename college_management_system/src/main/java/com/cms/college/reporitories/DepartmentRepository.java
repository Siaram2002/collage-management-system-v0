package com.cms.college.reporitories;

import com.cms.college.models.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    // Find by department shortCode
    Optional<Department> findByShortCode(String shortCode);
    
    // Check existence by shortCode
    boolean existsByShortCode(String shortCode);
}
