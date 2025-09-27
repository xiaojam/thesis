CREATE TABLE marriage_data (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    marriage_date DATE,
    marriage_place TEXT,
    marriage_certificate_number TEXT,
    household_address TEXT,
    has_children BOOLEAN,

    version INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by TEXT DEFAULT 'system',
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by TEXT DEFAULT 'system'
);