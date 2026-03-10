package com.college.attendance.dto;

import com.college.attendance.entity.User;

public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String fullName;
    private User.Role role;
    
    public RegisterRequest() {}
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public User.Role getRole() { return role; }
    public void setRole(User.Role role) { this.role = role; }
}