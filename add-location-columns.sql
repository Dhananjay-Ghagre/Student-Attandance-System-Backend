-- Add location columns to attendance table
ALTER TABLE attendance 
ADD COLUMN location_latitude DECIMAL(10, 8),
ADD COLUMN location_longitude DECIMAL(11, 8);