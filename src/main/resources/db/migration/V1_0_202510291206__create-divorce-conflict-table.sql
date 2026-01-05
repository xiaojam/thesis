CREATE TABLE divorce_conflict_causes (
    reason_id UUID NOT NULL,
    cause TEXT,
    CONSTRAINT fk_causes_to_reason FOREIGN KEY (reason_id) REFERENCES divorce_reason(id)
);