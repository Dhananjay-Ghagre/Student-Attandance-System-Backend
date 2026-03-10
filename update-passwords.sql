-- Update existing users with correct BCrypt hashes
-- Run this if you already have users in the database with wrong passwords

UPDATE users SET password = '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.' WHERE username = 'admin';
UPDATE users SET password = '$2a$10$dXJ3SW6G7P9wuQoH.Sh7cOUcYzOzm6oa2M6/E0XU5JbcQobc/TsO.' WHERE username = 'teacher1';
UPDATE users SET password = '$2a$10$dXJ3SW6G7P9wuQoH.Sh7cOUcYzOzm6oa2M6/E0XU5JbcQobc/TsO.' WHERE username = 'teacher2';
UPDATE users SET password = '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW' WHERE username = 'student1';
UPDATE users SET password = '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW' WHERE username = 'student2';
UPDATE users SET password = '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW' WHERE username = 'student3';