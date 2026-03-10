USE attendance_db;

-- Check current users and their IDs
SELECT 'Current Users:' as info;
SELECT id, username, role, full_name FROM users ORDER BY id;

-- Check current courses
SELECT 'Current Courses:' as info;
SELECT id, name, code, teacher_id FROM courses ORDER BY id;

-- Check current enrollments
SELECT 'Current Enrollments:' as info;
SELECT ce.id, u.username as student, c.name as course, c.id as course_id
FROM course_enrollments ce 
LEFT JOIN users u ON ce.student_id = u.id 
LEFT JOIN courses c ON ce.course_id = c.id;

-- Clean up and recreate enrollments with correct IDs
DELETE FROM course_enrollments;

-- Get the actual student1 ID and enroll in course 1
INSERT INTO course_enrollments (student_id, course_id) 
SELECT u.id, 1 
FROM users u 
WHERE u.username = 'student1' AND u.role = 'STUDENT';

-- Verify the enrollment
SELECT 'Fixed Enrollments:' as info;
SELECT ce.id, u.username as student, c.name as course, c.id as course_id
FROM course_enrollments ce 
JOIN users u ON ce.student_id = u.id 
JOIN courses c ON ce.course_id = c.id;