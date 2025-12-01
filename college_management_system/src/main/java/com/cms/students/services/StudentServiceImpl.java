package com.cms.students.services;

import com.cms.students.enums.EnrollmentStatus;
import com.cms.students.enums.StudentStatus;
import com.cms.students.exceptions.DuplicateStudentException;
import com.cms.students.exceptions.FileStorageException;
import com.cms.students.exceptions.StudentNotFoundException;
import com.cms.students.models.Student;
import com.cms.students.repository.StudentRepository;
import com.cms.students.services.media.PhotoStorageService;
import com.cms.students.services.media.QRFileStorageService;
import com.cms.students.utils.StudentCsvParser;

import com.cms.college.models.Course;
import com.cms.college.models.Department;
import com.cms.college.models.User;
import com.cms.college.reporitories.CourseRepository;
import com.cms.college.reporitories.DepartmentRepository;
import com.cms.common.CommonUserService;
import com.cms.common.enums.Status;
import com.cms.common.repositories.UserRepository;
import com.cms.qr.service.QRService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final CommonUserService commonUserService;
    private final QRService qrService;
    private final QRFileStorageService qrFileStorageService;
    private final PhotoStorageService photoStorageService;
    private final Executor qrExecutor;
    private final Executor photoExecutor;
    private final Executor studentBulkExecutor;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;

    // ------------------- CREATE -------------------
    @Override
    @Transactional
    public Student createStudent(Student student) {
        log.info("Creating student: {}", student.getRollNumber());
        validateStudentUnique(student);

        Student saved = studentRepository.save(student);
        log.debug("Student saved with ID={}", saved.getStudentId());

        User user = commonUserService.createUser(saved.getRollNumber(), "STUDENT", saved.getStudentId(),
                "stud@123", null);
        user.setContact(saved.getContact());
        saved.setUser(user);
        studentRepository.save(saved);

        generateQrAsync(saved);
        log.info("Student created successfully: {}", saved.getRollNumber());
        return saved;
    }

    private void validateStudentUnique(Student student) {
        if (studentRepository.existsByRollNumber(student.getRollNumber()))
            throw new DuplicateStudentException("Duplicate roll number: " + student.getRollNumber());
        if (studentRepository.existsByAdmissionNumber(student.getAdmissionNumber()))
            throw new DuplicateStudentException("Duplicate admission number: " + student.getAdmissionNumber());
    }

    // ------------------- UPDATE -------------------
    @Override
    @Transactional
    public Student updateStudent(Long studentId, Student updatedStudent) {
        Student existing = findStudentOrThrow(studentId);

        boolean rollChanged = !existing.getRollNumber().equals(updatedStudent.getRollNumber());
        boolean admissionChanged = !existing.getAdmissionNumber().equals(updatedStudent.getAdmissionNumber());

        if (rollChanged && studentRepository.existsByRollNumber(updatedStudent.getRollNumber()))
            throw new DuplicateStudentException("Duplicate roll number: " + updatedStudent.getRollNumber());
        if (admissionChanged && studentRepository.existsByAdmissionNumber(updatedStudent.getAdmissionNumber()))
            throw new DuplicateStudentException("Duplicate admission number: " + updatedStudent.getAdmissionNumber());

        copyStudentFields(existing, updatedStudent);

        if (admissionChanged && existing.getUser() != null) {
            existing.getUser().setUsername(updatedStudent.getAdmissionNumber());
            userRepository.save(existing.getUser());
        }

        Student saved = studentRepository.save(existing);

        if (rollChanged) regenerateQrAsync(saved);

        log.info("Student updated successfully: {}", saved.getRollNumber());
        return saved;
    }

    private void copyStudentFields(Student existing, Student updated) {
        existing.setFirstName(updated.getFirstName());
        existing.setMiddleName(updated.getMiddleName());
        existing.setLastName(updated.getLastName());
        existing.setDob(updated.getDob());
        existing.setGender(updated.getGender());
        existing.setDepartment(updated.getDepartment());
        existing.setCourse(updated.getCourse());
        existing.setAdmissionYear(updated.getAdmissionYear());
        existing.setAdmissionNumber(updated.getAdmissionNumber());
        existing.setRollNumber(updated.getRollNumber());
        existing.setStatus(updated.getStatus());
        existing.setEnrollmentStatus(updated.getEnrollmentStatus());

        if (updated.getContact() != null) existing.setContact(updated.getContact());
    }

    // ------------------- DELETE -------------------
    @Override
    @Transactional
    public void deleteStudent(Long studentId) {
        Student student = findStudentOrThrow(studentId);
        deleteStudentQr(student);
        deleteStudentPhoto(student);
        studentRepository.delete(student);
        log.info("Student deleted successfully: {}", student.getRollNumber());
    }

    // ------------------- STATUS -------------------
    @Override
    @Transactional
    public Student updateStudentStatus(Long studentId, StudentStatus status) {
        // Fetch student or throw exception if not found
        Student student = findStudentOrThrow(studentId);

        // Update student status
        student.setStatus(status);

        // Update linked User status if exists
        if (student.getUser() != null) {
            // Map StudentStatus to User Status
            switch (status) {
                case ACTIVE -> student.getUser().setStatus(Status.ACTIVE);
                case INACTIVE -> student.getUser().setStatus(Status.INACTIVE);
                case SUSPENDED -> student.getUser().setStatus(Status.SUSPENDED);
                case ON_LEAVE -> student.getUser().setStatus(Status.ON_LEAVE);
                case EXPELLED -> student.getUser().setStatus(Status.INACTIVE); // Optional mapping for expelled
            }
        }

        // Save student (cascades to user due to cascade = ALL)
        return studentRepository.save(student);
    }



    @Override
    @Transactional
    public Student updateEnrollmentStatus(Long studentId, EnrollmentStatus status) {
        Student student = findStudentOrThrow(studentId);
        student.setEnrollmentStatus(status);
        return studentRepository.save(student);
    }

    @Override
    @Transactional
    public Student activateStudentUser(Long studentId) {
        Student student = findStudentOrThrow(studentId);
        if (student.getUser() == null) throw new IllegalStateException("User missing for student: " + student.getRollNumber());
        student.getUser().setStatus(Status.ACTIVE);
        userRepository.save(student.getUser());
        log.info("User activated for student: {}", student.getRollNumber());
        return student;
    }

    // ------------------- FETCH -------------------
    private Student findStudentOrThrow(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with ID: " + studentId));
    }

    @Override
    public Student getStudentById(Long id) {
        return findStudentOrThrow(id);
    }

    @Override
    public Student getByRollNumber(String rollNumber) {
        return studentRepository.findByRollNumber(rollNumber)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with roll number: " + rollNumber));
    }

    @Override
    public Student getByAdmissionNumber(String admissionNumber) {
        return studentRepository.findByAdmissionNumber(admissionNumber)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with admission number: " + admissionNumber));
    }

    // ------------------- FILTERS WITH PAGINATION -------------------
    @Override
    public Page<Student> getByDepartment(Long departmentId, Pageable pageable) {
        return studentRepository.findByDepartment_DepartmentId(departmentId, pageable);
    }

    @Override
    public Page<Student> getByCourse(Long courseId, Pageable pageable) {
        return studentRepository.findByCourse_CourseId(courseId, pageable);
    }

    @Override
    public Page<Student> getByStatus(StudentStatus status, Pageable pageable) {
        return studentRepository.findByStatus(status, pageable);
    }

    @Override
    public Page<Student> getByEnrollmentStatus(EnrollmentStatus status, Pageable pageable) {
        return studentRepository.findByEnrollmentStatus(status, pageable);
    }

    @Override
    public Page<Student> filterStudents(Long departmentId, Long courseId, StudentStatus status, Pageable pageable) {
        return studentRepository.findByDepartment_DepartmentIdAndCourse_CourseIdAndStatus(departmentId, courseId, status, pageable);
    }

    // ------------------- QR -------------------
    private void generateQrAsync(Student student) {
        qrExecutor.execute(() -> {
            try {
                byte[] qrBytes = qrService.generateQrForEntity("STUDENT", student.getRollNumber());
                String url = qrFileStorageService.storeStudentQr(student.getRollNumber(), qrBytes);
                student.setQrUrl(url);
                studentRepository.save(student);
                log.info("QR generated for student: {}", student.getRollNumber());
            } catch (Exception e) {
                log.error("QR generation failed for student: {}", student.getRollNumber(), e);
            }
        });
    }

    private void regenerateQrAsync(Student student) {
        generateQrAsync(student);
    }

    private void deleteStudentQr(Student student) {
        qrFileStorageService.deleteStudentQr(student.getRollNumber());
    }

    // ------------------- PHOTO -------------------
    @Override
    public String uploadStudentPhoto(Long studentId, MultipartFile imageFile) {
        Student student = findStudentOrThrow(studentId);
        return uploadStudentPhoto(student, imageFile);
    }

    public String uploadStudentPhoto(Student student, MultipartFile imageFile) {
        try {
            String url = photoStorageService.storeStudentPhoto(student.getRollNumber(), imageFile.getBytes());
            student.setPhotoUrl(url);
            studentRepository.save(student);
            log.info("Photo uploaded for student: {}", student.getRollNumber());
            return url;
        } catch (IOException e) {
            log.error("Photo upload failed for student: {}", student.getRollNumber(), e);
            throw new FileStorageException("Photo upload failed for student: " + student.getRollNumber(), e);
        }
    }

    private boolean deleteStudentPhoto(Student student) {
        return photoStorageService.deleteStudentPhoto(student.getRollNumber());
    }

    @Async("photoExecutor")
    public void uploadPhotosBulkAsync(Map<Long, byte[]> studentPhotoMap) {
        List<Student> students = studentRepository.findAllById(studentPhotoMap.keySet());
        Map<Long, Student> studentMap = students.stream().collect(Collectors.toMap(Student::getStudentId, s -> s));

        studentPhotoMap.forEach((id, bytes) -> {
            Student student = studentMap.get(id);
            if (student != null) {
            	try {
            	    String url = photoStorageService.storeStudentPhoto(student.getRollNumber(), bytes);
            	    student.setPhotoUrl(url);
            	    studentRepository.save(student);
            	    log.info("Bulk photo saved for student: {}", student.getRollNumber());
            	} catch (IOException ex) {
            	    log.error("Failed saving bulk photo for student ID={}", id, ex);
            	}

            }
        });
    }

    // ------------------- BULK CSV -------------------
    @Transactional
    public void uploadStudentsBulk(MultipartFile file, String imageFolderPath) throws IOException {
        Map<String, Department> departmentMap = departmentRepository.findAll().stream()
                .collect(Collectors.toMap(Department::getShortCode, d -> d));
        Map<String, Course> courseMap = courseRepository.findAll().stream()
                .collect(Collectors.toMap(Course::getCourseCode, c -> c));

        List<Student> students = StudentCsvParser.parseStudents(file.getInputStream(), departmentMap, courseMap);
        List<Student> savedStudents = studentRepository.saveAll(students);
        log.info("Bulk upload completed: {} students saved", savedStudents.size());

        Map<Long, byte[]> photoMap = new HashMap<>();
        for (Student s : savedStudents) {
            String roll = s.getRollNumber().trim();
            File jpg = new File(imageFolderPath + File.separator + roll + ".jpg");
            File jpeg = new File(imageFolderPath + File.separator + roll + ".jpeg");
            File png = new File(imageFolderPath + File.separator + roll + ".png");
            File photoFile = jpg.exists() ? jpg : (jpeg.exists() ? jpeg : (png.exists() ? png : null));

            if (photoFile != null) {
                photoMap.put(s.getStudentId(), Files.readAllBytes(photoFile.toPath()));
            }
        }

        savedStudents.forEach(student -> studentBulkExecutor
                .execute(() -> processStudentBulkAsync(student, photoMap.get(student.getStudentId()))));
    }

    private void processStudentBulkAsync(Student student, byte[] photoBytes) {
        try {
            User user = commonUserService.createUser(student.getRollNumber(), "STUDENT", student.getStudentId(),
                    "stud@123", null);
            user.setContact(student.getContact());
            student.setUser(user);
            studentRepository.save(student);
            

            generateQrAsync(student);

            if (photoBytes != null) {
                MultipartFile multipartFile = new MockMultipartFile(student.getRollNumber() + ".png",
                        student.getRollNumber() + ".jpg", "image/jpeg", photoBytes);
                uploadStudentPhoto(student, multipartFile);
            }
        } catch (Exception e) {
            log.error("Failed async processing for student: {}", student.getRollNumber(), e);
        }
    }
}
