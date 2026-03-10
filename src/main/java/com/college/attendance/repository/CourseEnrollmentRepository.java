package com.college.attendance.repository;

import com.college.attendance.entity.Course;
import com.college.attendance.entity.CourseEnrollment;
import com.college.attendance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {
    List<CourseEnrollment> findByCourse(Course course);
    List<CourseEnrollment> findByStudent(User student);
    
    @Query("SELECT ce.student FROM CourseEnrollment ce WHERE ce.course.teacher = :teacher")
    List<User> findStudentsByTeacher(User teacher);
}