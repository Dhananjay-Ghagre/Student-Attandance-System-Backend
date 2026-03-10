-- Add onLeave column to attendance table
-- Run this SQL script in your database

USE attendance_db;

-- Add the onLeave column to the attendance table
ALTER TABLE attendance ADD COLUMN on_leave BOOLEAN NOT NULL DEFAULT FALSE;

-- Update existing records to set on_leave = false (default)
UPDATE attendance SET on_leave = FALSE WHERE on_leave IS NULL;