package com.college.attendance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    public void sendOtpEmail(String to, String otp, String type) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("dhananjayghagre37@gmail.com");
            message.setTo(to);
            message.setSubject("OTP Verification - Student Attendance System");
            
            String body = buildEmailBody(otp, type);
            message.setText(body);
            
            mailSender.send(message);
            System.out.println("✅ Email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("❌ Email failed for " + to + ": " + e.getMessage());
            System.out.println("🔑 DEVELOPMENT OTP for " + to + " (" + type + "): " + otp);
            System.out.println("📧 Use this OTP to continue with registration/password reset");
            // Don't throw exception - allow process to continue with console OTP
        }
    }
    
    private String buildEmailBody(String otp, String type) {
        String action = type.equals("REGISTRATION") ? "complete your registration" : "reset your password";
        
        return String.format(
            "Dear User,\n\n" +
            "Your OTP to %s is: %s\n\n" +
            "This OTP will expire in 10 minutes for security reasons.\n\n" +
            "If you did not request this OTP, please ignore this email.\n\n" +
            "Best regards,\n" +
            "Student Attendance System Team",
            action, otp
        );
    }
}