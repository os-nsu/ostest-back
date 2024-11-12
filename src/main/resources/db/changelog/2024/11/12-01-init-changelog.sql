INSERT INTO "group" (name, is_archived)
VALUES ('group_1_old_stream', false),
       ('group_2_old_stream', false),
       ('group_3_old_stream', false),
       ('group_4_old_stream', false),
       ('group_5_old_stream', false),
       ('group_6_old_stream', false),
       ('group_7_old_stream', false),
       ('group_8_old_stream', false),
       ('group_9_old_stream', false),
       ('group_10_old_stream', false),
       ('group_11_old_stream', false),
       ('group_12_old_stream', false),
       ('group_13_new_stream', false),
       ('group_14_new_stream', false),
       ('group_15_new_stream', false),
       ('group_16_new_stream', false),
       ('group_17_old_stream', true),
       ('group_18_new_stream', true),
       ('group_19_old_stream', true),
       ('group_20_new_stream', true),
       ('group_21_old_stream', true);

UPDATE "group"
SET is_archived = false
WHERE name = 'test_group';


INSERT INTO "test" (category, description, name, script_body, code)
VALUES ('DEFAULT', 'some description for test', 'test1_for_lab1', null, 'some_code_1'),
       ('DEFAULT', 'some description for test', 'test2_for_lab1', null, 'some_code_2'),
       ('DEFAULT', 'some description for test', 'test3_for_lab1', null, 'some_code_3'),
       ('DEFAULT', 'some description for test', 'test1_for_lab2', null, 'some_code_4'),
       ('DEFAULT', 'some description for test', 'test2_for_lab2', null, 'some_code_5'),
       ('DEFAULT', 'some description for test', 'test1_for_lab3', null, 'some_code_6'),
       ('DEFAULT', 'some description for test', 'test1_for_lab4', null, 'some_code_7'),
       ('DEFAULT', 'some description for test', 'test1_for_lab5', null, 'some_code_8');