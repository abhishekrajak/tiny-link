ALTER TABLE tiny_links DROP CONSTRAINT check_tiny_links_status;

ALTER TABLE tiny_links ADD CONSTRAINT check_tiny_links_status
    CHECK (status::text = ANY (ARRAY['ACTIVE'::text, 'INACTIVE'::text, 'EXPIRED'::text, 'ARCHIVED'::text, 'BANNED'::text, 'USER_IS_DELETED'::text]));