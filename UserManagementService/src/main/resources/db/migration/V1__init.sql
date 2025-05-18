CREATE SEQUENCE IF NOT EXISTS customer_profiles_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS users_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE customer_preferences
(
    customer_profile_id BIGINT       NOT NULL,
    preference          VARCHAR(255) NOT NULL
);

CREATE TABLE customer_profiles
(
    user_id                BIGINT       NOT NULL,
    reliability_score      FLOAT,
    driving_license_number VARCHAR(255) NOT NULL,
    driving_license_expiry date         NOT NULL,
    CONSTRAINT pk_customer_profiles PRIMARY KEY (user_id)
);

CREATE TABLE users
(
    id           BIGINT       NOT NULL,
    username     VARCHAR(255) NOT NULL,
    first_name   VARCHAR(255) NOT NULL,
    last_name    VARCHAR(255) NOT NULL,
    email        VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) NOT NULL,
    role         VARCHAR(255) NOT NULL,
    birth_date   date         NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

ALTER TABLE customer_profiles
    ADD CONSTRAINT FK_CUSTOMER_PROFILES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE customer_preferences
    ADD CONSTRAINT fk_customer_preferences_on_customer_profile FOREIGN KEY (customer_profile_id) REFERENCES customer_profiles (user_id);