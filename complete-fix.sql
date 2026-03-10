-- Complete fix for login issues
-- This script will ensure users exist with correct password hashes

-- First, delete existing users to avoid conflicts
DELETE FROM users WHERE username IN ('admin', 'teacher1', 'teacher2', 'student1', 'student2', 'student3');

-- Insert users with correct BCrypt hashes
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

-- Verify the users were created correctly
SELECT username, email, full_name, role FROM users WHERE username IN ('admin', 'teacher1', 'student1');

-- Test credentials:
-- Username: admin, Password: admin123
-- Username: teacher1, Password: teacher123  
-- Username: student1, Password: student123