package com.college.attendance.dto;

import java.time.LocalDate;

public class LeaveRequestDTO {
    private Long courseId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    
    // Constructors
    public LeaveRequestDTO() {}
    
    public LeaveRequestDTO(Long courseId, LocalDate startDate, LocalDate endDate, String reason) {
        this.courseId = courseId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
    }
    
    // Getters and Setters
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}