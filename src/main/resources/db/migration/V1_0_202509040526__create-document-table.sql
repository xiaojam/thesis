CREATE TABLE IF NOT EXISTS document_config (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workplace_id UUID REFERENCES master(id) ON DELETE SET NULL,
    head_name TEXT,
    numbering_format TEXT,
    last_sequence INTEGER DEFAULT 0,
    description TEXT,
    service_type TEXT,

    version INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by TEXT DEFAULT 'system',
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by TEXT DEFAULT 'system'
);

CREATE TABLE IF NOT EXISTS document (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    config_id UUID REFERENCES document_config(id) ON DELETE CASCADE,
    number TEXT,
    title TEXT,
    content TEXT,
    file_path TEXT,
    issued_at TIMESTAMP WITH TIME ZONE,

    version INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by TEXT DEFAULT 'system',
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by TEXT DEFAULT 'system'
);

CREATE TABLE IF NOT EXISTS document_bundle (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    service_type TEXT,
    reference_id UUID,
    config_id UUID REFERENCES document_config(id) ON DELETE SET NULL,
    number TEXT,
    issued_at TIMESTAMP WITH TIME ZONE,

    version INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by TEXT DEFAULT 'system',
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by TEXT DEFAULT 'system'
);