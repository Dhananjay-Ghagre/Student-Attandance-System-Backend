package com.college.attendance.controller;

import com.college.attendance.dto.AttendanceRequest;
import com.college.attendance.dto.LeaveReviewDTO;
import com.college.attendance.entity.Attendance;
import com.college.attendance.entity.Course;
import com.college.attendance.entity.LeaveRequest;
import com.college.attendance.entity.User;
import com.college.attendance.repository.CourseRepository;
import com.college.attendance.repository.LeaveRequestRepository;
import com.college.attendance.service.AttendanceService;
import com.college.attendance.service.LeaveRequestService;
import com.college.attendance.service.TeacherAccessService;
import com.college.attendance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teacher")
@CrossOrigin(origins = "http://localhost:3000")
public class TeacherController {
    
    @Autowired
    private AttendanceService attendanceService;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TeacherAccessService teacherAccessService;
    
    @Autowired
    private LeaveRequestService leaveRequestService;
    
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;
    
    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getMyCourses(Authentication auth) {
        return ResponseEntity.ok(teacherAccessService.getTeacherCourses(auth.getName()));
    }
    
    @PostMapping("/attendance")
    public ResponseEntity<?> markAttendance(@RequestBody AttendanceRequest request, Authentication auth) {
        try {
            // Validate teacher can access this course
            if (!teacherAccessService.canAccessCourse(auth.getName(), request.getCourseId())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Access denied: You are not assigned to this course"));
            }
            
            // Validate teacher can access this student
            if (!teacherAccessService.canAccessStudent(auth.getName(), request.getStudentId())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Access denied: Student not enrolled in your courses"));
            }
            
            Attendance attendance = attendanceService.markAttendance(
                request.getStudentId(), 
                request.getCourseId(), 
                request.getDate(), 
                request.isPresent()
            );
            
            return ResponseEntity.ok(Map.of(
                "message", "Attendance marked successfully",
                "attendance", attendance
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to mark attendance: " + e.getMessage()));
        }
    }
    
    @GetMapping("/attendance/course/{courseId}")
    public ResponseEntity<?> getCourseAttendance(@PathVariable Long courseId, Authentication auth) {
        // Validate teacher can access this course
        if (!teacherAccessService.canAccessCourse(auth.getName(), courseId)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Access denied: You are not assigned to this course"));
        }
        
        return ResponseEntity.ok(attendanceService.getCourseAttendance(courseId));
    }
    
    @GetMapping("/attendance/filter")
    public ResponseEntity<?> getFilteredAttendance(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long studentId,
            Authentication auth) {
        
        try {
            List<Attendance> attendance = attendanceService.getFilteredAttendance(
                auth.getName(), date, startDate, endDate, courseId, studentId, teacherAccessService);
            return ResponseEntity.ok(attendance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/attendance/student-history")
    public ResponseEntity<?> getStudentAttendanceHistory(
            @RequestParam Long studentId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            Authentication auth) {
        
        try {
            if (!teacherAccessService.canAccessStudent(auth.getName(), studentId)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Access denied: Student not in your courses"));
            }
            
            List<Attendance> attendance = attendanceService.getStudentAttendanceHistory(
                studentId, startDate, endDate);
            return ResponseEntity.ok(attendance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/students")
    public ResponseEntity<List<User>> getStudents(Authentication auth) {
        return ResponseEntity.ok(teacherAccessService.getStudentsForTeacherCourses(auth.getName()));
    }
    
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication auth) {
        try {
            User user = userService.getUserByUsername(auth.getName());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Profile not found"));
        }
    }
    
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody User profileData, Authentication auth) {
        try {
            User updatedUser = userService.updateProfile(auth.getName(), profileData);
            return ResponseEntity.ok(Map.of("message", "Profile updated successfully", "user", updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/leave-requests")
    public ResponseEntity<?> getLeaveRequests(Authentication auth) {
        try {
            List<LeaveRequest> requests = leaveRequestService.getTeacherLeaveRequests(auth.getName());
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            System.err.println("Error fetching leave requests: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/leave-requests/pending")
    public ResponseEntity<?> getPendingLeaveRequests(Authentication auth) {
        try {
            List<LeaveRequest> pendingRequests = leaveRequestService.getPendingLeaveRequests(auth.getName());
            return ResponseEntity.ok(pendingRequests);
        } catch (Exception e) {
            System.err.println("Error fetching pending leave requests: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/leave-requests/{requestId}/review")
    public ResponseEntity<?> reviewLeaveRequest(@PathVariable Long requestId, @RequestBody LeaveReviewDTO dto, Authentication auth) {
        try {
            LeaveRequest updatedRequest = leaveRequestService.reviewLeaveRequest(requestId, dto);
            return ResponseEntity.ok(Map.of("message", "Leave request reviewed successfully", "request", updatedRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/attendance/today")
    public ResponseEntity<?> getTodaysAttendance(
            @RequestParam String date,
            @RequestParam boolean present,
            Authentication auth) {
        try {
            List<Attendance> attendance = attendanceService.getTodaysAttendanceForTeacher(
                auth.getName(), date, present, teacherAccessService);
            return ResponseEntity.ok(attendance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/leaves/today")
    public ResponseEntity<?> getTodaysLeaves(
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date,
            Authentication auth) {
        try {
            List<LeaveRequest> leaves = leaveRequestService.getTodaysLeavesForTeacher(
                auth.getName(), date);
            return ResponseEntity.ok(leaves);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch today's leaves: " + e.getMessage()));
        }
    }
    
    @GetMapping("/debug/all-leaves")
    public ResponseEntity<?> getAllLeaves() {
        try {
            List<LeaveRequest> allLeaves = leaveRequestRepository.findAllOrderByCreatedAtDesc();
            System.out.println("=== DEBUG: All Leave Requests ===");
            System.out.println("Total leave requests in database: " + allLeaves.size());
            for (LeaveRequest lr : allLeaves) {
                System.out.println("ID: " + lr.getId() + 
                    ", Student: " + (lr.getStudent() != null ? lr.getStudent().getFullName() : "null") + 
                    ", Course: " + (lr.getCourse() != null ? lr.getCourse().getName() : "null") +
                    ", Teacher: " + (lr.getCourse() != null && lr.getCourse().getTeacher() != null ? lr.getCourse().getTeacher().getUsername() : "null") +
                    ", Status: " + lr.getStatus());
            }
            return ResponseEntity.ok(allLeaves);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}