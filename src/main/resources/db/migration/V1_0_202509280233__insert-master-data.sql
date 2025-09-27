WITH kabupaten_malang AS (
    SELECT id FROM master WHERE code = '35.07' AND group_name = 'WILAYAH'
),
kabupaten_gresik AS (
    SELECT id FROM master WHERE code = '35.25' AND group_name = 'WILAYAH'
)

INSERT INTO master (group_name, code, name, parent_id, description)
SELECT
    'PENGADILAN_AGAMA' AS group_name,
    '401427' AS code,
    'Pengadilan Agama Kab. Malang' AS name,
    km.id AS parent_id,
    'Pengadilan Agama' AS description
FROM kabupaten_malang km

UNION ALL

SELECT
    'PENGADILAN_AGAMA' AS group_name,
    '401294' AS code,
    'Pengadilan Agama Kab. Gresik' AS name,
    kg.id AS parent_id,
    'Pengadilan Agama' AS description
FROM kabupaten_gresik kg;