package com.college.attendance.controller;

import com.college.attendance.entity.Attendance;
import com.college.attendance.entity.Course;
import com.college.attendance.entity.User;
import com.college.attendance.service.AttendanceService;
import com.college.attendance.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentControllerUnitTest {

    @Mock
    private AttendanceService attendanceService;
    
    @Mock
    private UserService userService;
    
    @Mock
    private Authentication authentication;
    
    @InjectMocks
    private StudentController studentController;

    private User student;
    private Course course;
    private Attendance attendance;

    @BeforeEach
    void setUp() {
        student = new User();
        student.setId(1L);
        student.setUsername("student1");
        student.setRole(User.Role.STUDENT);

        course = new Course();
        course.setId(1L);
        course.setName("Mathematics 101");
        course.setCode("MATH101");

        attendance = new Attendance(student, course, LocalDate.now(), true);
    }

    @Test
    void shouldGetStudentAttendance() {
        // Given
        when(authentication.getName()).thenReturn("student1");
        when(userService.findByUsername("student1")).thenReturn(Optional.of(student));
        when(attendanceService.getStudentAttendance(1L)).thenReturn(List.of(attendance));

        // When
        ResponseEntity<List<Attendance>> response = studentController.getMyAttendance(authentication);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertTrue(response.getBody().get(0).isPresent());
    }

    @Test
    void shouldGetStudentProfile() {
        // Given
        when(authentication.getName()).thenReturn("student1");
        when(userService.getUserByUsername("student1")).thenReturn(student);

        // When
        ResponseEntity<?> response = studentController.getProfile(authentication);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(student, response.getBody());
    }

    @Test
    void shouldThrowExceptionWhenStudentNotFound() {
        // Given
        when(authentication.getName()).thenReturn("student1");
        when(userService.findByUsername("student1")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, 
            () -> studentController.getMyAttendance(authentication));
    }
}