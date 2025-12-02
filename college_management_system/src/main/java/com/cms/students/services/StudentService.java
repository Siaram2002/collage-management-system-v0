package com.cms.students.services;

import com.cms.students.enums.EnrollmentStatus;
import com.cms.students.enums.StudentStatus;
import com.cms.students.models.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface StudentService {

    // CRUD
    Student createStudent(Student student);
    public Student createStudentWithPhoto(Student student, MultipartFile photoFile) throws IOException;
    Student updateStudent(Long studentId, Student updatedStudent);
    void deleteStudent(Long studentId);
    Student getStudentById(Long studentId);

    // Status & Enrollment
    Student updateStudentStatus(Long studentId, StudentStatus status);
    Student updateEnrollmentStatus(Long studentId, EnrollmentStatus status);
    Student activateStudentUser(Long studentId);

    // Photo upload
    String uploadStudentPhoto(Long studentId, MultipartFile imageFile);


    // Bulk CSV
    void uploadStudentsBulk(MultipartFile file, String imageFolderPath) throws IOException;

    // Search / Filters
    Student getByRollNumber(String rollNumber);
    Student getByAdmissionNumber(String admissionNumber);
    Page<Student> getByDepartment(Long departmentId, Pageable pageable);
    Page<Student> getByCourse(Long courseId, Pageable pageable);
    Page<Student> getByStatus(StudentStatus status, Pageable pageable);
    Page<Student> getByEnrollmentStatus(EnrollmentStatus status, Pageable pageable);
    Page<Student> filterStudents(Long departmentId, Long courseId, StudentStatus status, Pageable pageable);
}
