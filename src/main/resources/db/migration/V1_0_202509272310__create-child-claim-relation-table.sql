CREATE TABLE child_claim_relation (
    child_claim_id TEXT NOT NULL,
    child_id TEXT NOT NULL,
    PRIMARY KEY (child_claim_id, child_id)
);