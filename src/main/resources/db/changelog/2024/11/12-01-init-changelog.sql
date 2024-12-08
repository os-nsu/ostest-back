INSERT INTO "group" (name, is_archived)
VALUES ('22201', false),
       ('22202', false),
       ('22203', false),
       ('22204', false),
       ('22205', false),
       ('22206', false),
       ('22207', false),
       ('22208', false),
       ('22209', false),
       ('22210', false),
       ('22211', false),
       ('22212', false),
       ('22213', false),
       ('22214', false),
       ('22215', false),
       ('22216', false),
       ('21201', false),
       ('21202', false),
       ('21203', false),
       ('21204', false),
       ('21205', false),
       ('21206', false),
       ('21207', false),
       ('21208', false),
       ('21209', false),
       ('21210', false),
       ('21211', false),
       ('21212', false),
       ('21213', false),
       ('21214', false),
       ('21215', false),
       ('21216', false),
       ('20201', true),
       ('20202', true),
       ('20203', true),
       ('20204', true),
       ('20205', true),
       ('20206', true),
       ('20207', true),
       ('20208', true),
       ('20209', true),
       ('20210', true),
       ('20211', true),
       ('20212', true),
       ('20213', true),
       ('20214', true),
       ('20215', true),
       ('20216', true),
       ('19201', true),
       ('19202', true),
       ('19203', true),
       ('19204', true),
       ('19205', true),
       ('19206', true),
       ('19207', true),
       ('19208', true),
       ('19209', true),
       ('19210', true),
       ('19211', true),
       ('19212', true),
       ('19213', true),
       ('19214', true),
       ('19215', true),
       ('19216', true);

UPDATE "group"
SET is_archived = false
WHERE name = 'test_group';


INSERT INTO "test" (category, description, name, script_body, code)
VALUES ('DEFAULT', 'Tests cleaning the project using ''make clean''.', 'test_successful_make_clean', null, '1'),
       ('DEFAULT', 'Tests that the proxy compile successfully.', 'test_successful_compilation', null, '2'),
       ('DEFAULT', 'Tests that the proxy binary contains expected library symbols.', 'test_library_symbols', null, '3'),
       ('DEFAULT', 'test_makefile_exists', 'test_makefile_exists', null, '4'),
       ('DEFAULT', 'test_project_structure', 'test_project_structure', null, '5'),
       ('DEFAULT', 'test_compilation_without_warnings', 'test_compilation_without_warnings', null, '6'),
       ('DEFAULT', 'Tests that the proxy starts successfully without arguments and can be terminated cleanly.', 'test_run_without_arguments', null, '7'),
       ('DEFAULT', 'Tests running the proxy successfully with ''--help'' argument.', 'test_run_with_help_argument', null, '8'),
       ('DEFAULT', 'Tests running the proxy with invalid arguments.', 'test_run_with_invalid_arguments', null, '9'),
       ('DEFAULT', 'Test that log file is created after starting the proxy.', 'test_log_file_creation', null, '10'),
       ('DEFAULT', 'Test that specific messages are presented in the log.', 'test_log_contains_message', null, '11'),
       ('DEFAULT', 'Test that messages appear in the log in the correct order.', 'test_log_messages_in_order', null, '12'),
       ('DEFAULT', 'Tests the launch of a proxy built with AddressSanitizer and UndefinedBehaviorSanitizer.', 'test_execution_with_sanitizers', null, '13'),
       ('DEFAULT', 'Tests that the proxy correctly terminates upon receiving specific signals.', 'test_proxy_termination_on_signal', null, '14');


ALTER TABLE test_laboratory_link
DROP CONSTRAINT "FKq9asnxuea0nm9lrk78gxqs8jk",
ADD CONSTRAINT "FKq9asnxuea0nm9lrk78gxqs8jk"
FOREIGN KEY (test_id) REFERENCES test(id) ON DELETE CASCADE;


INSERT INTO "test_laboratory_link" (is_switched_on, laboratory_id, test_id)
VALUES (true, 1, 1),
       (true, 1, 2),
       (true, 1, 3),
       (true, 1, 4),
       (true, 1, 5),
       (true, 1, 6),
       (true, 1, 7),
       (true, 1, 8),
       (true, 1, 9),
       (true, 2, 10),
       (true, 2, 11),
       (true, 2, 12),
       (true, 3, 13),
       (true, 4, 14);