CREATE TABLE tiny_link_analytics_event (
    id UUID PRIMARY KEY,
    tiny_code VARCHAR(20) NOT NULL,
    time_stamp TIMESTAMP WITH TIME ZONE NOT NULL,
    ip_address INET,
    user_agent TEXT,
    referer VARCHAR(512)
);

CREATE INDEX idx_tiny_link_analytics_event_tiny_code ON tiny_link_analytics_event(tiny_code);
CREATE INDEX idx_tiny_link_analytics_event_created_at ON tiny_link_analytics_event(time_stamp);

