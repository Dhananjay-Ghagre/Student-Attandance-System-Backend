package com.college.attendance.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otp_rate_limit")
public class OtpRateLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private int requestCount = 0;
    
    @Column(nullable = false)
    private LocalDateTime windowStart;
    
    public OtpRateLimit() {}
    
    public OtpRateLimit(String email) {
        this.email = email;
        this.requestCount = 1;
        this.windowStart = LocalDateTime.now();
    }
    
    public void incrementCount() {
        this.requestCount++;
    }
    
    public void resetWindow() {
        this.requestCount = 1;
        this.windowStart = LocalDateTime.now();
    }
    
    public boolean isWindowExpired() {
        return LocalDateTime.now().isAfter(windowStart.plusMinutes(15));
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public int getRequestCount() { return requestCount; }
    public void setRequestCount(int requestCount) { this.requestCount = requestCount; }
    
    public LocalDateTime getWindowStart() { return windowStart; }
    public void setWindowStart(LocalDateTime windowStart) { this.windowStart = windowStart; }
}