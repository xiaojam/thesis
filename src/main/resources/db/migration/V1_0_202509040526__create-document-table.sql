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

CREATE TABLE IF NOT EXISTS document_template (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    config_id UUID REFERENCES document_config(id) ON DELETE SET NULL,
    document_type TEXT DEFAULT 'NONE',
    name TEXT,
    file_path TEXT,

    version INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by TEXT DEFAULT 'system',
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by TEXT DEFAULT 'system'
);

CREATE TABLE IF NOT EXISTS generated_document (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    application_id UUID REFERENCES applications(id) ON DELETE SET NULL,
    document_template_id UUID REFERENCES document_templates(id) ON DELETE SET NULL,
    document_number TEXT,
    file_path TEXT,
    data_snapshot JSONB,
    issued_at TIMESTAMP WITH TIME ZONE,

    version INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by TEXT DEFAULT 'system',
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by TEXT DEFAULT 'system'
);

CREATE INDEX IF NOT EXISTS idx_generated_document_application_id ON generated_document(application_id);
CREATE INDEX IF NOT EXISTS idx_generated_document_template_id ON generated_document(document_template_id);

CREATE INDEX IF NOT EXISTS idx_document_template_config_id ON document_template(config_id);
CREATE INDEX IF NOT EXISTS idx_document_template_document_type ON document_template(document_type);