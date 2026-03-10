package com.college.attendance.dto;

import com.college.attendance.entity.LeaveRequest;

public class LeaveReviewDTO {
    private LeaveRequest.Status status;
    private String teacherComment;
    
    // Constructors
    public LeaveReviewDTO() {}
    
    public LeaveReviewDTO(LeaveRequest.Status status, String teacherComment) {
        this.status = status;
        this.teacherComment = teacherComment;
    }
    
    // Getters and Setters
    public LeaveRequest.Status getStatus() { return status; }
    public void setStatus(LeaveRequest.Status status) { this.status = status; }
    
    public String getTeacherComment() { return teacherComment; }
    public void setTeacherComment(String teacherComment) { this.teacherComment = teacherComment; }
}