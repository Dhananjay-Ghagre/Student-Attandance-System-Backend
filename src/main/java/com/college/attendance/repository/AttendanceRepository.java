package com.college.attendance.repository;

import com.college.attendance.entity.Attendance;
import com.college.attendance.entity.AttendanceStatus;
import com.college.attendance.entity.Course;
import com.college.attendance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudent(User student);
    List<Attendance> findByCourse(Course course);
    List<Attendance> findByStudentAndCourse(User student, Course course);
    Optional<Attendance> findByStudentAndCourseAndDate(User student, Course course, LocalDate date);
    Optional<Attendance> findByStudentAndDate(User student, LocalDate date);
    List<Attendance> findByDateAndAttendanceStatusNot(LocalDate date, AttendanceStatus status);
    Long countByCourseAndDateAndPresent(Course course, LocalDate date, boolean present);
    
    @Query("SELECT a FROM Attendance a WHERE " +
           "(:startDate IS NULL OR a.date >= :startDate) AND " +
           "(:endDate IS NULL OR a.date <= :endDate) AND " +
           "(:courseId IS NULL OR a.course.id = :courseId) AND " +
           "(:studentId IS NULL OR a.student.id = :studentId) AND " +
           "(:batch IS NULL OR a.student.batch = :batch) AND " +
           "(:year IS NULL OR a.student.yearOfStudy = :year)")
    List<Attendance> findFilteredAttendance(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("courseId") Long courseId,
        @Param("studentId") Long studentId,
        @Param("batch") String batch,
        @Param("year") String year
    );
}