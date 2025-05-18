-- CUSTOMER 1
INSERT INTO users (id, username, first_name, last_name, email, phone_number, role, birth_date)
VALUES (1, 'fra_fiore', 'Francesca', 'Fiore', 'francesca@example.com', '+383334567890', 'CUSTOMER', '1990-01-01') ON CONFLICT (id) DO NOTHING;

-- CUSTOMER 2
INSERT INTO users (id, username, first_name, last_name, email, phone_number, role, birth_date)
VALUES (2, 'asmith', 'Alice', 'Smith', 'asmith@example.com', '+323487654321', 'CUSTOMER', '1985-05-12') ON CONFLICT (id) DO NOTHING;

-- STAFF (non-customer)
INSERT INTO users (id, username, first_name, last_name, email, phone_number, role, birth_date)
VALUES (3, 'mwhite', 'Mark', 'White', 'mwhite@staff.com', '+393512223333', 'STAFF', '1998-03-10') ON CONFLICT (id) DO NOTHING;

-- STAFF (non-customer)
INSERT INTO users (id, username, first_name, last_name, email, phone_number, role, birth_date)
VALUES (4, 'sofiebrand', 'Sofia', 'Brand', 'sofia@staff.com', '+393544328890', 'STAFF', '1995-03-10') ON CONFLICT (id) DO NOTHING;

-- MANAGER (non-customer)
INSERT INTO users (id, username, first_name, last_name, email, phone_number, role, birth_date)
VALUES (5, 'marioss', 'Mario', 'Rossi', 'marioss@manager.com', '+393514143523', 'MANAGER', '1983-03-16') ON CONFLICT (id) DO NOTHING;

-- FLEET MANAGER (non-customer)
INSERT INTO users (id, username, first_name, last_name, email, phone_number, role, birth_date)
VALUES (6, 'saradose', 'Sara', 'Dose', 'sara@fleetmanager.com', '+393514666523', 'FLEET_MANAGER', '1983-03-16') ON CONFLICT (id) DO NOTHING;

SELECT setval('users_seq', (SELECT MAX(id) FROM users));

-- CUSTOMER PROFILE for user 1
INSERT INTO customer_profiles (user_id, reliability_score, driving_license_number, driving_license_expiry)
VALUES (1, 8.5, 'DL12345678', '2028-06-30') ON CONFLICT (user_id) DO NOTHING;

-- CUSTOMER PROFILE for user 2
INSERT INTO customer_profiles (user_id, reliability_score, driving_license_number, driving_license_expiry)
VALUES (2, 8.8, 'DL87654321', '2030-01-15') ON CONFLICT (user_id) DO NOTHING;

-- Preferences for user 1 (jdoe)
INSERT INTO customer_preferences (customer_profile_id, preference)
VALUES
    (1, 'EMAIL_NOTIFICATIONS'),
    (1, 'MARKETING_OFFERS')
    ON CONFLICT DO NOTHING;

-- Preferences for user 2 (asmith)
INSERT INTO customer_preferences (customer_profile_id, preference)
VALUES
    (2, 'SMS_NOTIFICATIONS')
    ON CONFLICT DO NOTHING;
