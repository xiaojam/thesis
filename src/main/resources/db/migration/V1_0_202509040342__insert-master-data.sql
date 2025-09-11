-- ===========================
-- Insert Provinsi Jawa Timur
-- ===========================
WITH provinsi AS (
    INSERT INTO master (group_name, code, name, description)
    VALUES ('WILAYAH', '35', 'Jawa Timur', 'Provinsi')
    RETURNING id
),
kabupaten_malang AS (
    INSERT INTO master (group_name, code, name, parent_id, description)
    SELECT 'WILAYAH', '35.07', 'Kabupaten Malang', p.id, 'Kabupaten'
    FROM provinsi p
    RETURNING id
),
kabupaten_gresik AS (
    INSERT INTO master (group_name, code, name, parent_id, description)
    SELECT 'WILAYAH', '35.25', 'Kabupaten Gresik', p.id, 'Kabupaten'
    FROM provinsi p
    RETURNING id
),

-- ===========================
-- Insert Kecamatan Turen (Malang)
-- ===========================
kecamatan_turen AS (
    INSERT INTO master (group_name, code, name, parent_id, description)
    SELECT 'WILAYAH', '35.07.09', 'Turen', km.id, 'Kecamatan'
    FROM kabupaten_malang km
    RETURNING id
),
desa_turen AS (
    INSERT INTO master (group_name, code, name, parent_id, description)
    SELECT 'WILAYAH', v.code, v.name, kt.id, v.description
    FROM kecamatan_turen kt
    JOIN (
        VALUES
            ('35.07.09.2001', 'Sananrejo', 'Desa'),
            ('35.07.09.2002', 'Sananan Kerto', 'Desa'),
            ('35.07.09.2003', 'Talok', 'Desa'),
            ('35.07.09.2004', 'Sawahan', 'Desa'),
            ('35.07.09.2005', 'Pagedangan', 'Desa'),
            ('35.07.09.2006', 'Tawang Rejeni', 'Desa'),
            ('35.07.09.2007', 'Undaan', 'Desa'),
            ('35.07.09.2008', 'Tangkilsari', 'Desa'),
            ('35.07.09.1009', 'Turen', 'Kelurahan'),
            ('35.07.09.2010', 'Tumpuk Renteng', 'Desa'),
            ('35.07.09.2011', 'Jambuwer', 'Desa'),
            ('35.07.09.2012', 'Kedok', 'Desa'),
            ('35.07.09.2013', 'Talangsuko', 'Desa'),
            ('35.07.09.2014', 'Jeru', 'Desa'),
            ('35.07.09.2015', 'Gedog Wetan', 'Desa'),
            ('35.07.09.2016', 'Gedog Kulon', 'Desa'),
            ('35.07.09.2017', 'Kemantren', 'Desa')
    ) AS v(code, name, description)
    ON TRUE
),
kua_turen AS (
    INSERT INTO master (group_name, code, name, parent_id, description)
    SELECT 'KUA', '3507091', 'KUA Kecamatan Turen', kt.id, 'KUA'
    FROM kecamatan_turen kt
    RETURNING id
),

-- ===========================
-- Insert Kecamatan Driyorejo (Gresik)
-- ===========================
kecamatan_driyorejo AS (
    INSERT INTO master (group_name, code, name, parent_id, description)
    SELECT 'WILAYAH', '35.25.15', 'Driyorejo', kg.id, 'Kecamatan'
    FROM kabupaten_gresik kg
    RETURNING id
),
desa_driyorejo AS (
    INSERT INTO master (group_name, code, name, parent_id, description)
    SELECT 'WILAYAH', v.code, v.name, kd.id, v.description
    FROM kecamatan_driyorejo kd
    JOIN (
        VALUES
            ('35.25.15.2001', 'Mojosarirejo', 'Desa'),
            ('35.25.15.2002', 'Krikilan', 'Desa'),
            ('35.25.15.2003', 'Driyorejo', 'Desa'),
            ('35.25.15.2004', 'Tanjungan', 'Desa'),
            ('35.25.15.2005', 'Karangandong', 'Desa'),
            ('35.25.15.2006', 'Kesamben Wetan', 'Desa'),
            ('35.25.15.2007', 'Sumput', 'Desa'),
            ('35.25.15.2008', 'Petiken', 'Desa'),
            ('35.25.15.2009', 'Randegansari', 'Desa'),
            ('35.25.15.2010', 'Tenaru', 'Desa'),
            ('35.25.15.2011', 'Mulung', 'Desa'),
            ('35.25.15.2012', 'Gadung', 'Desa'),
            ('35.25.15.2013', 'Cangkir', 'Desa'),
            ('35.25.15.2014', 'Wedoroanom', 'Desa'),
            ('35.25.15.2015', 'Banjaran', 'Desa'),
            ('35.25.15.2016', 'Bambe', 'Desa')
    ) AS v(code, name, description)
    ON TRUE
)
INSERT INTO master (group_name, code, name, parent_id, description)
SELECT 'KUA', '3525151', 'KUA Kecamatan Driyorejo', kd.id, 'KUA'
FROM kecamatan_driyorejo kd;