package com.cms.students.services;



import com.cms.students.models.Student;
import com.cms.students.repository.StudentRepository;
import com.cms.common.CommonUserService;
import com.cms.common.enums.RoleEnum;
import com.cms.college.models.Contact;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentUserService {

    private final StudentRepository studentRepository;
    private final CommonUserService commonUserService;

    private static final RoleEnum STUDENT_ROLE = RoleEnum.STUDENT;
    private static final String DEFAULT_PASSWORD = "stud@123";

    /**
     * Create users for all students who do not yet have a user.
     * Throws exception if a user cannot be created.
     */
    @Transactional
    public void createUsersForAllStudents() {
        List<Student> students = studentRepository.findAll();

        for (Student student : students) {
            if (student.getUser() == null) {
                createUserForStudent(student);
            }
        }
    }

    /**
     * Create users for selected students by their IDs
     * Throws exception if a user cannot be created.
     */
    @Transactional
    public void createUsersForSelectedStudents(List<Long> studentIds) {
        List<Student> students = studentRepository.findAllById(studentIds);

        for (Student student : students) {
            if (student.getUser() == null) {
                createUserForStudent(student);
            }
        }
    }

    /**
     * Helper method to create a user for a student.
     * Throws RuntimeException on failure.
     */
    private void createUserForStudent(Student student) {
        Contact contact = student.getContact();

        var user = commonUserService.createUser(
                student.getRollNumber(),   // username
                STUDENT_ROLE,              // role
                student.getStudentId(),    // referenceId
                DEFAULT_PASSWORD,          // default password
                contact                    // contact info
        );

        if (user == null) {
            throw new RuntimeException("Failed to create user for student: " + student.getRollNumber());
        }

        student.setUser(user);
        studentRepository.save(student);

        log.info("User created for student: {}", student.getRollNumber());
    }
}
