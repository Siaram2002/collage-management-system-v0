package com.cms.students.services;

import com.cms.students.dto.StudentFilterRequest;
import com.cms.students.dto.StudentProfileDTO;
import com.cms.students.mappers.StudentMapper;
import com.cms.students.models.Student;
import com.cms.students.repository.StudentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentListingService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public Page<StudentProfileDTO> getStudents(StudentFilterRequest filter) {

        // Normalize keyword
        String keyword = (filter.getKeyword() == null || filter.getKeyword().trim().isEmpty())
                ? null
                : filter.getKeyword().trim();

        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by(Sort.Direction.fromString(filter.getSortDir()), filter.getSortBy())
        );

        Page<Student> students = studentRepository.searchStudents(
                filter.getDepartmentId(),
                filter.getCourseId(),
                filter.getStatus(),
                keyword,
                pageable
        );

        return students.map(studentMapper::toProfileDTO);
    }
}
