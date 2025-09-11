ALTER TABLE marriage
    ADD COLUMN bride_id UUID,
    ADD COLUMN groom_id UUID;

ALTER TABLE marriage
    ADD CONSTRAINT fk_marriage_bride FOREIGN KEY (bride_id)
        REFERENCES bride(id) ON DELETE CASCADE;

ALTER TABLE marriage
    ADD CONSTRAINT fk_marriage_groom FOREIGN KEY (groom_id)
        REFERENCES groom(id) ON DELETE CASCADE;