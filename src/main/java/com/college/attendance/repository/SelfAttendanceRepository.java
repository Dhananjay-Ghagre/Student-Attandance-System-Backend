package com.college.attendance.repository;

import com.college.attendance.entity.SelfAttendance;
import com.college.attendance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SelfAttendanceRepository extends JpaRepository<SelfAttendance, Long> {
    Optional<SelfAttendance> findByStudentAndDate(User student, LocalDate date);
    List<SelfAttendance> findByStudent(User student);
    boolean existsByStudentAndDate(User student, LocalDate date);
}