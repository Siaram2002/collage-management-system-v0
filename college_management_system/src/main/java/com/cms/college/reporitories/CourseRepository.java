package com.cms.college.reporitories;

import com.cms.college.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Find by courseCode
    Optional<Course> findByCourseCode(String courseCode);
    
    // Check existence by courseCode
    boolean existsByCourseCode(String courseCode);
}
