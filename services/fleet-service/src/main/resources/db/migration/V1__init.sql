CREATE TABLE drivers (
                         id              UUID PRIMARY KEY,
                         first_name      VARCHAR(60)  NOT NULL,
                         last_name       VARCHAR(60)  NOT NULL,
                         phone           VARCHAR(20)  NOT NULL,
                         license_number  VARCHAR(30)  NOT NULL UNIQUE,
                         status          VARCHAR(20)  NOT NULL
                             CHECK (status IN ('OFF_DUTY', 'AVAILABLE', 'ON_MISSION')),
                         version         BIGINT       NOT NULL DEFAULT 0,
                         created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE vehicles (
                          id                   UUID PRIMARY KEY,
                          plate                VARCHAR(15)  NOT NULL UNIQUE,
                          model                VARCHAR(60)  NOT NULL,
                          capacity_kg          INTEGER      NOT NULL CHECK (capacity_kg > 0),
                          status               VARCHAR(20)  NOT NULL
                              CHECK (status IN ('AVAILABLE', 'ON_MISSION', 'MAINTENANCE', 'OUT_OF_SERVICE')),
                          current_driver_id    UUID REFERENCES drivers (id),
                          current_delivery_id  UUID,
                          version              BIGINT       NOT NULL DEFAULT 0,
                          created_at           TIMESTAMPTZ  NOT NULL DEFAULT now(),
                          updated_at           TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_vehicles_status ON vehicles (status);
CREATE INDEX idx_drivers_status  ON drivers (status);

CREATE UNIQUE INDEX ux_vehicles_current_driver
    ON vehicles (current_driver_id)
    WHERE current_driver_id IS NOT NULL;
