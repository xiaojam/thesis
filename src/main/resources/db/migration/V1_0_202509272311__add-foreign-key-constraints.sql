ALTER TABLE divorce_case ADD CONSTRAINT fk_divorce_case_plaintiff FOREIGN KEY (plaintiff_id) REFERENCES plaintiff(id);
ALTER TABLE divorce_case ADD CONSTRAINT fk_divorce_case_defendant FOREIGN KEY (defendant_id) REFERENCES defendant(id);
ALTER TABLE divorce_case ADD CONSTRAINT fk_divorce_case_marriage_data FOREIGN KEY (marriage_data_id) REFERENCES marriage_data(id);

ALTER TABLE divorce_reason ADD CONSTRAINT fk_divorce_reason_divorce_case FOREIGN KEY (divorce_case_id) REFERENCES divorce_case(id);

ALTER TABLE property_claim ADD CONSTRAINT fk_property_claim_divorce_case FOREIGN KEY (divorce_case_id) REFERENCES divorce_case(id);

ALTER TABLE shared_property ADD CONSTRAINT fk_shared_property_property_claim FOREIGN KEY (property_claim_id) REFERENCES property_claim(id);

ALTER TABLE child_claim ADD CONSTRAINT fk_child_claim_divorce_case FOREIGN KEY (divorce_case_id) REFERENCES divorce_case(id);

ALTER TABLE child_claim_relation ADD CONSTRAINT fk_relation_to_child_claim FOREIGN KEY (child_claim_id) REFERENCES child_claim(id);
ALTER TABLE child_claim_relation ADD CONSTRAINT fk_relation_to_child FOREIGN KEY (child_id) REFERENCES child(id);