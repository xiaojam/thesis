ALTER TABLE bride ADD COLUMN IF NOT EXISTS bride_father_id UUID NULL;
ALTER TABLE bride ADD COLUMN IF NOT EXISTS bride_mother_id UUID NULL;
ALTER TABLE bride ADD COLUMN IF NOT EXISTS previous_partner_id UUID NULL;
ALTER TABLE bride ADD COLUMN IF NOT EXISTS guardian_id UUID NULL;

ALTER TABLE bride ADD CONSTRAINT fk_bride_father FOREIGN KEY (bride_father_id) REFERENCES bride_father(id);
ALTER TABLE bride ADD CONSTRAINT fk_bride_mother FOREIGN KEY (bride_mother_id) REFERENCES bride_mother(id);
ALTER TABLE bride ADD CONSTRAINT fk_previous_partner FOREIGN KEY (previous_partner_id) REFERENCES previous_partner(id);
ALTER TABLE bride ADD CONSTRAINT fk_guardian FOREIGN KEY (guardian_id) REFERENCES guardian(id);

CREATE INDEX IF NOT EXISTS idx_bride_on_bride_father_id ON bride (bride_father_id);
CREATE INDEX IF NOT EXISTS idx_bride_on_bride_mother_id ON bride (bride_mother_id);
CREATE INDEX IF NOT EXISTS idx_bride_on_previous_partner_id ON bride (previous_partner_id);
CREATE INDEX IF NOT EXISTS idx_bride_on_guardian_id ON bride (guardian_id);

CREATE INDEX IF NOT EXISTS idx_bride_on_identity_id ON bride (identity_id);
