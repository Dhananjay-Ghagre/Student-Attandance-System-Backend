package com.college.attendance.service;

import com.college.attendance.entity.OtpVerification;
import com.college.attendance.entity.OtpRateLimit;
import com.college.attendance.repository.OtpVerificationRepository;
import com.college.attendance.repository.OtpRateLimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.util.Optional;

@Service
public class OtpService {
    
    @Autowired
    private OtpVerificationRepository otpRepository;
    
    @Autowired
    private OtpRateLimitRepository rateLimitRepository;
    
    @Autowired
    private EmailService emailService;
    
    private final SecureRandom random = new SecureRandom();
    private static final int MAX_OTP_REQUESTS = 5; // Max 5 OTP requests per 15 minutes
    
    @Transactional
    public void generateAndSendOtp(String email, String type) {
        try {
            // Check rate limiting
            checkRateLimit(email);
            
            // Delete any existing OTP for this email and type
            otpRepository.deleteByEmailAndType(email, type);
            
            // Generate 6-digit OTP
            String otp = String.format("%06d", random.nextInt(1000000));
            
            // Save OTP
            OtpVerification otpVerification = new OtpVerification(email, otp, type);
            otpRepository.save(otpVerification);
            
            // Send email (fallback to console if email fails)
            emailService.sendOtpEmail(email, otp, type);
            
            // Update rate limit
            updateRateLimit(email);
            
            System.out.println("✅ OTP generated successfully for: " + email);
            
        } catch (Exception e) {
            System.err.println("Error generating OTP: " + e.getMessage());
            throw new RuntimeException("Failed to generate OTP: " + e.getMessage());
        }
    }
    
    private void checkRateLimit(String email) {
        Optional<OtpRateLimit> rateLimitOpt = rateLimitRepository.findByEmail(email);
        
        if (rateLimitOpt.isPresent()) {
            OtpRateLimit rateLimit = rateLimitOpt.get();
            
            if (rateLimit.isWindowExpired()) {
                rateLimit.resetWindow();
                rateLimitRepository.save(rateLimit);
            } else if (rateLimit.getRequestCount() >= MAX_OTP_REQUESTS) {
                throw new RuntimeException("Too many OTP requests. Please try again after 15 minutes.");
            }
        }
    }
    
    private void updateRateLimit(String email) {
        Optional<OtpRateLimit> rateLimitOpt = rateLimitRepository.findByEmail(email);
        
        if (rateLimitOpt.isPresent()) {
            OtpRateLimit rateLimit = rateLimitOpt.get();
            rateLimit.incrementCount();
            rateLimitRepository.save(rateLimit);
        } else {
            rateLimitRepository.save(new OtpRateLimit(email));
        }
    }
    
    public boolean verifyOtp(String email, String otp, String type) {
        Optional<OtpVerification> otpVerification = otpRepository
            .findByEmailAndOtpAndTypeAndVerifiedFalse(email, otp, type);
        
        if (otpVerification.isPresent() && !otpVerification.get().isExpired()) {
            otpVerification.get().setVerified(true);
            otpRepository.save(otpVerification.get());
            return true;
        }
        
        return false;
    }
}