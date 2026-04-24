CREATE USER ff_user WITH PASSWORD 'ff_user';
CREATE DATABASE ff_user OWNER ff_user;

CREATE USER ff_room WITH PASSWORD 'ff_room';
CREATE DATABASE ff_room OWNER ff_room;

CREATE USER ff_reservation WITH PASSWORD 'ff_reservation';
CREATE DATABASE ff_reservation OWNER ff_reservation;

GRANT ALL PRIVILEGES ON DATABASE ff_user TO admin;
GRANT ALL PRIVILEGES ON DATABASE ff_room TO admin;
GRANT ALL PRIVILEGES ON DATABASE ff_reservation TO admin;