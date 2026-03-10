-- Sample data for Student Attendance System
-- Run this after the application creates the tables

-- Insert sample users (passwords are BCrypt encoded)
-- admin123 = $2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.
-- teacher123 = $2a$10$dXJ3SW6G7P9wuQoH.Sh7cOUcYzOzm6oa2M6/E0XU5JbcQobc/TsO.
-- student123 = $2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW
INSERT INTO users (username, password, email, full_name, role) VALUES
('admin', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.', 'admin@college.edu', 'System Administrator', 'ADMIN'),
('teacher1', '$2a$10$dXJ3SW6G7P9wuQoH.Sh7cOUcYzOzm6oa2M6/E0XU5JbcQobc/TsO.', 'teacher1@college.edu', 'John Smith', 'TEACHER'),
('teacher2', '$2a$10$dXJ3SW6G7P9wuQoH.Sh7cOUcYzOzm6oa2M6/E0XU5JbcQobc/TsO.', 'teacher2@college.edu', 'Jane Doe', 'TEACHER'),
('student1', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 'student1@college.edu', 'Alice Johnson', 'STUDENT'),
('student2', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 'student2@college.edu', 'Bob Wilson', 'STUDENT'),
('student3', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 'student3@college.edu', 'Carol Brown', 'STUDENT');

-- Insert sample courses
INSERT INTO courses (name, code, teacher_id) VALUES
('Mathematics 101', 'MATH101', 2),
('Physics 101', 'PHYS101', 2),
('Chemistry 101', 'CHEM101', 3),
('Computer Science 101', 'CS101', 3);

-- Insert course enrollments
INSERT INTO course_enrollments (student_id, course_id) VALUES
(4, 1), -- student1 enrolled in Mathematics 101
(4, 2), -- student1 enrolled in Physics 101
(5, 1), -- student2 enrolled in Mathematics 101
(5, 2), -- student2 enrolled in Physics 101
(6, 3), -- student3 enrolled in Chemistry 101
(6, 4); -- student3 enrolled in Computer Science 101

-- Insert sample attendance records
INSERT INTO attendance (student_id, course_id, date, present) VALUES
(4, 1, '2024-01-15', true),
(4, 1, '2024-01-16', true),
(4, 1, '2024-01-17', false),
(4, 2, '2024-01-15', true),
(4, 2, '2024-01-16', false),
(5, 1, '2024-01-15', true),
(5, 1, '2024-01-16', false),
(5, 1, '2024-01-17', true),
(5, 2, '2024-01-15', false),
(5, 2, '2024-01-16', true),
(6, 3, '2024-01-15', true),
(6, 3, '2024-01-16', true),
(6, 4, '2024-01-15', true),
(6, 4, '2024-01-16', false);