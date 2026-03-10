-- Ensure student1 is enrolled in at least one course
-- Run this if student1 has no course enrollments

-- Check if student1 exists and get their ID
SELECT id, username FROM users WHERE username = 'student1' AND role = 'STUDENT';

-- Check current enrollments for student1
SELECT ce.*, c.name, c.code 
FROM course_enrollments ce 
JOIN courses c ON ce.course_id = c.id 
JOIN users u ON ce.student_id = u.id 
WHERE u.username = 'student1';

-- If no enrollments exist, add student1 to Mathematics 101
INSERT IGNORE INTO course_enrollments (student_id, course_id) 
SELECT u.id, c.id 
FROM users u, courses c 
WHERE u.username = 'student1' 
AND c.code = 'MATH101'
AND NOT EXISTS (
    SELECT 1 FROM course_enrollments ce2 
    WHERE ce2.student_id = u.id AND ce2.course_id = c.id
);