CREATE TABLE "user" (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID UNIQUE,

    username VARCHAR(64) NOT NULL UNIQUE,
    password CHAR(60) NOT NULL,
    email VARCHAR(255) UNIQUE,

    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL
);

ALTER TABLE "user"
    ADD CONSTRAINT username_pattern CHECK (username ~ '^[a-zA-Z0-9._-]*$');

ALTER TABLE "user"
    ADD CONSTRAINT password_pattern CHECK (password ~ '^\\$2[ayb]\\$.{56}$');

ALTER TABLE "user"
    ADD CONSTRAINT email_pattern CHECK (email IS NULL OR email ~ '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$');

CREATE TABLE "user_roles" (
    user_id BIGINT NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
    role VARCHAR(64) NOT NULL,

    PRIMARY KEY (user_id, role)
);
