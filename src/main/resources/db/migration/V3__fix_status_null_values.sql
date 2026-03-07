-- Fix existing NULL values
UPDATE tiny_links
SET status = 'ACTIVE'
WHERE status IS NULL;

-- Drop and recreate the constraint
ALTER TABLE tiny_links
    DROP CONSTRAINT IF EXISTS check_tiny_links_status;

ALTER TABLE tiny_links
    ADD CONSTRAINT check_tiny_links_status
        CHECK (status IN ('ACTIVE', 'INACTIVE', 'EXPIRED', 'ARCHIVED', 'BANNED'));