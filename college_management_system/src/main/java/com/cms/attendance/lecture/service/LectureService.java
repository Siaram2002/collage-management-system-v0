package com.cms.attendance.lecture.service;


import com.cms.attendance.faculty.module.Faculty;
import com.cms.attendance.faculty.repositories.FacultyRepository;
import com.cms.attendance.lecture.dto.LectureCreateRequestDTO;
import com.cms.attendance.lecture.dto.LectureResponseDTO;
import com.cms.attendance.lecture.module.Lecture;
import com.cms.attendance.lecture.repositories.LectureRepository;
import com.cms.college.models.DepartmentClass;
import com.cms.college.reporitories.DepartmentClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LectureService {

    private final LectureRepository lectureRepository;
    private final DepartmentClassRepository departmentClassRepository;
    private final FacultyRepository facultyRepository;

    // CREATE LECTURE
    public LectureResponseDTO createLecture(LectureCreateRequestDTO request) {

        DepartmentClass departmentClass = departmentClassRepository.findById(
                request.getDepartmentClassId()
        ).orElseThrow(() -> new RuntimeException("Department class not found"));

        Faculty faculty = facultyRepository.findById(
                request.getFacultyId()
        ).orElseThrow(() -> new RuntimeException("Faculty not found"));

        Lecture lecture = Lecture.builder()
                .departmentClass(departmentClass)
                .faculty(faculty)
                .subjectName(request.getSubjectName())
                .lectureDate(request.getLectureDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .lectureType(request.getLectureType())
                .room(request.getRoom())
                .semester(request.getSemester())
                .academicYear(request.getAcademicYear())
                .status(request.getStatus())
                .build();

        Lecture saved = lectureRepository.save(lecture);

        return mapToResponse(saved);
    }

    // GET BY CLASS
    public List<LectureResponseDTO> getLecturesByDepartmentClass(Long departmentClassId) {

        return lectureRepository.findByDepartmentClass_DepartmentClassId(departmentClassId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // GET BY CLASS + DATE
    public List<LectureResponseDTO> getLecturesByClassAndDate(
            Long departmentClassId,
            java.time.LocalDate date
    ) {
        return lectureRepository
                .findByDepartmentClass_DepartmentClassIdAndLectureDate(departmentClassId, date)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private LectureResponseDTO mapToResponse(Lecture lecture) {

        return LectureResponseDTO.builder()
                .lectureId(lecture.getLectureId())
                .departmentClassId(
                        lecture.getDepartmentClass().getDepartmentClassId()
                )
                .facultyId(
                        lecture.getFaculty().getFacultyId()
                )
                .subjectName(lecture.getSubjectName())
                .lectureDate(lecture.getLectureDate())
                .startTime(lecture.getStartTime())
                .endTime(lecture.getEndTime())
                .lectureType(lecture.getLectureType())
                .room(lecture.getRoom())
                .semester(lecture.getSemester())
                .academicYear(lecture.getAcademicYear())
                .status(lecture.getStatus())
                .createdAt(lecture.getCreatedAt())
                .build();
    }
}
