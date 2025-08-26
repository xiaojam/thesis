ALTER TABLE application ADD COLUMN IF NOT EXISTS type TEXT NOT NULL DEFAULT 'MARRIAGE';

ALTER TABLE groom ADD COLUMN IF NOT EXISTS application_id UUID NULL;

ALTER TABLE bride ADD COLUMN IF NOT EXISTS application_id UUID NULL;

ALTER TABLE marriage ADD COLUMN IF NOT EXISTS application_id UUID NULL;

ALTER TABLE groom ADD CONSTRAINT fk_groom_to_application FOREIGN KEY (application_id) REFERENCES application(id);

ALTER TABLE bride ADD CONSTRAINT fk_bride_to_application FOREIGN KEY (application_id) REFERENCES application(id);

ALTER TABLE marriage ADD CONSTRAINT fk_marriage_to_application FOREIGN KEY (application_id) REFERENCES application(id);
