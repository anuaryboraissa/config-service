-- Add 'attempts' column with default 0
ALTER TABLE gepg.outbox_message
ADD COLUMN IF NOT EXISTS attempts INT DEFAULT 0 NOT NULL;

-- Add 'last_error_message' column, nullable
ALTER TABLE gepg.outbox_message
ADD COLUMN IF NOT EXISTS last_error_message TEXT;
