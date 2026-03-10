-- Fix login passwords with correct BCrypt hashes
-- These hashes correspond to: admin123, teacher123, student123

-- Update admin password (admin123)
UPDATE users SET password = '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.' WHERE username = 'admin';

-- Update teacher passwords (teacher123)
UPDATE users SET password = '$2a$10$dXJ3SW6G7P9wuQoH.Sh7cOUcYzOzm6oa2M6/E0XU5JbcQobc/TsO.' WHERE username = 'teacher1';
UPDATE users SET password = '$2a$10$dXJ3SW6G7P9wuQoH.Sh7cOUcYzOzm6oa2M6/E0XU5JbcQobc/TsO.' WHERE username = 'teacher2';

-- Update student passwords (student123)
UPDATE users SET password = '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW' WHERE username = 'student1';
UPDATE users SET password = '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW' WHERE username = 'student2';
UPDATE users SET password = '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW' WHERE username = 'student3';

-- Verify the updates
SELECT username, password FROM users WHERE username IN ('admin', 'teacher1', 'teacher2', 'student1', 'student2', 'student3');