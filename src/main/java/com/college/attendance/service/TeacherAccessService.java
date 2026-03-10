package com.college.attendance.service;

import com.college.attendance.entity.Course;
import com.college.attendance.entity.User;
import com.college.attendance.repository.CourseEnrollmentRepository;
import com.college.attendance.repository.CourseRepository;
import com.college.attendance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherAccessService {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CourseEnrollmentRepository courseEnrollmentRepository;
    
    public boolean canAccessCourse(String teacherUsername, Long courseId) {
        User teacher = userRepository.findByUsername(teacherUsername).orElse(null);
        if (teacher == null || teacher.getRole() != User.Role.TEACHER) {
            return false;
        }
        
        Course course = courseRepository.findById(courseId).orElse(null);
        return course != null && course.getTeacher() != null && 
               course.getTeacher().getId().equals(teacher.getId());
    }
    
    public List<Course> getTeacherCourses(String teacherUsername) {
        User teacher = userRepository.findByUsername(teacherUsername).orElseThrow();
        return courseRepository.findByTeacher(teacher);
    }
    
    public List<User> getStudentsForTeacherCourses(String teacherUsername) {
        User teacher = userRepository.findByUsername(teacherUsername).orElseThrow();
        List<User> enrolledStudents = courseEnrollmentRepository.findStudentsByTeacher(teacher);
        
        // If no enrollments exist, return all students for backward compatibility
        if (enrolledStudents.isEmpty()) {
            return userRepository.findByRole(User.Role.STUDENT);
        }
        
        return enrolledStudents.stream().distinct().collect(Collectors.toList());
    }
    
    public boolean canAccessStudent(String teacherUsername, Long studentId) {
        List<User> accessibleStudents = getStudentsForTeacherCourses(teacherUsername);
        return accessibleStudents.stream().anyMatch(student -> student.getId().equals(studentId));
    }
}