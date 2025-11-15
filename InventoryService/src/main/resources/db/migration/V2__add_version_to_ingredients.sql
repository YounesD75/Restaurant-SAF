-- Add version column for optimistic locking
ALTER TABLE ingredients
ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;
