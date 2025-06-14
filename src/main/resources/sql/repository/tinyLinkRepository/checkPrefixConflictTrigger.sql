CREATE OR REPLACE FUNCTION check_prefix_conflict()
    RETURNS TRIGGER AS
$$
BEGIN
    IF EXISTS (SELECT 1
               FROM prefixes
               WHERE LOWER(LEFT(NEW.tiny_code, LENGTH(prefix))) = LOWER(prefix)) THEN
        RAISE EXCEPTION 'Prefix conflict detected';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS check_prefix_before_insert ON tiny_links;

CREATE TRIGGER check_prefix_before_insert
    BEFORE INSERT
    ON tiny_links
    FOR EACH ROW
EXECUTE FUNCTION check_prefix_conflict();