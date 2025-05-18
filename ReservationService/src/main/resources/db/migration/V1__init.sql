CREATE SEQUENCE IF NOT EXISTS brand_model_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS car_model_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS feature_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS maintenance_history_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS note_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS vehicle_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE brand
(
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_brand PRIMARY KEY (name)
);

CREATE TABLE brand_model
(
    id    BIGINT       NOT NULL,
    brand VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL,
    CONSTRAINT pk_brand_model PRIMARY KEY (id)
);

CREATE TABLE car_model
(
    id                   BIGINT           NOT NULL,
    brand_model_id       BIGINT           NOT NULL,
    model_year           INTEGER          NOT NULL,
    segment              VARCHAR(255)     NOT NULL,
    number_of_doors      INTEGER          NOT NULL,
    seating_capacity     INTEGER          NOT NULL,
    luggage_capacity     INTEGER          NOT NULL,
    category             VARCHAR(255)     NOT NULL,
    engine_type          VARCHAR(255)     NOT NULL,
    transmission_type    VARCHAR(255)     NOT NULL,
    drivetrain           VARCHAR(255)     NOT NULL,
    motor_displacement   DOUBLE PRECISION NOT NULL,
    rental_price_per_day FLOAT            NOT NULL,
    CONSTRAINT pk_car_model PRIMARY KEY (id)
);

CREATE TABLE car_model_feature
(
    car_model_id BIGINT NOT NULL,
    feature_id   BIGINT NOT NULL,
    CONSTRAINT pk_car_model_feature PRIMARY KEY (car_model_id, feature_id)
);

CREATE TABLE feature
(
    id   BIGINT       NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_feature PRIMARY KEY (id)
);

CREATE TABLE maintenance_history
(
    id                      BIGINT       NOT NULL,
    vehicle_id              BIGINT       NOT NULL,
    maintenance_date        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    maintenance_status      VARCHAR(255) NOT NULL,
    defect                  VARCHAR(255),
    service                 VARCHAR(255) NOT NULL,
    maintenance_description VARCHAR(255),
    CONSTRAINT pk_maintenance_history PRIMARY KEY (id)
);

CREATE TABLE note
(
    id         BIGINT       NOT NULL,
    vehicle_id BIGINT       NOT NULL,
    note       VARCHAR(255) NOT NULL,
    date       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    author     VARCHAR(255) NOT NULL,
    type       VARCHAR(50)  NOT NULL,
    CONSTRAINT pk_note PRIMARY KEY (id)
);

CREATE TABLE segment
(
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_segment PRIMARY KEY (name)
);

CREATE TABLE vehicle
(
    id                   BIGINT       NOT NULL,
    car_model_id         BIGINT       NOT NULL,
    license_plate        VARCHAR(255) NOT NULL,
    vin                  VARCHAR(255) NOT NULL,
    kilometers_travelled INTEGER      NOT NULL,
    vehicle_status       VARCHAR(255) NOT NULL,
    CONSTRAINT pk_vehicle PRIMARY KEY (id)
);

ALTER TABLE feature
    ADD CONSTRAINT uc_feature_name UNIQUE (name);

ALTER TABLE vehicle
    ADD CONSTRAINT uc_vehicle_license_plate UNIQUE (license_plate);

ALTER TABLE vehicle
    ADD CONSTRAINT uc_vehicle_vin UNIQUE (vin);

ALTER TABLE brand_model
    ADD CONSTRAINT FK_BRAND_MODEL_ON_BRAND FOREIGN KEY (brand) REFERENCES brand (name);

ALTER TABLE car_model
    ADD CONSTRAINT FK_CAR_MODEL_ON_BRAND_MODEL FOREIGN KEY (brand_model_id) REFERENCES brand_model (id);

ALTER TABLE car_model
    ADD CONSTRAINT FK_CAR_MODEL_ON_SEGMENT FOREIGN KEY (segment) REFERENCES segment (name);

ALTER TABLE maintenance_history
    ADD CONSTRAINT FK_MAINTENANCE_HISTORY_ON_VEHICLE FOREIGN KEY (vehicle_id) REFERENCES vehicle (id);

ALTER TABLE note
    ADD CONSTRAINT FK_NOTE_ON_VEHICLE FOREIGN KEY (vehicle_id) REFERENCES vehicle (id);

ALTER TABLE vehicle
    ADD CONSTRAINT FK_VEHICLE_ON_CAR_MODEL FOREIGN KEY (car_model_id) REFERENCES car_model (id);

ALTER TABLE car_model_feature
    ADD CONSTRAINT fk_carmodfea_on_car_model FOREIGN KEY (car_model_id) REFERENCES car_model (id);

ALTER TABLE car_model_feature
    ADD CONSTRAINT fk_carmodfea_on_feature FOREIGN KEY (feature_id) REFERENCES feature (id);
