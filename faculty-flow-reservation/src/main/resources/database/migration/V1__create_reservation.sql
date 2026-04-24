CREATE TYPE reservation_status AS ENUM ('PENDING', 'ACCEPTED', 'REJECTED');

CREATE TABLE reservation (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID UNIQUE DEFAULT gen_random_uuid(),

    room VARCHAR(255) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    reserved_by VARCHAR(255) NOT NULL,
    status reservation_status NOT NULL,
    note VARCHAR(255)
);

ALTER TABLE reservation
    ADD CONSTRAINT valid_time CHECK (end_time > start_time);