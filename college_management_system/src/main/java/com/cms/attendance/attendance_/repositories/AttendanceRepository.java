package com.cms.attendance.attendance_.repositories;


import com.cms.attendance.attendance_.module.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    boolean existsByLecture_LectureIdAndStudent_StudentId(
            Long lectureId,
            Long studentId
    );

    // Find by date range
    @Query("SELECT a FROM Attendance a WHERE a.lecture.lectureDate BETWEEN :startDate AND :endDate ORDER BY a.lecture.lectureDate DESC, a.scanTime DESC")
    List<Attendance> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Find by department and date range
    @Query("SELECT a FROM Attendance a WHERE a.lecture.departmentClass.department.departmentId = :departmentId " +
           "AND a.lecture.lectureDate BETWEEN :startDate AND :endDate ORDER BY a.lecture.lectureDate DESC, a.scanTime DESC")
    List<Attendance> findByDepartmentAndDateRange(@Param("departmentId") Long departmentId, 
                                                   @Param("startDate") LocalDate startDate, 
                                                   @Param("endDate") LocalDate endDate);

    // Find by department class and date range
    @Query("SELECT a FROM Attendance a WHERE a.lecture.departmentClass.departmentClassId = :departmentClassId " +
           "AND a.lecture.lectureDate BETWEEN :startDate AND :endDate ORDER BY a.lecture.lectureDate DESC")
    List<Attendance> findByDepartmentClassAndDateRange(@Param("departmentClassId") Long departmentClassId,
                                                        @Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);

    // Count attendance by student and date range
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.studentId = :studentId " +
           "AND a.lecture.lectureDate BETWEEN :startDate AND :endDate")
    Long countByStudentAndDateRange(@Param("studentId") Long studentId,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

    // Find by student
    @Query("SELECT a FROM Attendance a WHERE a.student.studentId = :studentId ORDER BY a.lecture.lectureDate DESC, a.scanTime DESC")
    List<Attendance> findByStudentId(@Param("studentId") Long studentId);
}

