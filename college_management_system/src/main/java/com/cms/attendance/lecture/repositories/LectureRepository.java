package com.cms.attendance.lecture.repositories;

import com.cms.attendance.lecture.module.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {

    List<Lecture> findByDepartmentClass_DepartmentClassId(Long departmentClassId);

    List<Lecture> findByDepartmentClass_DepartmentClassIdAndLectureDate(
            Long departmentClassId,
            LocalDate lectureDate
    );

    @Query("SELECT COUNT(l) FROM Lecture l WHERE l.departmentClass.departmentClassId = :departmentClassId " +
           "AND l.lectureDate BETWEEN :startDate AND :endDate")
    Long countByDepartmentClass_DepartmentClassIdAndLectureDateBetween(
            @Param("departmentClassId") Long departmentClassId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
