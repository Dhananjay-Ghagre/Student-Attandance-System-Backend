package com.college.attendance.controller;

import com.college.attendance.dto.JwtResponse;
import com.college.attendance.dto.LoginRequest;
import com.college.attendance.dto.RegisterRequest;
import com.college.attendance.entity.User;
import com.college.attendance.security.JwtUtils;
import com.college.attendance.service.UserService;
import com.college.attendance.service.OtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private OtpService otpService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        logger.info("Login attempt for username: {}", loginRequest.getUsername());
        
        Optional<User> userOpt = userService.findByUsername(loginRequest.getUsername());
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            logger.debug("User found: {}, Role: {}", user.getUsername(), user.getRole());
            
            boolean passwordValid = userService.validatePassword(loginRequest.getPassword(), user.getPassword());
            logger.debug("Password validation result for user {}: {}", user.getUsername(), passwordValid);
            
            if (passwordValid) {
                String token = jwtUtils.generateJwtToken(user.getUsername(), user.getRole().name());
                logger.info("Successful login for user: {} with role: {}", user.getUsername(), user.getRole());
                return ResponseEntity.ok(new JwtResponse(token, user.getUsername(), user.getRole().name()));
            } else {
                logger.warn("Invalid password attempt for user: {}", user.getUsername());
            }
        } else {
            logger.warn("Login attempt for non-existent user: {}", loginRequest.getUsername());
        }
        
        return ResponseEntity.badRequest().body("Invalid credentials");
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Password is required"));
            }
            
            if (userService.existsByUsername(request.getUsername())) {
                return ResponseEntity.status(409).body(Map.of("error", "Username already exists"));
            }
            
            if (userService.existsByEmail(request.getEmail())) {
                return ResponseEntity.status(409).body(Map.of("error", "Email already exists"));
            }
            
            // Generate and send OTP
            otpService.generateAndSendOtp(request.getEmail(), "REGISTRATION");
            
            return ResponseEntity.ok(Map.of("message", "OTP sent to your email. Please verify to complete registration."));
        } catch (Exception e) {
            logger.error("Registration error for email {}: {}", request.getEmail(), e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/verify-registration")
    public ResponseEntity<?> verifyRegistration(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String otp = request.get("otp");
            String username = request.get("username");
            String password = request.get("password");
            String fullName = request.get("fullName");
            String role = request.get("role");
            
            if (otpService.verifyOtp(email, otp, "REGISTRATION")) {
                User user = new User(username, password, email, fullName, User.Role.valueOf(role));
                User savedUser = userService.createUser(user);
                return ResponseEntity.ok(Map.of("message", "Registration successful", "user", savedUser));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired OTP"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Verification failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            
            if (!userService.existsByEmail(email)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email not found"));
            }
            
            otpService.generateAndSendOtp(email, "FORGOT_PASSWORD");
            
            return ResponseEntity.ok(Map.of("message", "OTP sent to your email"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to send OTP: " + e.getMessage()));
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String otp = request.get("otp");
            String newPassword = request.get("newPassword");
            
            if (otpService.verifyOtp(email, otp, "FORGOT_PASSWORD")) {
                userService.updatePasswordByEmail(email, newPassword);
                return ResponseEntity.ok(Map.of("message", "Password reset successful"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired OTP"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password reset failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody Map<String, String> request) {
        String email = null;
        try {
            email = request.get("email");
            String type = request.get("type");
            
            if (email == null || type == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email and type are required"));
            }
            
            otpService.generateAndSendOtp(email, type);
            
            return ResponseEntity.ok(Map.of("message", "OTP resent successfully"));
        } catch (Exception e) {
            logger.error("Resend OTP error for email {}: {}", email, e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}