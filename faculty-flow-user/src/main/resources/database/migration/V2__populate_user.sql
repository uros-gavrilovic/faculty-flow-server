INSERT INTO user_account (username, password, email, first_name, last_name, is_verified)
VALUES (
    'admin',
    crypt('admin123', gen_salt('bf')),
    'admin@faculty-flow.rs',
    'Admin',
    'Admin',
    TRUE
);

INSERT INTO user_roles (user_id, role)
VALUES (
    (SELECT id FROM user_account WHERE username = 'admin'),
    'ADMINISTRATOR'
);

