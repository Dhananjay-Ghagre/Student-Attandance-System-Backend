package com.college.attendance.repository;

import com.college.attendance.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    
    List<LeaveRequest> findByStudentIdOrderByCreatedAtDesc(Long studentId);
    
    @Query("SELECT lr FROM LeaveRequest lr JOIN lr.course c WHERE c.teacher.id = :teacherId ORDER BY lr.createdAt DESC")
    List<LeaveRequest> findByTeacherIdOrderByCreatedAtDesc(@Param("teacherId") Long teacherId);
    
    @Query("SELECT lr FROM LeaveRequest lr JOIN lr.course c WHERE c.teacher.id = :teacherId AND lr.status = 'PENDING' ORDER BY lr.createdAt DESC")
    List<LeaveRequest> findPendingByTeacherId(@Param("teacherId") Long teacherId);
    
    @Query("SELECT lr FROM LeaveRequest lr JOIN lr.course c WHERE c.teacher.id = :teacherId AND :date BETWEEN lr.startDate AND lr.endDate ORDER BY lr.createdAt DESC")
    List<LeaveRequest> findTodaysLeavesByTeacherId(@Param("teacherId") Long teacherId, @Param("date") java.time.LocalDate date);
    
    @Query("SELECT lr FROM LeaveRequest lr ORDER BY lr.createdAt DESC")
    List<LeaveRequest> findAllOrderByCreatedAtDesc();
    
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.status = 'APPROVED' AND :date BETWEEN lr.startDate AND lr.endDate ORDER BY lr.createdAt DESC")
    List<LeaveRequest> findApprovedLeavesForDate(@Param("date") java.time.LocalDate date);
}