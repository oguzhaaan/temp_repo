CREATE SEQUENCE IF NOT EXISTS dropoff_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS pickup_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS reservation_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE dropoff
(
    reservation_id      BIGINT NOT NULL,
    timestamp           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    location            VARCHAR(255),
    handled_by_staff_id BIGINT,
    CONSTRAINT pk_dropoff PRIMARY KEY (reservation_id)
);

CREATE TABLE pickup
(
    reservation_id      BIGINT NOT NULL,
    timestamp           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    location            VARCHAR(255),
    handled_by_staff_id BIGINT,
    CONSTRAINT pk_pickup PRIMARY KEY (reservation_id)
);

CREATE TABLE reservation
(
    id                 BIGINT       NOT NULL,
    user_id            BIGINT       NOT NULL,
    vehicle_id         BIGINT       NOT NULL,
    start_date         date         NOT NULL,
    end_date           date         NOT NULL,
    reservation_status VARCHAR(255) NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at         TIMESTAMP WITHOUT TIME ZONE,
    total_price        FLOAT        NOT NULL,
    cancellation_date  TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_reservation PRIMARY KEY (id)
);


ALTER TABLE dropoff
    ADD CONSTRAINT FK_DROPOFF_ON_RESERVATION FOREIGN KEY (reservation_id) REFERENCES reservation (id);

ALTER TABLE pickup
    ADD CONSTRAINT FK_PICKUP_ON_RESERVATION FOREIGN KEY (reservation_id) REFERENCES reservation (id);

ALTER TABLE reservation
    ADD CONSTRAINT FK_RESERVATION_ON_VEHICLE FOREIGN KEY (vehicle_id) REFERENCES vehicle (id);