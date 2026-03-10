package com.college.attendance.controller;

import com.college.attendance.dto.LeaveRequestDTO;
import com.college.attendance.entity.Attendance;
import com.college.attendance.entity.Course;
import com.college.attendance.entity.CourseEnrollment;
import com.college.attendance.entity.LeaveRequest;
import com.college.attendance.entity.SelfAttendance;
import com.college.attendance.entity.User;
import com.college.attendance.repository.CourseRepository;
import com.college.attendance.repository.CourseEnrollmentRepository;
import com.college.attendance.repository.CourseEnrollmentRepository;
import com.college.attendance.service.AttendanceService;
import com.college.attendance.service.LeaveRequestService;
import com.college.attendance.service.SelfAttendanceService;
import com.college.attendance.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "http://localhost:3000")
public class StudentController {
    
    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);
    
    @Autowired
    private AttendanceService attendanceService;
    
    @Autowired
    private SelfAttendanceService selfAttendanceService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private LeaveRequestService leaveRequestService;
    
    @Autowired
    private CourseEnrollmentRepository courseEnrollmentRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @GetMapping("/attendance")
    public ResponseEntity<List<Attendance>> getMyAttendance(Authentication auth) {
        User student = userService.findByUsername(auth.getName()).orElseThrow();
        return ResponseEntity.ok(attendanceService.getStudentAttendance(student.getId()));
    }
    
    @GetMapping("/attendance/course/{courseId}")
    public ResponseEntity<List<Attendance>> getMyCourseAttendance(@PathVariable Long courseId, Authentication auth) {
        User student = userService.findByUsername(auth.getName()).orElseThrow();
        return ResponseEntity.ok(attendanceService.getStudentCourseAttendance(student.getId(), courseId));
    }
    
    @PostMapping("/mark-attendance")
    public ResponseEntity<?> markSelfAttendance(@RequestBody Map<String, Object> attendanceData, Authentication auth) {
        try {
            logger.debug("Received attendance data: {} for user: {}", attendanceData, auth.getName());
            
            Long courseId = Long.valueOf(attendanceData.get("courseId").toString());
            Boolean present = (Boolean) attendanceData.get("present");
            String date = (String) attendanceData.get("date");
            
            // Extract location data if provided
            Attendance attendance;
            if (attendanceData.containsKey("location")) {
                Map<String, Object> locationData = (Map<String, Object>) attendanceData.get("location");
                Double latitude = Double.valueOf(locationData.get("latitude").toString());
                Double longitude = Double.valueOf(locationData.get("longitude").toString());
                logger.debug("Location data for user {}: lat={}, lng={}", auth.getName(), latitude, longitude);
                
                attendance = attendanceService.markStudentAttendanceWithLocation(
                    auth.getName(), courseId, date, present, latitude, longitude
                );
            } else {
                attendance = attendanceService.markStudentAttendance(
                    auth.getName(), courseId, date, present
                );
            }
            
            logger.info("Attendance marked for user: {} - courseId: {}, present: {}, date: {}", 
                       auth.getName(), courseId, present, date);
            
            return ResponseEntity.ok(Map.of("message", "Attendance marked successfully", "attendance", attendance));
        } catch (RuntimeException e) {
            logger.error("Error marking attendance for user {}: {}", auth.getName(), e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/attendance-status")
    public ResponseEntity<Map<String, Boolean>> getAttendanceStatus(Authentication auth) {
        boolean hasMarked = selfAttendanceService.hasMarkedToday(auth.getName());
        return ResponseEntity.ok(Map.of("hasMarkedToday", hasMarked));
    }
    
    @GetMapping("/self-attendance")
    public ResponseEntity<List<SelfAttendance>> getSelfAttendance(Authentication auth) {
        return ResponseEntity.ok(selfAttendanceService.getStudentSelfAttendance(auth.getName()));
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
    

    
    @PostMapping("/leave-request")
    public ResponseEntity<?> submitLeaveRequest(@RequestBody LeaveRequestDTO dto, Authentication auth) {
        try {
            LeaveRequest leaveRequest = leaveRequestService.submitLeaveRequest(auth.getName(), dto);
            return ResponseEntity.ok(Map.of("message", "Leave request submitted successfully", "request", leaveRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/leave-requests")
    public ResponseEntity<List<LeaveRequest>> getMyLeaveRequests(Authentication auth) {
        return ResponseEntity.ok(leaveRequestService.getStudentLeaveRequests(auth.getName()));
    }
    
    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getMyCourses(Authentication auth) {
        User student = userService.findByUsername(auth.getName()).orElseThrow();
        List<CourseEnrollment> enrollments = courseEnrollmentRepository.findByStudent(student);
        List<Course> courses = enrollments.stream().map(CourseEnrollment::getCourse).toList();
        return ResponseEntity.ok(courses);
    }
    
    @GetMapping("/approved-leaves")
    public ResponseEntity<?> getApprovedLeaves(@RequestParam String date, Authentication auth) {
        try {
            User student = userService.findByUsername(auth.getName()).orElseThrow();
            java.time.LocalDate localDate = java.time.LocalDate.parse(date);
            return ResponseEntity.ok(leaveRequestService.getStudentApprovedLeaves(student.getId(), localDate));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/complete-profile")
    public ResponseEntity<?> completeProfile(@RequestBody Map<String, String> profileData, Authentication auth) {
        try {
            User user = userService.findByUsername(auth.getName()).orElseThrow();
            user.setBatch(profileData.get("batch"));
            user.setYearOfStudy(profileData.get("yearOfStudy"));
            user.setCourse(profileData.get("course"));
            user.setProfileCompleted(true);
            userService.saveUser(user);
            
            // Find course by name and enroll student
            String courseName = profileData.get("course");
            if (courseName != null && !courseName.trim().isEmpty()) {
                try {
                    Course course = courseRepository.findAll().stream()
                        .filter(c -> c.getName().equals(courseName))
                        .findFirst()
                        .orElse(null);
                    
                    if (course != null) {
                        // Check if already enrolled
                        boolean alreadyEnrolled = courseEnrollmentRepository.findByStudent(user)
                            .stream().anyMatch(enrollment -> enrollment.getCourse().getId().equals(course.getId()));
                        
                        if (!alreadyEnrolled) {
                            CourseEnrollment enrollment = new CourseEnrollment();
                            enrollment.setStudent(user);
                            enrollment.setCourse(course);
                            courseEnrollmentRepository.save(enrollment);
                        }
                    }
                } catch (Exception enrollmentError) {
                    // Log error but don't fail the profile completion
                    System.err.println("Error creating course enrollment: " + enrollmentError.getMessage());
                }
            }
            
            return ResponseEntity.ok(Map.of("message", "Profile completed successfully", "user", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/checkin")
    public ResponseEntity<?> checkIn(@RequestBody Map<String, Object> locationData, Authentication auth) {
        try {
            Double latitude = Double.valueOf(locationData.get("latitude").toString());
            Double longitude = Double.valueOf(locationData.get("longitude").toString());
            
            logger.info("Check-in attempt by user: {} at location: [{}, {}]", auth.getName(), latitude, longitude);
            
            Attendance attendance = attendanceService.checkIn(auth.getName(), latitude, longitude);
            
            logger.info("Successful check-in for user: {} at {}", auth.getName(), attendance.getCheckInTime());
            return ResponseEntity.ok(Map.of("message", "Checked in successfully", "attendance", attendance));
        } catch (RuntimeException e) {
            logger.warn("Check-in failed for user: {} - {}", auth.getName(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/checkout")
    public ResponseEntity<?> checkOut(@RequestBody Map<String, Object> locationData, Authentication auth) {
        try {
            Double latitude = Double.valueOf(locationData.get("latitude").toString());
            Double longitude = Double.valueOf(locationData.get("longitude").toString());
            
            logger.info("Check-out attempt by user: {} at location: [{}, {}]", auth.getName(), latitude, longitude);
            
            Attendance attendance = attendanceService.checkOut(auth.getName(), latitude, longitude);
            
            logger.info("Successful check-out for user: {} at {}", auth.getName(), attendance.getCheckOutTime());
            return ResponseEntity.ok(Map.of("message", "Checked out successfully", "attendance", attendance));
        } catch (RuntimeException e) {
            logger.warn("Check-out failed for user: {} - {}", auth.getName(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/today-status")
    public ResponseEntity<?> getTodayAttendanceStatus(Authentication auth) {
        try {
            Attendance attendance = attendanceService.getTodayAttendanceStatus(auth.getName());
            if (attendance == null) {
                return ResponseEntity.ok(Map.of(
                    "hasCheckedIn", false,
                    "hasCheckedOut", false,
                    "status", "NOT_STARTED"
                ));
            }
            
            return ResponseEntity.ok(Map.of(
                "hasCheckedIn", attendance.getCheckInTime() != null,
                "hasCheckedOut", attendance.getCheckOutTime() != null,
                "checkInTime", attendance.getCheckInTime(),
                "checkOutTime", attendance.getCheckOutTime(),
                "status", attendance.getAttendanceStatus().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}