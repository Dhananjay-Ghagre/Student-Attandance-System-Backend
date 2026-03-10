package com.college.attendance.service;

import com.college.attendance.entity.SelfAttendance;
import com.college.attendance.entity.User;
import com.college.attendance.repository.SelfAttendanceRepository;
import com.college.attendance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class SelfAttendanceService {
    
    @Autowired
    private SelfAttendanceRepository selfAttendanceRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public SelfAttendance markSelfAttendance(String username) {
        User student = userRepository.findByUsername(username).orElseThrow();
        LocalDate today = LocalDate.now();
        
        if (selfAttendanceRepository.existsByStudentAndDate(student, today)) {
            throw new RuntimeException("Attendance already marked for today");
        }
        
        SelfAttendance attendance = new SelfAttendance(student, today);
        return selfAttendanceRepository.save(attendance);
    }
    
    public boolean hasMarkedToday(String username) {
        User student = userRepository.findByUsername(username).orElseThrow();
        return selfAttendanceRepository.existsByStudentAndDate(student, LocalDate.now());
    }
    
    public List<SelfAttendance> getStudentSelfAttendance(String username) {
        User student = userRepository.findByUsername(username).orElseThrow();
        return selfAttendanceRepository.findByStudent(student);
    }
}