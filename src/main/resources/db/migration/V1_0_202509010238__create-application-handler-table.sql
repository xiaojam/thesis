CREATE TABLE IF NOT EXISTS application_handler (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    application_id UUID NULL,
    username TEXT NULL,
    role TEXT NOT NULL DEFAULT 'DEFAULT',
    workplace_code TEXT NULL,

    version INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by TEXT DEFAULT 'system',
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by TEXT DEFAULT 'system'
);


ALTER TABLE application_handler
    ADD CONSTRAINT fk_application_handler_to_application
    FOREIGN KEY (application_id)
    REFERENCES application(id);
