CREATE TYPE reservation_status AS ENUM ('PENDING', 'CANCELED', 'ACCEPTED', 'REJECTED');
CREATE TYPE event_type AS ENUM ('LECTURE', 'EXERCISE', 'LABORATORY', 'EXAM', 'CONSULTATION', 'OTHER');

CREATE TABLE reservation (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID UNIQUE DEFAULT gen_random_uuid(),

    name VARCHAR(255) NOT NULL,
    room VARCHAR(255) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    reserved_by VARCHAR(255) NOT NULL,
    reviewed_by VARCHAR(255),
    event_type event_type NOT NULL,
    status reservation_status NOT NULL,
    note VARCHAR(255),
    comment VARCHAR(255)
);

ALTER TABLE reservation
    ADD CONSTRAINT valid_time CHECK (end_time > start_time);