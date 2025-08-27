ALTER TABLE groom ADD COLUMN IF NOT EXISTS groom_father_id UUID NULL;
ALTER TABLE groom ADD COLUMN IF NOT EXISTS groom_mother_id UUID NULL;
ALTER TABLE groom ADD COLUMN IF NOT EXISTS previous_partner_id UUID NULL;

ALTER TABLE groom ADD CONSTRAINT fk_groom_father FOREIGN KEY (groom_father_id) REFERENCES groom_father(id);
ALTER TABLE groom ADD CONSTRAINT fk_groom_mother FOREIGN KEY (groom_mother_id) REFERENCES groom_mother(id);
ALTER TABLE groom ADD CONSTRAINT fk_groom_previous_partner FOREIGN KEY (previous_partner_id) REFERENCES previous_partner(id);

CREATE INDEX IF NOT EXISTS idx_groom_on_groom_father_id ON groom (groom_father_id);
CREATE INDEX IF NOT EXISTS idx_groom_on_groom_mother_id ON groom (groom_mother_id);
CREATE INDEX IF NOT EXISTS idx_groom_on_previous_partner_id ON groom (previous_partner_id);

CREATE INDEX IF NOT EXISTS idx_groom_on_identity_id ON groom (identity_id);
