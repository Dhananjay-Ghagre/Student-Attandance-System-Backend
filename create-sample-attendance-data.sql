USE attendance_db;

-- First, update existing students with batch and year information
UPDATE users SET 
    batch = '2024-2025',
    yearOfStudy = '1st Year',
    course = 'Mathematics 101'
WHERE username = 'student1' AND role = 'STUDENT';

-- Add some sample attendance records
-- Make sure we have the right student and course IDs
SET @student_id = (SELECT id FROM users WHERE username = 'student1' AND role = 'STUDENT');
SET @course_id = (SELECT id FROM courses WHERE name = 'Mathematics 101' LIMIT 1);

-- Insert sample attendance records for the past week
INSERT INTO attendance (student_id, course_id, date, present, on_leave) VALUES
(@student_id, @course_id, CURDATE() - INTERVAL 7 DAY, true, false),
(@student_id, @course_id, CURDATE() - INTERVAL 6 DAY, true, false),
(@student_id, @course_id, CURDATE() - INTERVAL 5 DAY, false, false),
(@student_id, @course_id, CURDATE() - INTERVAL 4 DAY, true, false),
(@student_id, @course_id, CURDATE() - INTERVAL 3 DAY, true, false),
(@student_id, @course_id, CURDATE() - INTERVAL 2 DAY, false, false),
(@student_id, @course_id, CURDATE() - INTERVAL 1 DAY, true, false);

-- Verify the data
SELECT 'Student Info:' as info;
SELECT id, username, batch, yearOfStudy, course FROM users WHERE role = 'STUDENT';

SELECT 'Attendance Records:' as info;
SELECT a.id, u.username, u.batch, u.yearOfStudy, c.name as course_name, a.date, a.present 
FROM attendance a 
JOIN users u ON a.student_id = u.id 
JOIN courses c ON a.course_id = c.id 
ORDER BY a.date DESC;