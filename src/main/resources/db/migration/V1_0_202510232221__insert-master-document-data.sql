BEGIN;

INSERT INTO document_config (workplace_id, head_name, numbering_format, last_sequence, service_type, description)
SELECT
    m.id,
    'Sutoyo Priono, S.Ag., M.Si.I.',
    'B.{{SEQ_3}}/Kua.{{KODE_KEC}}/PW.01/{{ROMAN_MONTH}}/{{YYYY}}',
    0,
    'MARRIAGE',
    '-'
FROM master m
WHERE m.code = '3525151' AND m.group_name = 'KUA'
LIMIT 1;

INSERT INTO document_config (workplace_id, head_name, numbering_format, last_sequence, service_type, description)
SELECT
    m.id,
    'Machrush Aliy, S.Kom., M.M.T.',
    '474.2/{{SEQ_3}}/DCK/{{ROMAN_MONTH}}/{{YYYY}}',
    0,
    'MARRIAGE',
    '-'
FROM master m
WHERE m.code = '35.25.15.2013' AND m.group_name = 'WILAYAH' AND m.description = 'Desa'
LIMIT 1;

INSERT INTO document_config (workplace_id, head_name, numbering_format, last_sequence, service_type, description)
SELECT
    m.id,
    'Dr. H. Muslikin, M.H.',
    '{{SEQ_4}}/Pdt.G/{{YYYY}}/PA.GS',
    0,
    'DIVORCE',
    '-'
FROM master m
WHERE m.code = '401294' AND m.group_name = 'PENGADILAN_AGAMA'
LIMIT 1;

INSERT INTO document_template (config_id, document_type, name, file_path)
SELECT
    dc.id,
    'N1_GROOM',
    'Model N1',
    'template-n1-groom.html'
FROM document_config dc
JOIN master m ON dc.workplace_id = m.id
WHERE m.code = '35.25.15.2013' AND dc.service_type = 'MARRIAGE'
LIMIT 1;

INSERT INTO document_template (config_id, document_type, name, file_path)
SELECT
    dc.id,
    'N1_BRIDE',
    'Model N1',
    'template-n1-bride.html'
FROM document_config dc
JOIN master m ON dc.workplace_id = m.id
WHERE m.code = '35.25.15.2013' AND dc.service_type = 'MARRIAGE'
LIMIT 1;

INSERT INTO document_template (config_id, document_type, name, file_path)
SELECT
    dc.id,
    'N2_GROOM',
    'Model N2',
    'template-n2-groom.html'
FROM document_config dc
JOIN master m ON dc.workplace_id = m.id
WHERE m.code = '35.25.15.2013' AND dc.service_type = 'MARRIAGE'
LIMIT 1;

INSERT INTO document_template (config_id, document_type, name, file_path)
SELECT
    dc.id,
    'N2_BRIDE',
    'Model N2',
    'template-n2-bride.html'
FROM document_config dc
JOIN master m ON dc.workplace_id = m.id
WHERE m.code = '35.25.15.2013' AND dc.service_type = 'MARRIAGE'
LIMIT 1;

INSERT INTO document_template (config_id, document_type, name, file_path)
SELECT
    dc.id,
    'N4_GROOM',
    'Model N4',
    'template-n4-groom.html'
FROM document_config dc
JOIN master m ON dc.workplace_id = m.id
WHERE m.code = '35.25.15.2013' AND dc.service_type = 'MARRIAGE'
LIMIT 1;

INSERT INTO document_template (config_id, document_type, name, file_path)
SELECT
    dc.id,
    'N4_BRIDE',
    'Model N4',
    'template-n4-bride.html'
FROM document_config dc
JOIN master m ON dc.workplace_id = m.id
WHERE m.code = '35.25.15.2013' AND dc.service_type = 'MARRIAGE'
LIMIT 1;

INSERT INTO document_template (config_id, document_type, name, file_path)
SELECT
    dc.id,
    'N5_GROOM',
    'Model N5',
    'template-n5-groom.html'
FROM document_config dc
JOIN master m ON dc.workplace_id = m.id
WHERE m.code = '35.25.15.2013' AND dc.service_type = 'MARRIAGE'
LIMIT 1;

INSERT INTO document_template (config_id, document_type, name, file_path)
SELECT
    dc.id,
    'N5_BRIDE',
    'Model N5',
    'template-n5-bride.html'
FROM document_config dc
JOIN master m ON dc.workplace_id = m.id
WHERE m.code = '35.25.15.2013' AND dc.service_type = 'MARRIAGE'
LIMIT 1;

INSERT INTO document_template (config_id, document_type, name, file_path)
SELECT
    dc.id,
    'N6_GROOM',
    'Model N6',
    'template-n6-groom.html'
FROM document_config dc
JOIN master m ON dc.workplace_id = m.id
WHERE m.code = '35.25.15.2013' AND dc.service_type = 'MARRIAGE'
LIMIT 1;

INSERT INTO document_template (config_id, document_type, name, file_path)
SELECT
    dc.id,
    'N6_BRIDE',
    'Model N6',
    'template-n6-bride.html'
FROM document_config dc
JOIN master m ON dc.workplace_id = m.id
WHERE m.code = '35.25.15.2013' AND dc.service_type = 'MARRIAGE'
LIMIT 1;

INSERT INTO document_template (config_id, document_type, name, file_path)
SELECT
    dc.id,
    'WN',
    'Wali Nikah',
    'template-wn-bride.html'
FROM document_config dc
JOIN master m ON dc.workplace_id = m.id
WHERE m.code = '35.25.15.2013' AND dc.service_type = 'MARRIAGE'
LIMIT 1;

INSERT INTO document_template (config_id, document_type, name, file_path)
SELECT
    dc.id,
    'UPDATE_HISTORY',
    'Riwayat Perubahan Data',
    'template-update-history.html'
FROM document_config dc
JOIN master m ON dc.workplace_id = m.id
WHERE m.code = '35.25.15.2013' AND dc.service_type = 'MARRIAGE'
LIMIT 1;

INSERT INTO document_template (config_id, document_type, name, file_path)
SELECT
    dc.id,
    'COMPLETE',
    'Surat Gugatan Cerai Penuh',
    'template-surat-gugatan-penuh.html'
FROM document_config dc
JOIN master m ON dc.workplace_id = m.id
WHERE m.code = '401294' AND dc.service_type = 'DIVORCE'
LIMIT 1;

INSERT INTO document_template (config_id, document_type, name, file_path)
SELECT
    dc.id,
    'PROPERTY',
    'Surat Gugatan Cerai Harta Bersama',
    'template-surat-gugatan-harta-bersama.html'
FROM document_config dc
JOIN master m ON dc.workplace_id = m.id
WHERE m.code = '401294' AND dc.service_type = 'DIVORCE'
LIMIT 1;

INSERT INTO document_template (config_id, document_type, name, file_path)
SELECT
    dc.id,
    'CHILD_CUSTODY',
    'Surat Gugatan Cerai Hak Anak',
    'template-surat-gugatan-hak-anak.html'
FROM document_config dc
JOIN master m ON dc.workplace_id = m.id
WHERE m.code = '401294' AND dc.service_type = 'DIVORCE'
LIMIT 1;

INSERT INTO document_template (config_id, document_type, name, file_path)
SELECT
    dc.id,
    'BASIC',
    'Surat Gugatan Cerai',
    'template-surat-gugatan.html'
FROM document_config dc
JOIN master m ON dc.workplace_id = m.id
WHERE m.code = '401294' AND dc.service_type = 'DIVORCE'
LIMIT 1;

COMMIT;