create database os_test;

INSERT INTO role(name)
VALUES ('ADMIN'),
       ('STUDENT'),
       ('TEACHER');

INSERT INTO "group" (name)
VALUES ('test_group');

INSERT INTO "user" (first_name, second_name, username, group_id)
VALUES ('Robert', 'Pattinson', 'batman_forever', 1),
       ('Dora', 'Explorer', 'dora_explorer', 1),
       ('Tom', 'Jerry', 'catch_me_if_u_can', 1);


INSERT INTO user_role (user_id, role_id)
VALUES (1, 1),
       (2, 2),
       (3, 3);

CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO user_password (user_id, password)
VALUES (1, crypt('batman_forever', gen_salt('bf', 8))),
       (2, crypt('dora_explorer', gen_salt('bf', 8))),
       (3, crypt('catch_me_if_u_can', gen_salt('bf', 8)));






