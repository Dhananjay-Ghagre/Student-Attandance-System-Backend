package com.college.attendance.service;

import com.college.attendance.entity.Attendance;
import com.college.attendance.entity.Course;
import com.college.attendance.entity.User;
import com.college.attendance.repository.AttendanceRepository;
import com.college.attendance.repository.CourseRepository;
import com.college.attendance.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private AttendanceService attendanceService;

    private User student;
    private Course course;
    private LocalDate testDate;

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

        testDate = LocalDate.now();
    }

    @Test
    void shouldMarkAttendanceSuccessfully() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(attendanceRepository.findByStudentAndCourseAndDate(student, course, testDate))
                .thenReturn(Optional.empty());
        
        Attendance savedAttendance = new Attendance(student, course, testDate, true);
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(savedAttendance);

        // When
        Attendance result = attendanceService.markAttendance(1L, 1L, testDate, true);

        // Then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(student, result.getStudent());
        assertEquals(course, result.getCourse());
        verify(attendanceRepository).save(any(Attendance.class));
    }

    @Test
    void shouldThrowExceptionWhenStudentNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> attendanceService.markAttendance(1L, 1L, testDate, true));
        
        assertEquals("Student not found with id: 1", exception.getMessage());
    }

    @Test
    void shouldMarkStudentAttendanceWithLocation() {
        // Given
        when(userRepository.findByUsername("student1")).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(attendanceRepository.findByStudentAndCourseAndDate(any(), any(), any()))
                .thenReturn(Optional.empty());
        
        Attendance savedAttendance = new Attendance(student, course, testDate, true);
        savedAttendance.setLocationLatitude(12.966868);
        savedAttendance.setLocationLongitude(77.723573);
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(savedAttendance);

        // When
        Attendance result = attendanceService.markStudentAttendanceWithLocation(
            "student1", 1L, testDate.toString(), true, 12.966868, 77.723573);

        // Then
        assertNotNull(result);
        assertEquals(12.966868, result.getLocationLatitude());
        assertEquals(77.723573, result.getLocationLongitude());
        verify(attendanceRepository).save(any(Attendance.class));
    }

    @Test
    void shouldThrowExceptionWhenAttendanceAlreadyMarked() {
        // Given
        Attendance existingAttendance = new Attendance(student, course, testDate, true);
        when(userRepository.findByUsername("student1")).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(attendanceRepository.findByStudentAndCourseAndDate(student, course, testDate))
                .thenReturn(Optional.of(existingAttendance));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> attendanceService.markStudentAttendance("student1", 1L, testDate.toString(), true));
        
        assertEquals("Attendance already marked for this course today", exception.getMessage());
    }
}