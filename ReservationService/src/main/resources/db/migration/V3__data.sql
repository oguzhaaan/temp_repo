-- Pre-populate brand table
INSERT INTO brand (name)
VALUES ('Fiat') ON CONFLICT (name) DO NOTHING;
INSERT INTO brand (name)
VALUES ('Toyota') ON CONFLICT (name) DO NOTHING;
INSERT INTO brand (name)
VALUES ('Ford') ON CONFLICT (name) DO NOTHING;
INSERT INTO brand (name)
VALUES ('BMW') ON CONFLICT (name) DO NOTHING;
INSERT INTO brand (name)
VALUES ('Audi') ON CONFLICT (name) DO NOTHING;
INSERT INTO brand (name)
VALUES ('Honda') ON CONFLICT (name) DO NOTHING;
INSERT INTO brand (name)
VALUES ('Mercedes') ON CONFLICT (name) DO NOTHING;
INSERT INTO brand (name)
VALUES ('Volkswagen') ON CONFLICT (name) DO NOTHING;
INSERT INTO brand (name)
VALUES ('Nissan') ON CONFLICT (name) DO NOTHING;
INSERT INTO brand (name)
VALUES ('Chevrolet') ON CONFLICT (name) DO NOTHING;
INSERT INTO brand (name)
VALUES ('Hyundai') ON CONFLICT (name) DO NOTHING;
INSERT INTO brand (name)
VALUES ('Kia') ON CONFLICT (name) DO NOTHING;
INSERT INTO brand (name)
VALUES ('Peugeot') ON CONFLICT (name) DO NOTHING;

-- Pre-populate model table
INSERT INTO brand_model (id, model, brand)
VALUES (1, '500', 'Fiat') ON CONFLICT (id) DO NOTHING;
INSERT INTO brand_model (id, model, brand)
VALUES (2, 'Punto', 'Fiat') ON CONFLICT (id) DO NOTHING;
INSERT INTO brand_model (id, model, brand)
VALUES (3, 'Yaris', 'Toyota') ON CONFLICT (id) DO NOTHING;
INSERT INTO brand_model (id, model, brand)
VALUES (4, 'Corolla', 'Toyota') ON CONFLICT (id) DO NOTHING;
INSERT INTO brand_model (id, model, brand)
VALUES (5, 'Fiesta', 'Ford') ON CONFLICT (id) DO NOTHING;
INSERT INTO brand_model (id, model, brand)
VALUES (6, 'Focus', 'Ford') ON CONFLICT (id) DO NOTHING;
INSERT INTO brand_model (id, model, brand)
VALUES (7, '3 Series', 'BMW') ON CONFLICT (id) DO NOTHING;
INSERT INTO brand_model (id, model, brand)
VALUES (8, '5 Series', 'BMW') ON CONFLICT (id) DO NOTHING;
INSERT INTO brand_model (id, model, brand)
VALUES (9, 'A3', 'Audi') ON CONFLICT (id) DO NOTHING;
INSERT INTO brand_model (id, model, brand)
VALUES (10, 'A4', 'Audi') ON CONFLICT (id) DO NOTHING;
INSERT INTO brand_model (id, model, brand)
VALUES (11, 'Civic', 'Honda') ON CONFLICT (id) DO NOTHING;
INSERT INTO brand_model (id, model, brand)
VALUES (12, 'Accord', 'Honda') ON CONFLICT (id) DO NOTHING;
INSERT INTO brand_model (id, model, brand)
VALUES (13, 'C-Class', 'Mercedes') ON CONFLICT (id) DO NOTHING;
INSERT INTO brand_model (id, model, brand)
VALUES (14, 'E-Class', 'Mercedes') ON CONFLICT (id) DO NOTHING;
INSERT INTO brand_model (id, model, brand)
VALUES (15, 'Golf', 'Volkswagen') ON CONFLICT (id) DO NOTHING;
INSERT INTO brand_model (id, model, brand)
VALUES (16, 'Passat', 'Volkswagen') ON CONFLICT (id) DO NOTHING;
INSERT INTO brand_model (id, model, brand)
VALUES (17, 'Qashqai', 'Nissan') ON CONFLICT (id) DO NOTHING;
INSERT INTO brand_model (id, model, brand)
VALUES (18, 'Juke', 'Nissan') ON CONFLICT (id) DO NOTHING;
INSERT INTO brand_model (id, model, brand)
VALUES (19, 'Cruze', 'Chevrolet') ON CONFLICT (id) DO NOTHING;
INSERT INTO brand_model (id, model, brand)
VALUES (20, 'Spark', 'Chevrolet') ON CONFLICT (id) DO NOTHING;
INSERT INTO brand_model (id, model, brand)
VALUES (21, 'i30', 'Hyundai') ON CONFLICT (id) DO NOTHING;
INSERT INTO brand_model (id, model, brand)
VALUES (22, 'i40', 'Hyundai') ON CONFLICT (id) DO NOTHING;
INSERT INTO brand_model (id, model, brand)
VALUES (23, 'Rio', 'Kia') ON CONFLICT (id) DO NOTHING;
INSERT INTO brand_model (id, model, brand)
VALUES (24, 'Ceed', 'Kia') ON CONFLICT (id) DO NOTHING;
INSERT INTO brand_model (id, model, brand)
VALUES (25, '208', 'Peugeot') ON CONFLICT (id) DO NOTHING;
INSERT INTO brand_model (id, model, brand)
VALUES (26, '308', 'Peugeot') ON CONFLICT (id) DO NOTHING;
SELECT setval('brand_model_seq', (SELECT MAX(id) FROM brand_model));

-- Pre-populate feature table
INSERT INTO feature (id, name)
VALUES (1, 'Air Conditioning') ON CONFLICT (id) DO NOTHING;
INSERT INTO feature (id, name)
VALUES (2, 'Power Steering') ON CONFLICT (id) DO NOTHING;
INSERT INTO feature (id, name)
VALUES (3, 'Power Windows') ON CONFLICT (id) DO NOTHING;
INSERT INTO feature (id, name)
VALUES (4, 'ABS') ON CONFLICT (id) DO NOTHING;
INSERT INTO feature (id, name)
VALUES (5, 'ESP') ON CONFLICT (id) DO NOTHING;
INSERT INTO feature (id, name)
VALUES (6, 'Airbag') ON CONFLICT (id) DO NOTHING;
INSERT INTO feature (id, name)
VALUES (7, 'Central Locking') ON CONFLICT (id) DO NOTHING;
INSERT INTO feature (id, name)
VALUES (8, 'Cruise Control') ON CONFLICT (id) DO NOTHING;
INSERT INTO feature (id, name)
VALUES (9, 'Leather Seats') ON CONFLICT (id) DO NOTHING;
INSERT INTO feature (id, name)
VALUES (10, 'Heated Seats') ON CONFLICT (id) DO NOTHING;
INSERT INTO feature (id, name)
VALUES (11, 'Navigation System') ON CONFLICT (id) DO NOTHING;
INSERT INTO feature (id, name)
VALUES (12, 'Bluetooth') ON CONFLICT (id) DO NOTHING;
INSERT INTO feature (id, name)
VALUES (13, 'USB') ON CONFLICT (id) DO NOTHING;
INSERT INTO feature (id, name)
VALUES (14, 'Radio') ON CONFLICT (id) DO NOTHING;
INSERT INTO feature (id, name)
VALUES (15, 'Parking Sensors') ON CONFLICT (id) DO NOTHING;
INSERT INTO feature (id, name)
VALUES (16, 'Rear Camera') ON CONFLICT (id) DO NOTHING;
INSERT INTO feature (id, name)
VALUES (17, 'Sunroof') ON CONFLICT (id) DO NOTHING;
SELECT setval('feature_seq', (SELECT MAX(id) FROM brand_model));

-- Pre-populate segment table
INSERT INTO segment (name)
VALUES ('Compact') ON CONFLICT (name) DO NOTHING;
INSERT INTO segment (name)
VALUES ('SUV') ON CONFLICT (name) DO NOTHING;
INSERT INTO segment (name)
VALUES ('Luxury') ON CONFLICT (name) DO NOTHING;
INSERT INTO segment (name)
VALUES ('Sedan') ON CONFLICT (name) DO NOTHING;
INSERT INTO segment (name)
VALUES ('Hatchback') ON CONFLICT (name) DO NOTHING;
INSERT INTO segment (name)
VALUES ('Convertible') ON CONFLICT (name) DO NOTHING;
INSERT INTO segment (name)
VALUES ('Coupe') ON CONFLICT (name) DO NOTHING;
INSERT INTO segment (name)
VALUES ('Wagon') ON CONFLICT (name) DO NOTHING;
INSERT INTO segment (name)
VALUES ('Van') ON CONFLICT (name) DO NOTHING;
INSERT INTO segment (name)
VALUES ('Truck') ON CONFLICT (name) DO NOTHING;
INSERT INTO segment (name)
VALUES ('Limousine') ON CONFLICT (name) DO NOTHING;


-- Pre-populate car_model table
INSERT INTO car_model (id, brand_model_id, model_year, segment, number_of_doors, seating_capacity, luggage_capacity,
                       category, engine_type, transmission_type, drivetrain, motor_displacement, rental_price_per_day)
VALUES (1, 1, 2020, 'Compact', 4, 5, 400, 'ECONOMY', 'HYBRID', 'AUTOMATIC', 'FWD', 1.8, 50.0),  -- fiat500
       (2, 3, 2021, 'Compact', 4, 5, 400, 'ECONOMY', 'PETROL', 'MANUAL', 'FWD', 2.0, 60.0),     -- toyotaYaris
       (3, 2, 2022, 'SUV', 5, 5, 600, 'LUXURY', 'DIESEL', 'AUTOMATIC', 'AWD', 3.0, 120.0),      -- fiatPunto
       (4, 7, 2023, 'Sedan', 4, 5, 500, 'LUXURY', 'PETROL', 'AUTOMATIC', 'RWD', 2.0, 150.0),    -- bmw3Series
       (5, 5, 2023, 'Coupe', 2, 4, 300, 'PREMIUM', 'PETROL', 'AUTOMATIC', 'RWD', 5.0, 200.0),   -- fordFocus
       (6, 23, 2022, 'Sedan', 4, 5, 500, 'PREMIUM', 'HYBRID', 'AUTOMATIC', 'FWD', 2.5, 80.0),   -- kiaCeed
       (7, 8, 2023, 'SUV', 5, 7, 800, 'LUXURY', 'ELECTRIC', 'AUTOMATIC', 'AWD', 6.2, 250.0),    -- bmw5Series
       (8, 11, 2022, 'SUV', 5, 5, 700, 'PREMIUM', 'HYBRID', 'AUTOMATIC', 'AWD', 2.0, 90.0),     -- hondaCivic
       (9, 13, 2021, 'Sedan', 4, 5, 400, 'ECONOMY', 'PETROL', 'AUTOMATIC', 'FWD', 2.5, 70.0),   -- mercedesCClass
       (10, 19, 2022, 'Compact', 4, 5, 400, 'ECONOMY', 'DIESEL', 'MANUAL', 'FWD', 2.0, 65.0),   -- chevroletCruze
       (11, 21, 2023, 'SUV', 5, 7, 800, 'LUXURY', 'PETROL', 'AUTOMATIC', 'AWD', 3.5, 220.0),    -- hyundaiI30
       (12, 9, 2022, 'SUV', 5, 5, 700, 'PREMIUM', 'ELECTRIC', 'AUTOMATIC', 'AWD', 2.5, 95.0),   -- audiA3
       (13, 3, 2023, 'SUV', 5, 5, 700, 'PREMIUM', 'PETROL', 'AUTOMATIC', 'AWD', 2.5, 100.0),    -- toyotaCorolla
       (14, 25, 2022, 'Hatchback', 5, 5, 400, 'ECONOMY', 'DIESEL', 'MANUAL', 'FWD', 1.4, 60.0), -- peugeot208
       (15, 26, 2023, 'SUV', 5, 7, 800, 'LUXURY', 'PETROL', 'AUTOMATIC', 'AWD', 3.0, 230.0) ON CONFLICT (id) DO NOTHING; -- peugeot308

SELECT setval('car_model_seq', (SELECT MAX(id) FROM car_model));


-- Pre-populate car_model_feature table
-- Pre-populate car_model_feature table, avoiding duplicates
INSERT INTO car_model_feature (car_model_id, feature_id)
VALUES
    (1, 1), (1, 2), (1, 3), (1, 5), (1, 7), -- fiat500
    (2, 1), (2, 4), (2, 6), (2, 10), (2, 13), -- toyotaYaris
    (3, 3), (3, 7), (3, 8), (3, 11), (3, 12), -- fiatPunto
    (4, 1), (4, 5), (4, 6), (4, 9), (4, 10), -- bmw3Series
    (5, 2), (5, 3), (5, 7), (5, 13), (5, 15), -- fordFocus
    (6, 2), (6, 8), (6, 10), (6, 12), (6, 15), -- kiaCeed
    (7, 4), (7, 9), (7, 10), (7, 16), (7, 17), -- bmw5Series
    (8, 1), (8, 5), (8, 6), (8, 7), (8, 9), -- hondaCivic
    (9, 2), (9, 4), (9, 8), (9, 12), (9, 13), -- mercedesCClass
    (10, 1), (10, 3), (10, 11), (10, 13), (10, 16), -- chevroletCruze
    (11, 3), (11, 5), (11, 8), (11, 9), (11, 16), -- hyundaiI30
    (12, 1), (12, 6), (12, 10), (12, 12), (12, 15), -- audiA3
    (13, 2), (13, 5), (13, 11), (13, 12), (13, 13), -- toyotaCorolla
    (14, 3), (14, 4), (14, 5), (14, 7), (14, 13), -- peugeot208
    (15, 2), (15, 6), (15, 9), (15, 12), (15, 17) -- peugeot308
    ON CONFLICT (car_model_id, feature_id) DO NOTHING;


-- Pre-populate vehicle table with random data
INSERT INTO vehicle (id, kilometers_travelled, license_plate, vehicle_status, vin, car_model_id)
VALUES
    -- Fiat 500
    (1, 15000, 'FIAT500-001', 'AVAILABLE', '1HGBH41JXMN109186', 1),
    (2, 12000, 'FIAT500-002', 'RENTED', '1HGBH41JXMN109187', 1),
    (3, 10000, 'FIAT500-003', 'UNDER_MAINTENANCE', '1HGBH41JXMN109188', 1),

    -- Toyota Yaris
    (4, 20000, 'TOYOTA-YARIS-001', 'AVAILABLE', '2HGBH41JXMN109189', 2),
    (5, 25000, 'TOYOTA-YARIS-002', 'RENTED', '2HGBH41JXMN109190', 2),
    (6, 30000, 'TOYOTA-YARIS-003', 'UNDER_MAINTENANCE', '2HGBH41JXMN109191', 2),

    -- Fiat Punto
    (7, 50000, 'FIAT-PUNTO-001', 'AVAILABLE', '3HGBH41JXMN109192', 3),
    (8, 40000, 'FIAT-PUNTO-002', 'RENTED', '3HGBH41JXMN109193', 3),
    (9, 45000, 'FIAT-PUNTO-003', 'AVAILABLE', '3HGBH41JXMN109194', 3),

    -- BMW 3 Series
    (10, 12000, 'BMW3SERIES-001', 'AVAILABLE', '4HGBH41JXMN109195', 4),
    (11, 14000, 'BMW3SERIES-002', 'RENTED', '4HGBH41JXMN109196', 4),

    -- Ford Focus
    (12, 5000, 'FORD-FOCUS-001', 'AVAILABLE', '5HGBH41JXMN109197', 5),
    (13, 7000, 'FORD-FOCUS-002', 'AVAILABLE', '5HGBH41JXMN109198', 5),

    -- Kia Ceed
    (14, 18000, 'KIA-CEED-001', 'AVAILABLE', '6HGBH41JXMN109199', 6),
    (15, 21000, 'KIA-CEED-002', 'UNDER_MAINTENANCE', '6HGBH41JXMN109200', 6),
    (16, 25000, 'KIA-CEED-003', 'AVAILABLE', '6HGBH41JXMN109201', 6),

    -- BMW 5 Series
    (17, 32000, 'BMW5SERIES-001', 'RENTED', '7HGBH41JXMN109202', 7),

    -- Honda Civic
    (18, 10000, 'HONDA-CIVIC-001', 'AVAILABLE', '8HGBH41JXMN109203', 8),
    (19, 15000, 'HONDA-CIVIC-002', 'RENTED', '8HGBH41JXMN109204', 8),

    -- Mercedes C-Class
    (20, 25000, 'MERCEDES-C-CLASS-001', 'AVAILABLE', '9HGBH41JXMN109205', 9),

    -- Chevrolet Cruze
    (21, 18000, 'CHEVROLET-CRUZE-001', 'AVAILABLE', '10HGBH41JXMN109206', 10),
    (22, 22000, 'CHEVROLET-CRUZE-002', 'RENTED', '10HGBH41JXMN109207', 10),

    -- Hyundai I30
    (23, 12000, 'HYUNDAI-I30-001', 'AVAILABLE', '11HGBH41JXMN109208', 11),

    -- Audi A3
    (24, 10000, 'AUDI-A3-001', 'AVAILABLE', '12HGBH41JXMN109209', 12) ON CONFLICT (id) DO NOTHING;

SELECT setval('vehicle_seq', (SELECT MAX(id) FROM vehicle));


-- Pre-populate maintenance_history table with random data for each vehicle
-- The maintenance history for each vehicle can have 0 to 3 records

-- Fiat 500
INSERT INTO maintenance_history (id, defect, maintenance_date, maintenance_description, maintenance_status, service, vehicle_id)
VALUES
    (1, 'Engine Fault', '2025-03-20 10:00:00', 'Engine replacement required', 'COMPLETED', 'Engine Repair', 1),
    (2, 'Brake Pads Wear', '2025-02-15 14:00:00', 'Brake pads replaced', 'COMPLETED', 'Brake Service', 1),
    (3, 'Suspension Issue', '2025-04-10 09:00:00', 'Suspension repair ongoing', 'REPAIRING', 'Suspension Repair', 1),

-- Toyota Yaris
    (4, 'Tire Pressure', '2025-03-18 08:00:00', 'Tire pressure adjustment', 'COMPLETED', 'Tire Service', 2),
    (5, 'Oil Leak', '2026-04-12 11:00:00', 'Oil leak detected', 'UPCOMING', 'Engine Repair', 2),

-- Fiat Punto
    (6, 'Transmission Issue', '2025-01-30 12:00:00', 'Transmission repair completed', 'COMPLETED', 'Transmission Repair', 3),
    (7, 'Battery Replacement', '2025-04-05 15:00:00', 'Battery replacement in progress', 'WAITING_FOR_PARTS', 'Battery Service', 3),

-- BMW 3 Series
    (8, 'Air Conditioning Failure', '2025-04-01 10:00:00', 'AC repaired', 'COMPLETED', 'AC Repair', 4),
    (9, 'Brake Fluid', '2025-02-25 16:00:00', 'Brake fluid change', 'COMPLETED', 'Brake Service', 4),

-- Ford Focus
    (10, 'Clutch Issue', '2025-03-01 13:00:00', 'Clutch replaced', 'COMPLETED', 'Clutch Repair', 5),
    (11, 'Engine Noise', '2026-04-13 09:00:00', 'Engine diagnostics scheduled', 'UPCOMING', 'Engine Diagnostics', 5),

-- Kia Ceed
    (12, 'Fuel Pump', '2025-01-20 11:00:00', 'Fuel pump replaced', 'COMPLETED', 'Fuel System Service', 6),
    (13, 'Suspension Damage', '2026-04-12 14:00:00', 'Suspension repair scheduled', 'UPCOMING', 'Suspension Repair', 6),
    (14, 'Exhaust Issue', '2025-04-05 12:00:00', 'Exhaust system replacement', 'REPAIRING', 'Exhaust Repair', 6),

-- BMW 5 Series
    (15, 'Battery', '2025-03-28 16:00:00', 'Battery replaced', 'COMPLETED', 'Battery Service', 7),

-- Honda Civic
    (16, 'Brake Pads', '2025-03-22 10:00:00', 'Brake pads worn out, replaced', 'COMPLETED', 'Brake Service', 8),
    (17, 'Exhaust Leak', '2026-04-12 09:00:00', 'Exhaust leak detected', 'UPCOMING', 'Exhaust Repair', 8),

-- Mercedes C-Class
    (18, 'Oil Change', '2025-04-10 14:00:00', 'Oil changed', 'COMPLETED', 'Oil Change', 9),

-- Chevrolet Cruze
    (19, 'Air Filter', '2025-02-18 11:00:00', 'Air filter replacement', 'COMPLETED', 'Air Filter Service', 10),
    (20, 'Brake Pads', '2026-04-13 09:00:00', 'Brake pads worn out, change scheduled', 'UPCOMING', 'Brake Service', 10),

-- Hyundai I30
    (21, 'Water Pump', '2025-01-10 14:00:00', 'Water pump replaced', 'COMPLETED', 'Water Pump Service', 11),

-- Audi A3
    (22, 'Clutch Issue', '2026-04-10 12:00:00', 'Clutch replacement', 'UPCOMING', 'Clutch Repair', 12),

-- Toyota Corolla
    (23, 'Suspension', '2025-02-20 09:00:00', 'Suspension repaired', 'COMPLETED', 'Suspension Repair', 13),

-- Peugeot 208
    (24, 'Engine Overheating', '2026-04-12 13:00:00', 'Engine overheating issue', 'UPCOMING', 'Engine Diagnostics', 14),
    (25, 'Timing Belt', '2025-03-10 12:00:00', 'Timing belt replaced', 'COMPLETED', 'Timing Belt Service', 14),

-- Peugeot 308
    (26, 'Exhaust Repair', '2025-02-28 14:00:00', 'Exhaust repair completed', 'COMPLETED', 'Exhaust Repair', 15) ON CONFLICT (id) DO NOTHING;

SELECT setval('maintenance_history_seq', (SELECT MAX(id) FROM maintenance_history));

-- Pre-populate the note table with rental-related notes for each vehicle (0 to 3 notes per vehicle)

-- Fiat 500
INSERT INTO note (id, author, date, note, vehicle_id, type)
VALUES
    (1, 'Rental Manager', '2025-03-10 10:00:00', 'Vehicle returned with slight scratch on rear bumper. Needs cleaning.', 1, 'MAINTENANCE'),
    (2, 'Customer Service', '2025-01-15 11:30:00', 'Customer requested a second set of keys for this vehicle.', 1, 'GENERAL'),

-- Toyota Yaris
    (3, 'Rental Manager', '2025-02-05 08:45:00', 'Vehicle returned on time with no damages.', 2, 'GENERAL'),

-- Fiat Punto
    (4, 'Fleet Coordinator', '2025-03-18 14:00:00', 'Vehicle available for rental after routine cleaning.', 3, 'MAINTENANCE'),
    (5, 'Customer Service', '2025-01-20 13:30:00', 'Customer left a positive review about the car’s fuel efficiency.', 3, 'GENERAL'),

-- BMW 3 Series
    (6, 'Rental Manager', '2025-04-02 16:00:00', 'Customer requested a longer rental period due to travel delay. Extended by 3 days.', 4, 'RESERVATION'),
    (7, 'Maintenance Team', '2025-02-25 12:00:00', 'Check for minor scratches noted, vehicle cleared for rental after inspection.', 4, 'MAINTENANCE'),

-- Ford Focus
    (8, 'Customer Service', '2025-03-25 09:00:00', 'Vehicle returned on time and in good condition. Client requested a different model next time.', 5, 'GENERAL'),

-- Kia Ceed
    (9, 'Rental Manager', '2025-02-15 10:30:00', 'Car parked in garage B; available for pickup after 5:00 PM.', 6, 'RESERVATION_PICKUP'),
    (10, 'Fleet Coordinator', '2025-01-25 14:00:00', 'Customer requested delivery to another location. Delivery completed successfully.', 6, 'GENERAL'),

-- BMW 5 Series
    (11, 'Customer Service', '2025-03-28 16:30:00', 'Vehicle returned with no issues. Customer praised the vehicle’s comfort.', 7, 'GENERAL'),

-- Honda Civic
    (12, 'Rental Manager', '2025-03-22 11:15:00', 'Car is available for pickup, cleaned and ready for the next rental.', 8, 'RESERVATION_PICKUP'),

-- Mercedes C-Class
    (13, 'Customer Service', '2025-02-05 09:30:00', 'Customer complained about slight engine noise during rental. Issue resolved.', 9, 'GENERAL'),

-- Chevrolet Cruze
    (14, 'Rental Manager', '2025-01-10 17:00:00', 'Vehicle delayed in returning, customer compensated with a discount.', 10, 'RESERVATION_CANCEL'),
    (15, 'Fleet Coordinator', '2025-03-12 10:45:00', 'Vehicle parked in garage C for cleaning, ready for next booking.', 10, 'MAINTENANCE'),

-- Hyundai I30
    (16, 'Customer Service', '2025-01-15 09:15:00', 'Customer noted the air conditioning needed servicing during rental. Will check upon return.', 11, 'MAINTENANCE'),

-- Audi A3
    (17, 'Fleet Coordinator', '2025-03-30 14:00:00', 'Vehicle available for long-term rental starting tomorrow. Ready for pickup at 9 AM.', 12, 'RESERVATION'),

-- Toyota Corolla
    (18, 'Customer Service', '2025-02-10 10:00:00', 'Client praised the fuel efficiency of the car. Looking to rent again.', 13, 'GENERAL'),

-- Peugeot 208
    (19, 'Rental Manager', '2025-04-01 08:30:00', 'Vehicle ready for next rental after cleaning and full inspection.', 14, 'MAINTENANCE'),
    (20, 'Fleet Coordinator', '2025-03-15 15:30:00', 'Customer had a minor accident. Car in repair for a week before return to the fleet.', 14, 'MAINTENANCE'),

-- Peugeot 308
    (21, 'Customer Service', '2025-02-25 13:00:00', 'Vehicle returned early. Customer satisfied with the service.', 15, 'GENERAL')
    ON CONFLICT (id) DO NOTHING;

SELECT setval('note_seq', (SELECT MAX(id) FROM note));


-- RESERVATIONS FOR CUSTOMER 1 (ID 1)
-- CONFIRMED
INSERT INTO reservation (id, user_id, vehicle_id, start_date, end_date, reservation_status, created_at, updated_at, total_price, cancellation_date)
VALUES
    (1, 1, 6, '2025-10-04', '2025-10-07', 'CONFIRMED', '2025-04-19 10:00:00', '2025-04-19 10:00:00', 466.04, NULL),
    (2, 1, 18, '2025-11-30', '2025-11-03', 'CONFIRMED', '2025-04-06 10:00:00', '2025-04-06 10:00:00', 188.82, NULL);

-- ONGOING
INSERT INTO reservation (id, user_id, vehicle_id, start_date, end_date, reservation_status, created_at, updated_at, total_price, cancellation_date)
VALUES (3, 1, 3, '2025-04-22', '2025-05-28', 'ONGOING', '2025-04-15 10:00:00', '2025-04-20 10:00:00', 1215.50, NULL);

-- Pickup for Reservation 3
INSERT INTO pickup (reservation_id, timestamp, location, handled_by_staff_id)
VALUES (3, '2025-04-22 10:00:00', 'Airport Terminal A', 3);

-- Dropoff for Reservation 3
INSERT INTO dropoff (reservation_id, timestamp, location, handled_by_staff_id)
VALUES (3, '2025-04-28 10:00:00', 'City Center Garage', 4);

-- COMPLETED
INSERT INTO reservation (id, user_id, vehicle_id, start_date, end_date, reservation_status, created_at, updated_at, total_price, cancellation_date)
VALUES (4, 1, 12, '2025-04-01', '2025-04-07', 'COMPLETED', '2025-03-25 09:30:00', '2025-04-07 11:00:00', 321.00, NULL);

-- Pickup for Reservation 4
INSERT INTO pickup (reservation_id, timestamp, location, handled_by_staff_id)
VALUES (4, '2025-04-01 09:00:00', 'Main Station', 3);

-- Dropoff for Reservation 4
INSERT INTO dropoff (reservation_id, timestamp, location, handled_by_staff_id)
VALUES (4, '2025-04-07 09:00:00', 'North Lot', 4);

-- CANCELED
INSERT INTO reservation (id, user_id, vehicle_id, start_date, end_date, reservation_status, created_at, updated_at, total_price, cancellation_date)
VALUES (5, 1, 21, '2025-03-10', '2025-03-15', 'CANCELLED', '2025-02-28 12:00:00', '2025-03-05 15:00:00', 299.00, '2025-03-05 15:00:00');

-- RESERVATIONS FOR CUSTOMER 2 (ID 2)
-- CONFIRMED
INSERT INTO reservation (id, user_id, vehicle_id, start_date, end_date, reservation_status, created_at, updated_at, total_price, cancellation_date)
VALUES
    (6, 2, 9, '2025-09-01', '2025-09-04', 'CONFIRMED', '2025-04-13 10:00:00', '2025-04-13 10:00:00', 397.32, NULL),
    (7, 2, 2, '2025-09-10', '2025-09-12', 'CONFIRMED', '2025-04-16 08:00:00', '2025-04-16 08:00:00', 280.00, NULL);

-- ONGOING
INSERT INTO reservation (id, user_id, vehicle_id, start_date, end_date, reservation_status, created_at, updated_at, total_price, cancellation_date)
VALUES (8, 2, 16, '2025-04-24', '2025-05-27', 'ONGOING', '2025-04-15 11:00:00', '2025-04-24 11:30:00', 1360.00, NULL);

-- Pickup for Reservation 8
INSERT INTO pickup (reservation_id, timestamp, location, handled_by_staff_id)
VALUES (8, '2025-04-24 09:30:00', 'Downtown Lot', 4);

-- Dropoff for Reservation 8
INSERT INTO dropoff (reservation_id, timestamp, location, handled_by_staff_id)
VALUES (8, '2025-04-27 09:30:00', 'Uptown Lot', 3);

-- COMPLETED
INSERT INTO reservation (id, user_id, vehicle_id, start_date, end_date, reservation_status, created_at, updated_at, total_price, cancellation_date)
VALUES (9, 2, 5, '2025-03-01', '2025-03-05', 'COMPLETED', '2025-02-20 10:00:00', '2025-03-05 12:00:00', 210.00, NULL);

-- Pickup for Reservation 9
INSERT INTO pickup (reservation_id, timestamp, location, handled_by_staff_id)
VALUES (9, '2025-03-01 10:00:00', 'Central Garage', 3);

-- Dropoff for Reservation 9
INSERT INTO dropoff (reservation_id, timestamp, location, handled_by_staff_id)
VALUES (9, '2025-03-05 10:00:00', 'East Garage', 4);

-- CANCELED
INSERT INTO reservation (id, user_id, vehicle_id, start_date, end_date, reservation_status, created_at, updated_at, total_price, cancellation_date)
VALUES (10, 2, 20, '2025-04-10', '2025-04-15', 'CANCELLED', '2025-03-30 12:00:00', '2025-04-08 13:30:00', 312.00, '2025-04-08 13:30:00');

-- Reset sequence (if using auto-increment IDs)
SELECT setval('reservation_seq', (SELECT MAX(id) FROM reservation));


