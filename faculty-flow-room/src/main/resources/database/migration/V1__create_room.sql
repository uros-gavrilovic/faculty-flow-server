CREATE TYPE room_type AS ENUM ('CLASSROOM', 'AMPHITHEATER', 'COMPUTER_LAB', 'OFFICE', 'OTHER');
CREATE TYPE building_type AS ENUM ('NEW', 'OLD');

CREATE TABLE room (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID UNIQUE DEFAULT gen_random_uuid(),

    name VARCHAR(255) NOT NULL,
    code VARCHAR(255) NOT NULL,
    type room_type NOT NULL,
    floor INTEGER,
    building building_type NOT NULL,
    old_name VARCHAR(255),
    capacity INTEGER
);

ALTER TABLE room
    ADD CONSTRAINT floor_constraint CHECK (floor >= -1 AND floor <= 5);

ALTER TABLE room
    ADD CONSTRAINT capacity_constraint CHECK (capacity >= 0 AND capacity <= 100);
