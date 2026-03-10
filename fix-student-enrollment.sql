-- Fix student enrollment issue
-- This will enroll student1 in Mathematics 101 course

-- First, let's see what users exist
SELECT id, username, role FROM users WHERE role = 'STUDENT';

-- Check what courses exist
SELECT id, name, code FROM courses;

-- Check current enrollments
SELECT ce.id, u.username, c.name, c.code 
FROM course_enrollments ce 
JOIN users u ON ce.student_id = u.id 
JOIN courses c ON ce.course_id = c.id;

-- Enroll student1 in Mathematics 101 (assuming student1 has id=4 and MATH101 has id=1)
INSERT INTO course_enrollments (student_id, course_id) 
VALUES (4, 1)
ON DUPLICATE KEY UPDATE student_id = student_id;