package com.cms.college.reporitories;

import com.cms.college.models.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    // Find department by its code
    Optional<Department> findByDepartmentCode(String departmentCode);
    
    // Check existence by department code
    boolean existsByDepartmentCode(String departmentCode); // ✅ must be "existsBy<Field>"
}

