package com.college.attendance.controller;

import com.college.attendance.dto.AttendanceReportDTO;
import com.college.attendance.dto.CourseEnrollmentDTO;
import com.college.attendance.entity.Course;
import com.college.attendance.entity.User;
import com.college.attendance.repository.CourseRepository;
import com.college.attendance.service.AdminReportService;
import com.college.attendance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class  AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private AdminReportService adminReportService;
    
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }
    
    @GetMapping("/teachers")
    public ResponseEntity<List<User>> getTeachers() {
        return ResponseEntity.ok(userService.findByRole(User.Role.TEACHER));
    }
    
    @GetMapping("/students")
    public ResponseEntity<List<User>> getStudents() {
        return ResponseEntity.ok(userService.findByRole(User.Role.STUDENT));
    }
    
    @PostMapping("/courses")
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        Course savedCourse = courseRepository.save(course);
        return ResponseEntity.ok(savedCourse);
    }
    
    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseRepository.findAll());
    }
    
    @GetMapping("/reports/total-students")
    public ResponseEntity<Map<String, Long>> getTotalStudents() {
        Long total = adminReportService.getTotalStudents();
        return ResponseEntity.ok(Map.of("totalStudents", total));
    }
    
    @GetMapping("/reports/course-enrollments")
    public ResponseEntity<List<CourseEnrollmentDTO>> getCourseEnrollments() {
        return ResponseEntity.ok(adminReportService.getCourseEnrollments());
    }
    
    @GetMapping("/reports/attendance-by-date")
    public ResponseEntity<List<AttendanceReportDTO>> getAttendanceByDate(@RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        return ResponseEntity.ok(adminReportService.getAttendanceReportByDate(localDate));
    }
    
    @GetMapping("/reports/attendance/filter")
    public ResponseEntity<?> getFilteredAttendance(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) String batch,
            @RequestParam(required = false) String year) {
        try {
            return ResponseEntity.ok(adminReportService.getFilteredAttendance(startDate, endDate, courseId, studentId, batch, year));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/assign-teacher")
    public ResponseEntity<?> assignTeacherToCourse(@RequestBody Map<String, Long> request) {
        try {
            Long courseId = request.get("courseId");
            Long teacherId = request.get("teacherId");
            adminReportService.assignTeacherToCourse(courseId, teacherId);
            return ResponseEntity.ok(Map.of("message", "Teacher assigned successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/reports/attendance-by-date-status")
    public ResponseEntity<?> getAttendanceByDateAndStatus(
            @RequestParam String date,
            @RequestParam(required = false) Boolean present) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            return ResponseEntity.ok(adminReportService.getAttendanceByDate(localDate, present));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/approved-leaves")
    public ResponseEntity<?> getApprovedLeaves(@RequestParam String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            return ResponseEntity.ok(adminReportService.getApprovedLeaves(localDate));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/test-auth")
    public ResponseEntity<?> testAuth() {
        return ResponseEntity.ok(Map.of("message", "Admin auth working"));
    }
}