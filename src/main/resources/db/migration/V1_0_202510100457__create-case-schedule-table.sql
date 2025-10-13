CREATE TABLE case_schedule (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    divorce_case_id UUID,
    date_type TEXT DEFAULT 'COUNCIL_DATE',
    event_date DATE,
    process_step INTEGER,
    daily_queue_number INTEGER,
    status TEXT DEFAULT 'SCHEDULED',

    version INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by TEXT DEFAULT 'system',
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by TEXT DEFAULT 'system',

    CONSTRAINT fk_schedule_case_on_divorce_case
        FOREIGN KEY (divorce_case_id)
        REFERENCES divorce_case (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_case_schedule_divorce_case_id ON case_schedule(divorce_case_id);
CREATE INDEX idx_case_schedule_event_date ON case_schedule(event_date);