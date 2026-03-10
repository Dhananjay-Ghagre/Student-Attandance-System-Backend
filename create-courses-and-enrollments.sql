USE attendance_db;

-- Create courses (assuming teacher1 has ID 2)
INSERT INTO courses (name, code, teacher_id) VALUES
('Mathematics 101', 'MATH101', 2),
('Physics 101', 'PHYS101', 2),
('Chemistry 101', 'CHEM101', 2);

-- Enroll student1 (assuming student1 has ID 3) in Mathematics 101 (course ID 1)
INSERT INTO course_enrollments (student_id, course_id) VALUES
(3, 1);

-- Verify the data
SELECT 'Users:' as table_name;
SELECT id, username, role FROM users;

SELECT 'Courses:' as table_name;
SELECT * FROM courses;

SELECT 'Enrollments:' as table_name;
SELECT ce.id, u.username as student, c.name as course 
FROM course_enrollments ce 
JOIN users u ON ce.student_id = u.id 
JOIN courses c ON ce.course_id = c.id;