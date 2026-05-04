-- ניקוי טבלאות בסדר הפוך לקשרים (Foreign Keys)
DELETE FROM station_in_line;
DELETE FROM travel;
DELETE FROM station;
DELETE FROM bus_line;
DELETE FROM bus;
DELETE FROM driver;

-- 1. הוספת נהגים (Drivers)
INSERT INTO driver (id, name, license_number, phone, rating, is_active, deletion_requested, role, email, password) VALUES 
(1, 'אברהם לוי', 'D12345', '0501234567', 5, true, false, 'ROLE_ADMIN', 'admin@test.com', '$2a$12$R.3Vq.fFCHmDofGqJvI9OufGv.3vR1/jE.R2pYp9vP/qfKzI7hR1q'),
(2, 'משה כהן', 'D67890', '0529876543', 4, true, false, 'ROLE_DRIVER', 'moshe@test.com', 'none'),
(3, 'יצחק ישראלי', 'D11223', '0544433221', 3, true, false, 'ROLE_DRIVER', 'itzik@test.com', 'none'),
(4, 'דוד מזרחי', 'D44556', '0587654321', 5, true, false, 'ROLE_DRIVER', 'david@test.com', 'none'),
(5, 'שמעון פרץ', 'D99887', '0531122334', 2, true, false, 'ROLE_DRIVER', 'shimon@test.com', 'none');

-- 2. הוספת אוטובוסים (Buses)
INSERT INTO bus (id, license_plate, capacity, status, last_test_date, is_active) VALUES 
(1, '1122233', 55, 'AVAILABLE', '2025-01-01', true), 
(2, '4455566', 50, 'AVAILABLE', '2024-11-20', true), 
(3, '7788899', 40, 'MAINTENANCE', '2024-05-10', true),
(4, '3344455', 60, 'AVAILABLE', '2025-02-01', true), 
(5, '9900011', 55, 'OUT_OF_SERVICE', '2023-12-15', true);

-- 3. הוספת קווי אוטובוס (Bus Lines)
INSERT INTO bus_line (id, line_number, origin, destination, is_active) VALUES 
(1, 402, 'בני ברק', 'ירושלים', true),
(2, 422, 'בני ברק', 'ירושלים', true),
(3, 350, 'אשדוד', 'בני ברק', true);

-- 4. הוספת תחנות (Stations)
INSERT INTO station (id, name, address, is_active) VALUES 
(1, 'חזון איש/רבי עקיבא', 'חזון איש 10, בני ברק', true),
(2, 'עזרא/נחמיה', 'עזרא 45, בני ברק', true),
(3, 'מחלף גבעת שמואל', 'כביש 4', true),
(4, 'צומת שורש', 'כביש 1', true),
(5, 'תחנה מרכזית ירושלים', 'יפו 220, ירושלים', true),
(6, 'שרי ישראל', 'שרי ישראל 15, ירושלים', true),
(7, 'רובע ז', 'ענף החיים, אשדוד', true),
(8, 'מחלף אשדוד', 'כביש 4', true);

-- 5. בניית מסלולים (Stations In Line)
INSERT INTO station_in_line (bus_line_id, station_id, station_order, is_active) VALUES 
(1, 1, 1, true), 
(1, 2, 2, true), 
(1, 4, 3, true), 
(1, 5, 4, true),
(2, 1, 1, true),
(2, 2, 2, true),
(2, 4, 3, true),
(2, 5, 4, true);

-- 6. יצירת לו"ז נסיעות (Travels)
INSERT INTO travel (id, bus_line_id, driver_id, bus_id, departure_time, arrival_time, is_active, status) VALUES 
(1, 1, 2, 1, '2026-02-09T08:00:00', '2026-02-09T09:30:00', true, 'COMPLETED'),
(2, 1, 3, 2, '2026-02-09T08:15:00', '2026-02-09T09:45:00', true, 'COMPLETED'),
(3, 1, 4, 4, '2026-02-09T08:30:00', '2026-02-09T10:00:00', true, 'COMPLETED'),
(4, 2, 2, 1, '2026-02-10T12:00:00', '2026-02-10T13:30:00', true, 'PLANNED'),
(5, 3, 5, 2, '2026-02-10T14:00:00', '2026-02-10T15:30:00', true, 'PLANNED');

-- 7. עדכון ה-Sequences
ALTER TABLE bus ALTER COLUMN id RESTART WITH 100;
ALTER TABLE driver ALTER COLUMN id RESTART WITH 100;
ALTER TABLE bus_line ALTER COLUMN id RESTART WITH 100;
ALTER TABLE station ALTER COLUMN id RESTART WITH 100;
ALTER TABLE travel ALTER COLUMN id RESTART WITH 100;