CREATE TABLE divorce_reason (
    id TEXT PRIMARY KEY,
    divorce_case_id UUID UNIQUE,
    initial_situation TEXT,
    conflict_reason TEXT,
    reconciliation_attempt TEXT,
    current_condition TEXT,

    version INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by TEXT DEFAULT 'system',
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by TEXT DEFAULT 'system'
);