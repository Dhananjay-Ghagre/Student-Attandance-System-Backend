package com.college.attendance.dto;

public class CourseEnrollmentDTO {
    private Long courseId;
    private String courseName;
    private String courseCode;
    private Long enrolledStudents;
    private String teacherName;
    
    public CourseEnrollmentDTO() {}
    
    public CourseEnrollmentDTO(Long courseId, String courseName, String courseCode, Long enrolledStudents, String teacherName) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.enrolledStudents = enrolledStudents;
        this.teacherName = teacherName;
    }
    
    // Getters and Setters
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    
    public Long getEnrolledStudents() { return enrolledStudents; }
    public void setEnrolledStudents(Long enrolledStudents) { this.enrolledStudents = enrolledStudents; }
    
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
}