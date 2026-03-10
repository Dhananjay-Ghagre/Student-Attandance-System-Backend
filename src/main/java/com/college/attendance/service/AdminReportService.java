package com.college.attendance.service;

import com.college.attendance.dto.AttendanceReportDTO;
import com.college.attendance.dto.CourseEnrollmentDTO;
import com.college.attendance.entity.Course;
import com.college.attendance.entity.User;
import com.college.attendance.repository.AttendanceRepository;
import com.college.attendance.repository.CourseRepository;
import com.college.attendance.repository.LeaveRequestRepository;
import com.college.attendance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminReportService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;
    
    public Long getTotalStudents() {
        return userRepository.countByRole(User.Role.STUDENT);
    }
    
    public List<CourseEnrollmentDTO> getCourseEnrollments() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream().map(course -> {
            Long enrolledStudents = (long) userRepository.findByRole(User.Role.STUDENT).size();
            String teacherName = course.getTeacher() != null ? course.getTeacher().getFullName() : "Not Assigned";
            return new CourseEnrollmentDTO(
                course.getId(),
                course.getName(),
                course.getCode(),
                enrolledStudents,
                teacherName
            );
        }).collect(Collectors.toList());
    }
    
    public List<AttendanceReportDTO> getAttendanceReportByDate(LocalDate date) {
        List<Course> courses = courseRepository.findAll();
        return courses.stream().map(course -> {
            // Get actual attendance records for this course and date
            Long presentStudents = attendanceRepository.countByCourseAndDateAndPresent(course, date, true);
            Long absentStudents = attendanceRepository.countByCourseAndDateAndPresent(course, date, false);
            Long totalStudents = presentStudents + absentStudents;
            
            return new AttendanceReportDTO(
                course.getId(),
                course.getName(),
                course.getCode(),
                totalStudents,
                presentStudents,
                absentStudents,
                date.toString()
            );
        }).collect(Collectors.toList());
    }
    
    public void assignTeacherToCourse(Long courseId, Long teacherId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found"));
        User teacher = userRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("Teacher not found"));
        
        if (teacher.getRole() != User.Role.TEACHER) {
            throw new RuntimeException("User is not a teacher");
        }
        
        course.setTeacher(teacher);
        courseRepository.save(course);
    }
    
    public List<?> getFilteredAttendance(String startDate, String endDate, Long courseId, Long studentId, String batch, String year) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;
        
        return attendanceRepository.findFilteredAttendance(start, end, courseId, studentId, batch, year);
    }
    
    public List<?> getAttendanceByDate(LocalDate date, Boolean present) {
        System.out.println("DEBUG: AdminReportService.getAttendanceByDate called with date=" + date + ", present=" + present);
        
        List<com.college.attendance.entity.Attendance> allRecords = attendanceRepository.findFilteredAttendance(date, date, null, null, null, null);
        System.out.println("DEBUG: Found " + allRecords.size() + " total attendance records for date " + date);
        
        if (present != null) {
            List<com.college.attendance.entity.Attendance> filteredRecords = allRecords.stream()
                .filter(record -> {
                    boolean isPresent = record.isPresent();
                    boolean onLeave = record.isOnLeave();
                    System.out.println("DEBUG: Student " + record.getStudent().getFullName() + " - present: " + isPresent + ", onLeave: " + onLeave + ", filtering for present: " + present);
                    return isPresent == present && !onLeave; // Exclude leave records from present/absent counts
                })
                .collect(Collectors.toList());
            System.out.println("DEBUG: After filtering for present=" + present + ", found " + filteredRecords.size() + " records");
            return filteredRecords;
        } else {
            return allRecords;
        }
    }
    
    public List<?> getApprovedLeaves(LocalDate date) {
        return leaveRequestRepository.findApprovedLeavesForDate(date);
    }
}