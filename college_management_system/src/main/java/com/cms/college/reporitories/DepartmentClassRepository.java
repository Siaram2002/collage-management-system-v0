package com.cms.college.reporitories;

import com.cms.college.models.DepartmentClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentClassRepository extends JpaRepository<DepartmentClass, Long> {
    List<DepartmentClass> findByDepartment_DepartmentId(Long departmentId);

}