INSERT INTO drivers (id, first_name, last_name, phone, license_number, status, version, created_at)
SELECT
    ('20000000-0000-0000-0000-' || lpad(n::text, 12, '0'))::uuid,
    (ARRAY['Youssef','Salma','Karim','Imane','Hamza','Nadia','Omar','Rachid','Ali','Sara',
     'Mehdi','Khadija','Anas','Fatima','Yassine','Hajar','Othmane','Zineb','Amine','Leila'])[n],
    (ARRAY['Bennani','El Idrissi','Alaoui','Tazi','Ouazzani','Chraibi','Lahlou','Amrani','Bennis','Mansouri',
           'Sqalli','Berrada','Kettani','Fassi','Naciri','Benjelloun','Tahiri','Squalli','Rami','Haddad'])[n],
    '+2126' || lpad((10000000 + n)::text, 8, '0'),
    'CAS-' || lpad(n::text, 6, '0'),
    CASE WHEN n <= 18 THEN 'AVAILABLE' ELSE 'OFF_DUTY' END,
    0,
    now()
FROM generate_series(1, 20) AS n
ON CONFLICT (id) DO NOTHING;

INSERT INTO vehicles (id, plate, model, capacity_kg, status, current_driver_id, version, created_at, updated_at)
SELECT
    ('10000000-0000-0000-0000-' || lpad(n::text, 12, '0'))::uuid,
    (12340 + n)::text || '-' || (ARRAY['A','B','C','D'])[1 + n % 4] || '-6',
    (ARRAY['Renault Master','Peugeot Boxer','Iveco Daily','Fiat Ducato'])[1 + n % 4],
    (ARRAY[1500, 1200, 2000, 1400])[1 + n % 4],
    CASE WHEN n = 19 THEN 'MAINTENANCE'
         WHEN n = 20 THEN 'OUT_OF_SERVICE'
         ELSE 'AVAILABLE' END,
    CASE WHEN n <= 15 THEN ('20000000-0000-0000-0000-' || lpad(n::text, 12, '0'))::uuid END,
    0,
    now(),
    now()
FROM generate_series(1, 20) AS n
ON CONFLICT (id) DO NOTHING;
