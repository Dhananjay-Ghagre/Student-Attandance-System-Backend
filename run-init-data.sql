-- Run this to populate the database with sample data
-- Make sure to run this in your MySQL database

USE attendance_db;

-- Insert sample courses first
INSERT IGNORE INTO courses (id, name, code, teacher_id) VALUES
(1, 'Mathematics 101', 'MATH101', 2),
(2, 'Physics 101', 'PHYS101', 2),
(3, 'Chemistry 101', 'CHEM101', 3),
(4, 'Computer Science 101', 'CS101', 3);