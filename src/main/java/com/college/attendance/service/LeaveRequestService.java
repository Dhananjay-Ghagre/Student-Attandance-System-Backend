package com.college.attendance.service;

import com.college.attendance.dto.LeaveRequestDTO;
import com.college.attendance.dto.LeaveReviewDTO;
import com.college.attendance.entity.Course;
import com.college.attendance.entity.LeaveRequest;
import com.college.attendance.entity.User;
import com.college.attendance.repository.CourseRepository;
import com.college.attendance.repository.LeaveRequestRepository;
import com.college.attendance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeaveRequestService {
    
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    public LeaveRequest submitLeaveRequest(String username, LeaveRequestDTO dto) {
        User student = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Student not found"));
        
        Course course = courseRepository.findById(dto.getCourseId())
            .orElseThrow(() -> new RuntimeException("Course not found"));
        
        LeaveRequest leaveRequest = new LeaveRequest(student, course, dto.getStartDate(), dto.getEndDate(), dto.getReason());
        return leaveRequestRepository.save(leaveRequest);
    }
    
    public List<LeaveRequest> getStudentLeaveRequests(String username) {
        User student = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Student not found"));
        
        return leaveRequestRepository.findByStudentIdOrderByCreatedAtDesc(student.getId());
    }
    
    public List<LeaveRequest> getTeacherLeaveRequests(String username) {
        User teacher = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Teacher not found"));
        
        System.out.println("Getting leave requests for teacher ID: " + teacher.getId() + ", username: " + username);
        List<LeaveRequest> requests = leaveRequestRepository.findByTeacherIdOrderByCreatedAtDesc(teacher.getId());
        System.out.println("Found " + requests.size() + " leave requests");
        
        return requests;
    }
    
    public List<LeaveRequest> getPendingLeaveRequests(String username) {
        User teacher = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Teacher not found"));
        
        System.out.println("Getting pending leave requests for teacher ID: " + teacher.getId() + ", username: " + username);
        List<LeaveRequest> requests = leaveRequestRepository.findPendingByTeacherId(teacher.getId());
        System.out.println("Found " + requests.size() + " pending leave requests");
        
        return requests;
    }
    
    @Autowired
    private AttendanceService attendanceService;
    
    public LeaveRequest reviewLeaveRequest(Long requestId, LeaveReviewDTO dto) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Leave request not found"));
        
        leaveRequest.setStatus(dto.getStatus());
        leaveRequest.setTeacherComment(dto.getTeacherComment());
        leaveRequest.setReviewedAt(LocalDateTime.now());
        
        LeaveRequest savedRequest = leaveRequestRepository.save(leaveRequest);
        
        // Automatically create attendance records based on leave status
        createAttendanceForLeaveRequest(savedRequest);
        
        return savedRequest;
    }
    
    private void createAttendanceForLeaveRequest(LeaveRequest leaveRequest) {
        // Only create attendance if leave is approved or rejected (not pending)
        if ("PENDING".equals(leaveRequest.getStatus())) {
            return;
        }
        
        boolean isApproved = "APPROVED".equals(leaveRequest.getStatus());
        
        // Create attendance records for each day in the leave period
        java.time.LocalDate currentDate = leaveRequest.getStartDate();
        while (!currentDate.isAfter(leaveRequest.getEndDate())) {
            try {
                if (isApproved) {
                    // Approved leave: mark as present and on leave
                    attendanceService.markAttendanceWithLeave(
                        leaveRequest.getStudent().getId(),
                        leaveRequest.getCourse().getId(),
                        currentDate,
                        true,
                        true
                    );
                } else {
                    // Rejected leave: mark as absent
                    attendanceService.markAttendance(
                        leaveRequest.getStudent().getId(),
                        leaveRequest.getCourse().getId(),
                        currentDate,
                        false
                    );
                }
            } catch (Exception e) {
                System.err.println("Error creating attendance for date " + currentDate + ": " + e.getMessage());
            }
            currentDate = currentDate.plusDays(1);
        }
    }
    
    public List<LeaveRequest> getTodaysLeavesForTeacher(String username, java.time.LocalDate date) {
        User teacher = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Teacher not found"));
        
        return leaveRequestRepository.findTodaysLeavesByTeacherId(teacher.getId(), date);
    }
    
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAllOrderByCreatedAtDesc();
    }
    
    public List<LeaveRequest> getStudentApprovedLeaves(Long studentId, java.time.LocalDate date) {
        return leaveRequestRepository.findAll().stream()
            .filter(leave -> leave.getStudent().getId().equals(studentId))
            .filter(leave -> "APPROVED".equals(leave.getStatus()))
            .filter(leave -> !date.isBefore(leave.getStartDate()) && !date.isAfter(leave.getEndDate()))
            .toList();
    }
}