package com.college.attendance.service;

import com.college.attendance.entity.Attendance;
import com.college.attendance.entity.AttendanceStatus;
import com.college.attendance.entity.Course;
import com.college.attendance.entity.User;
import com.college.attendance.repository.AttendanceRepository;
import com.college.attendance.repository.CourseRepository;
import com.college.attendance.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.college.attendance.service.TeacherAccessService;

@Service
public class AttendanceService {
    
    private static final Logger logger = LoggerFactory.getLogger(AttendanceService.class);
    
    // College location coordinates
    private static final double COLLEGE_LATITUDE = 12.966868;
    private static final double COLLEGE_LONGITUDE = 77.723573;
    private static final double ALLOWED_DISTANCE_METERS = 100.0;
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Transactional
    public Attendance markAttendance(Long studentId, Long courseId, LocalDate date, boolean present) {
        User student = userRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
        
        Optional<Attendance> existing = attendanceRepository.findByStudentAndCourseAndDate(student, course, date);
        
        Attendance attendance;
        if (existing.isPresent()) {
            attendance = existing.get();
            attendance.setPresent(present);
        } else {
            attendance = new Attendance(student, course, date, present);
        }
        
        return attendanceRepository.save(attendance);
    }
    
    public List<Attendance> getStudentAttendance(Long studentId) {
        User student = userRepository.findById(studentId).orElseThrow();
        return attendanceRepository.findByStudent(student);
    }
    
    public List<Attendance> getCourseAttendance(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        return attendanceRepository.findByCourse(course);
    }
    
    public List<Attendance> getStudentCourseAttendance(Long studentId, Long courseId) {
        User student = userRepository.findById(studentId).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();
        return attendanceRepository.findByStudentAndCourse(student, course);
    }
    
    public List<Attendance> getFilteredAttendance(String teacherUsername, String date, String startDate, String endDate, Long courseId, Long studentId, TeacherAccessService teacherAccessService) {
        // Validate date range
        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            if (!end.isAfter(start)) {
                throw new RuntimeException("End date must be after start date");
            }
        }
        
        List<Attendance> results = new ArrayList<>();
        
        if (courseId != null) {
            if (!teacherAccessService.canAccessCourse(teacherUsername, courseId)) {
                throw new RuntimeException("Access denied: You are not assigned to this course");
            }
            results = getCourseAttendance(courseId);
        } else {
            // Get all attendance for teacher's courses
            List<Course> teacherCourses = teacherAccessService.getTeacherCourses(teacherUsername);
            for (Course course : teacherCourses) {
                results.addAll(getCourseAttendance(course.getId()));
            }
        }
        
        // Filter by date if provided
        if (date != null && !date.isEmpty()) {
            LocalDate filterDate = LocalDate.parse(date);
            results = results.stream()
                .filter(a -> a.getDate().equals(filterDate))
                .collect(Collectors.toList());
        } else if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            results = results.stream()
                .filter(a -> !a.getDate().isBefore(start) && !a.getDate().isAfter(end))
                .collect(Collectors.toList());
        }
        
        // Filter by student if provided
        if (studentId != null) {
            if (!teacherAccessService.canAccessStudent(teacherUsername, studentId)) {
                throw new RuntimeException("Access denied: Student not in your courses");
            }
            results = results.stream()
                .filter(a -> a.getStudent().getId().equals(studentId))
                .collect(Collectors.toList());
        }
        
        return results;
    }
    
    public List<Attendance> getStudentAttendanceHistory(Long studentId, String startDate, String endDate) {
        User student = userRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        
        return attendanceRepository.findByStudent(student).stream()
            .filter(a -> !a.getDate().isBefore(start) && !a.getDate().isAfter(end))
            .collect(Collectors.toList());
    }
    
    public List<Attendance> getTodaysAttendanceForTeacher(String teacherUsername, String date, boolean present, TeacherAccessService teacherAccessService) {
        LocalDate filterDate = LocalDate.parse(date);
        List<Course> teacherCourses = teacherAccessService.getTeacherCourses(teacherUsername);
        List<Attendance> results = new ArrayList<>();
        
        for (Course course : teacherCourses) {
            List<Attendance> courseAttendance = attendanceRepository.findByCourse(course).stream()
                .filter(a -> a.getDate().equals(filterDate) && a.isPresent() == present)
                .collect(Collectors.toList());
            results.addAll(courseAttendance);
        }
        
        return results;
    }
    
    @Transactional
    public Attendance markStudentAttendance(String studentUsername, Long courseId, String dateStr, boolean present) {
        logger.info("Marking attendance for student: {}, courseId: {}, date: {}, present: {}", 
                   studentUsername, courseId, dateStr, present);
        
        User student = userRepository.findByUsername(studentUsername)
            .orElseThrow(() -> new RuntimeException("Student not found: " + studentUsername));
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
        
        LocalDate date = LocalDate.parse(dateStr);
        
        // Check if attendance already exists for this date
        Optional<Attendance> existing = attendanceRepository.findByStudentAndCourseAndDate(student, course, date);
        if (existing.isPresent()) {
            throw new RuntimeException("Attendance already marked for this course today");
        }
        
        Attendance attendance = new Attendance(student, course, date, present);
        Attendance saved = attendanceRepository.save(attendance);
        logger.info("Successfully marked attendance - ID: {}, student: {}, present: {}", 
                   saved.getId(), studentUsername, saved.isPresent());
        
        return saved;
    }
    
    @Transactional
    public Attendance markStudentAttendanceWithLocation(String studentUsername, Long courseId, String dateStr, boolean present, Double latitude, Double longitude) {
        logger.info("Marking attendance with location for student: {}, location: [{}, {}]", 
                   studentUsername, latitude, longitude);
        
        User student = userRepository.findByUsername(studentUsername)
            .orElseThrow(() -> new RuntimeException("Student not found: " + studentUsername));
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
        
        LocalDate date = LocalDate.parse(dateStr);
        
        // Check if attendance already exists for this date
        Optional<Attendance> existing = attendanceRepository.findByStudentAndCourseAndDate(student, course, date);
        if (existing.isPresent()) {
            throw new RuntimeException("Attendance already marked for this course today");
        }
        
        Attendance attendance = new Attendance(student, course, date, present);
        attendance.setLocationLatitude(latitude);
        attendance.setLocationLongitude(longitude);
        
        return attendanceRepository.save(attendance);
    }
    

    
    @Transactional
    public Attendance markAttendanceWithLeave(Long studentId, Long courseId, LocalDate date, boolean present, boolean onLeave) {
        User student = userRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
        
        Optional<Attendance> existing = attendanceRepository.findByStudentAndCourseAndDate(student, course, date);
        
        Attendance attendance;
        if (existing.isPresent()) {
            attendance = existing.get();
            attendance.setPresent(present);
            attendance.setOnLeave(onLeave);
        } else {
            attendance = new Attendance(student, course, date, present, onLeave);
        }
        
        return attendanceRepository.save(attendance);
    }
    
    @Transactional
    public Attendance checkIn(String studentUsername, Double latitude, Double longitude) {
        User student = userRepository.findByUsername(studentUsername)
            .orElseThrow(() -> new RuntimeException("Student not found: " + studentUsername));
        
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        
        // Check if within check-in window (8:00 AM - 9:00 AM)
        LocalTime checkInStart = LocalTime.of(8, 0);
        LocalTime checkInEnd = LocalTime.of(9, 0);
        
        if (currentTime.isBefore(checkInStart) || currentTime.isAfter(checkInEnd)) {
            throw new RuntimeException("Check-in only allowed between 8:00 AM and 9:00 AM");
        }
        
        // Find or create today's attendance record
        Optional<Attendance> existing = attendanceRepository.findByStudentAndDate(student, today);
        Attendance attendance;
        
        if (existing.isPresent()) {
            attendance = existing.get();
            if (attendance.getCheckInTime() != null) {
                throw new RuntimeException("Already checked in today");
            }
        } else {
            attendance = new Attendance();
            attendance.setStudent(student);
            attendance.setDate(today);
            attendance.setAttendanceStatus(AttendanceStatus.ABSENT);
        }
        
        attendance.setCheckInTime(now);
        attendance.setIsCheckInValid(true);
        attendance.setLocationLatitude(latitude);
        attendance.setLocationLongitude(longitude);
        
        updateAttendanceStatus(attendance);
        return attendanceRepository.save(attendance);
    }
    
    @Transactional
    public Attendance checkOut(String studentUsername, Double latitude, Double longitude) {
        User student = userRepository.findByUsername(studentUsername)
            .orElseThrow(() -> new RuntimeException("Student not found: " + studentUsername));
        
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        
        // Check if within check-out window (4:00 PM - 5:00 PM)
        LocalTime checkOutStart = LocalTime.of(16, 0);
        LocalTime checkOutEnd = LocalTime.of(17, 0);
        
        if (currentTime.isBefore(checkOutStart) || currentTime.isAfter(checkOutEnd)) {
            throw new RuntimeException("Check-out only allowed between 4:00 PM and 5:00 PM");
        }
        
        // Find today's attendance record
        Attendance attendance = attendanceRepository.findByStudentAndDate(student, today)
            .orElseThrow(() -> new RuntimeException("No check-in record found for today"));
        
        if (attendance.getCheckOutTime() != null) {
            throw new RuntimeException("Already checked out today");
        }
        
        if (attendance.getCheckInTime() == null) {
            throw new RuntimeException("Must check-in before checking out");
        }
        
        attendance.setCheckOutTime(now);
        attendance.setIsCheckOutValid(true);
        
        updateAttendanceStatus(attendance);
        return attendanceRepository.save(attendance);
    }
    
    private void updateAttendanceStatus(Attendance attendance) {
        boolean hasValidCheckIn = attendance.getIsCheckInValid() != null && attendance.getIsCheckInValid();
        boolean hasValidCheckOut = attendance.getIsCheckOutValid() != null && attendance.getIsCheckOutValid();
        
        if (hasValidCheckIn && hasValidCheckOut) {
            attendance.setAttendanceStatus(AttendanceStatus.PRESENT);
            attendance.setPresent(true);
        } else if (hasValidCheckIn || hasValidCheckOut) {
            attendance.setAttendanceStatus(AttendanceStatus.PARTIAL);
            attendance.setPresent(false);
        } else {
            attendance.setAttendanceStatus(AttendanceStatus.ABSENT);
            attendance.setPresent(false);
        }
    }
    
    public Attendance getTodayAttendanceStatus(String studentUsername) {
        User student = userRepository.findByUsername(studentUsername)
            .orElseThrow(() -> new RuntimeException("Student not found: " + studentUsername));
        
        LocalDate today = LocalDate.now();
        return attendanceRepository.findByStudentAndDate(student, today).orElse(null);
    }
    
    @Transactional
    public void markAbsentForMissedCheckInOut() {
        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        
        // Run this after 5:00 PM to mark absent for incomplete attendance
        if (currentTime.isAfter(LocalTime.of(17, 0))) {
            List<Attendance> incompleteAttendance = attendanceRepository.findByDateAndAttendanceStatusNot(today, AttendanceStatus.PRESENT);
            
            for (Attendance attendance : incompleteAttendance) {
                attendance.setAttendanceStatus(AttendanceStatus.ABSENT);
                attendance.setPresent(false);
                attendanceRepository.save(attendance);
            }
        }
    }
}