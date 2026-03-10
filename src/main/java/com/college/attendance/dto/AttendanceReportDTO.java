package com.college.attendance.dto;

public class AttendanceReportDTO {
    private Long courseId;
    private String courseName;
    private String courseCode;
    private Long totalStudents;
    private Long presentStudents;
    private Long absentStudents;
    private String date;
    
    public AttendanceReportDTO() {}
    
    public AttendanceReportDTO(Long courseId, String courseName, String courseCode, Long totalStudents, Long presentStudents, Long absentStudents, String date) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.totalStudents = totalStudents;
        this.presentStudents = presentStudents;
        this.absentStudents = absentStudents;
        this.date = date;
    }
    
    // Getters and Setters
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    
    public Long getTotalStudents() { return totalStudents; }
    public void setTotalStudents(Long totalStudents) { this.totalStudents = totalStudents; }
    
    public Long getPresentStudents() { return presentStudents; }
    public void setPresentStudents(Long presentStudents) { this.presentStudents = presentStudents; }
    
    public Long getAbsentStudents() { return absentStudents; }
    public void setAbsentStudents(Long absentStudents) { this.absentStudents = absentStudents; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}