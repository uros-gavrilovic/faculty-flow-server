CREATE TABLE user_account (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID UNIQUE DEFAULT gen_random_uuid(),

    username VARCHAR(64) NOT NULL UNIQUE,
    password CHAR(60) NOT NULL,
    email VARCHAR(255) UNIQUE,

    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL
);

ALTER TABLE user_account
    ADD CONSTRAINT username_pattern CHECK (username ~ '^[a-zA-Z0-9._-]*$');

ALTER TABLE user_account
    ADD CONSTRAINT password_pattern CHECK (password ~ '^\$2[aby]\$.{56}$');

ALTER TABLE user_account
    ADD CONSTRAINT email_pattern CHECK (email IS NULL OR email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

CREATE TYPE user_role AS ENUM ('USER', 'ADMINISTRATOR');

CREATE TABLE "user_roles" (
    user_id BIGINT NOT NULL REFERENCES user_account(id) ON DELETE CASCADE,
    role user_role NOT NULL,

    PRIMARY KEY (user_id, role)
);
