package com.college.attendance.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("admin123: " + encoder.encode("admin123"));
        System.out.println("teacher123: " + encoder.encode("teacher123"));
        System.out.println("student123: " + encoder.encode("student123"));
    }
}