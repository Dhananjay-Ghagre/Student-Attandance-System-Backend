-- Add onLeave column to attendance table if it doesn't exist
ALTER TABLE attendance ADD COLUMN IF NOT EXISTS on_leave BOOLEAN DEFAULT FALSE;

-- Update existing records to set onLeave = false if null
UPDATE attendance SET on_leave = FALSE WHERE on_leave IS NULL;