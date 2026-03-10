USE attendance_db;

INSERT INTO users (username, password, email, full_name, role) VALUES
('admin', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.', 'admin@college.edu', 'System Administrator', 'ADMIN'),
('teacher1', '$2a$10$dXJ3SW6G7P9wuQoH.Sh7cOUcYzOzm6oa2M6/E0XU5JbcQobc/TsO.', 'teacher1@college.edu', 'John Smith', 'TEACHER'),
('student1', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 'student1@college.edu', 'Alice Johnson', 'STUDENT');