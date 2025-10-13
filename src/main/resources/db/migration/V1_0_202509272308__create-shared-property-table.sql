CREATE TABLE shared_property (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    property_claim_id TEXT,
    property_type TEXT,
    description TEXT,
    estimated_value DOUBLE PRECISION,
    ownership_proof TEXT,

    version INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by TEXT DEFAULT 'system',
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by TEXT DEFAULT 'system'
);