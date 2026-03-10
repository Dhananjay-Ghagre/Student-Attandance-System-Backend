package com.college.attendance.dto;

import java.time.LocalDate;

public class AttendanceRequest {
    private Long studentId;
    private Long courseId;
    private LocalDate date;
    private boolean present;
    
    public AttendanceRequest() {}
    
    public AttendanceRequest(Long studentId, Long courseId, LocalDate date, boolean present) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.date = date;
        this.present = present;
    }
    
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public boolean isPresent() { return present; }
    public void setPresent(boolean present) { this.present = present; }
}