package com.college.attendance.service;

import com.college.attendance.entity.Attendance;
import com.college.attendance.entity.AttendanceStatus;
import com.college.attendance.entity.CourseEnrollment;
import com.college.attendance.entity.User;
import com.college.attendance.repository.AttendanceRepository;
import com.college.attendance.repository.CourseEnrollmentRepository;
import com.college.attendance.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceSchedulerService {
    
    private static final Logger logger = LoggerFactory.getLogger(AttendanceSchedulerService.class);
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CourseEnrollmentRepository courseEnrollmentRepository;
    
    @Value("${attendance.backfill.days:30}")
    private int backfillDays;
    
    @Value("${attendance.backfill.enabled:true}")
    private boolean backfillEnabled;
    
    @Scheduled(cron = "0 30 17 * * ?") // Run at 5:30 PM daily
    @Transactional
    public void markAbsentStudents() {
        logger.info("Starting scheduled task to mark absent students");
        
        LocalDate today = LocalDate.now();
        
        try {
            // First, create attendance records for all enrolled students who haven't marked attendance
            int createdRecords = createMissingAttendanceRecords(today);
            
            // Then, mark incomplete attendance records as absent
            List<Attendance> incompleteAttendance = attendanceRepository.findByDateAndAttendanceStatusNot(today, AttendanceStatus.PRESENT);
            
            int markedAbsent = 0;
            for (Attendance attendance : incompleteAttendance) {
                if (attendance.getAttendanceStatus() != AttendanceStatus.PRESENT) {
                    attendance.setAttendanceStatus(AttendanceStatus.ABSENT);
                    attendance.setPresent(false);
                    attendanceRepository.save(attendance);
                    markedAbsent++;
                }
            }
            
            logger.info("Scheduled task completed: {} new records created, {} students marked absent for {}", 
                       createdRecords, markedAbsent, today);
            
        } catch (Exception e) {
            logger.error("Error in scheduled task for marking absent students: {}", e.getMessage(), e);
        }
    }
    
    @Transactional
    public int createMissingAttendanceRecords(LocalDate date) {
        logger.debug("Creating missing attendance records for date: {}", date);
        
        // Get all active course enrollments
        List<CourseEnrollment> enrollments = courseEnrollmentRepository.findAll();
        int createdCount = 0;
        
        for (CourseEnrollment enrollment : enrollments) {
            // Check if attendance record already exists
            Optional<Attendance> existing = attendanceRepository.findByStudentAndCourseAndDate(
                enrollment.getStudent(), enrollment.getCourse(), date);
            
            if (existing.isEmpty()) {
                // Create absent record for students who didn't mark attendance
                Attendance attendance = new Attendance(
                    enrollment.getStudent(), 
                    enrollment.getCourse(), 
                    date, 
                    false // present = false (absent)
                );
                attendance.setAttendanceStatus(AttendanceStatus.ABSENT);
                attendanceRepository.save(attendance);
                createdCount++;
                
                logger.debug("Created absent record for student: {} in course: {} for date: {}", 
                           enrollment.getStudent().getUsername(), enrollment.getCourse().getName(), date);
            }
        }
        
        if (createdCount > 0) {
            logger.info("Created {} missing attendance records for {}", createdCount, date);
        }
        return createdCount;
    }
    
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void backfillMissingAttendanceOnStartup() {
        if (!backfillEnabled) {
            logger.info("Attendance backfill is disabled");
            return;
        }
        
        logger.info("Starting attendance backfill process for last {} days", backfillDays);
        
        LocalDate endDate = LocalDate.now().minusDays(1); // Yesterday
        LocalDate startDate = endDate.minusDays(backfillDays - 1);
        
        int totalCreated = 0;
        
        try {
            // Backfill for each day in the range
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                int dailyCreated = createMissingAttendanceRecords(date);
                totalCreated += dailyCreated;
            }
            
            logger.info("Attendance backfill completed: {} total records created for period {} to {}", 
                       totalCreated, startDate, endDate);
            
        } catch (Exception e) {
            logger.error("Error during attendance backfill: {}", e.getMessage(), e);
        }
    }
}