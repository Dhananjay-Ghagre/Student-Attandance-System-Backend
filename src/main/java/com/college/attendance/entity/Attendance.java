package com.college.attendance.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({"courses", "attendances"})
    private User student;
    
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnoreProperties({"teacher", "attendances", "enrollments"})
    private Course course;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(nullable = false)
    private boolean present;
    
    @Column(nullable = false)
    private boolean onLeave = false;
    
    @Column(name = "location_latitude")
    private Double locationLatitude;
    
    @Column(name = "location_longitude")
    private Double locationLongitude;
    
    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;
    
    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_status")
    private AttendanceStatus attendanceStatus = AttendanceStatus.ABSENT;
    
    @Column(name = "is_check_in_valid")
    private Boolean isCheckInValid = false;
    
    @Column(name = "is_check_out_valid")
    private Boolean isCheckOutValid = false;
    
    // Constructors
    public Attendance() {}
    
    public Attendance(User student, Course course, LocalDate date, boolean present) {
        this.student = student;
        this.course = course;
        this.date = date;
        this.present = present;
        this.onLeave = false;
    }
    
    public Attendance(User student, Course course, LocalDate date, boolean present, boolean onLeave) {
        this.student = student;
        this.course = course;
        this.date = date;
        this.present = present;
        this.onLeave = onLeave;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public boolean isPresent() { return present; }
    public void setPresent(boolean present) { this.present = present; }
    
    public boolean isOnLeave() { return onLeave; }
    public void setOnLeave(boolean onLeave) { this.onLeave = onLeave; }
    
    public Double getLocationLatitude() { return locationLatitude; }
    public void setLocationLatitude(Double locationLatitude) { this.locationLatitude = locationLatitude; }
    
    public Double getLocationLongitude() { return locationLongitude; }
    public void setLocationLongitude(Double locationLongitude) { this.locationLongitude = locationLongitude; }
    
    public LocalDateTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalDateTime checkInTime) { this.checkInTime = checkInTime; }
    
    public LocalDateTime getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(LocalDateTime checkOutTime) { this.checkOutTime = checkOutTime; }
    
    public AttendanceStatus getAttendanceStatus() { return attendanceStatus; }
    public void setAttendanceStatus(AttendanceStatus attendanceStatus) { this.attendanceStatus = attendanceStatus; }
    
    public Boolean getIsCheckInValid() { return isCheckInValid; }
    public void setIsCheckInValid(Boolean isCheckInValid) { this.isCheckInValid = isCheckInValid; }
    
    public Boolean getIsCheckOutValid() { return isCheckOutValid; }
    public void setIsCheckOutValid(Boolean isCheckOutValid) { this.isCheckOutValid = isCheckOutValid; }
}