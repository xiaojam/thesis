CREATE TABLE divorce_case (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    application_id UUID UNIQUE,
    case_type TEXT,
    plaintiff_id UUID,
    defendant_id UUID,
    marriage_data_id UUID UNIQUE,
    reconciliation_attempt_description TEXT,
    iddah_support_amount DOUBLE PRECISION,
    mutah_description TEXT,
    maddiyah_support_amount DOUBLE PRECISION,
    maddiyah_duration_in_months INTEGER,

    version INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by TEXT DEFAULT 'system',
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by TEXT DEFAULT 'system'
);