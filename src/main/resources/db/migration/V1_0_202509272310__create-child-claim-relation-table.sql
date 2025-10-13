CREATE TABLE child_claim_relation (
    child_claim_id UUID NOT NULL,
    child_id UUID NOT NULL,
    PRIMARY KEY (child_claim_id, child_id)
);