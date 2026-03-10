-- Check current users and their password hashes
SELECT username, password, role FROM users WHERE username IN ('admin', 'teacher1', 'student1');

-- Expected hashes:
-- admin123 -> $2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.
-- teacher123 -> $2a$10$dXJ3SW6G7P9wuQoH.Sh7cOUcYzOzm6oa2M6/E0XU5JbcQobc/TsO.
-- student123 -> $2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW