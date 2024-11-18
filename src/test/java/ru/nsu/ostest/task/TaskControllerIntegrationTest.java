package ru.nsu.ostest.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.nsu.ostest.TransactionalHelper;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.session.AttemptDto;
import ru.nsu.ostest.adapter.in.rest.model.session.AttemptResultDto;
import ru.nsu.ostest.adapter.in.rest.model.session.AvailableTaskResponse;
import ru.nsu.ostest.adapter.in.rest.model.session.MakeAttemptDto;
import ru.nsu.ostest.adapter.in.rest.model.test.AttemptResultSetRequest;
import ru.nsu.ostest.adapter.in.rest.model.test.TestCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestResultsDto;
import ru.nsu.ostest.adapter.in.rest.model.user.RoleEnum;
import ru.nsu.ostest.adapter.in.rest.model.user.UserCreationRequestDto;
import ru.nsu.ostest.adapter.mapper.AttemptMapper;
import ru.nsu.ostest.adapter.mapper.LaboratoryMapper;
import ru.nsu.ostest.adapter.mapper.TestMapper;
import ru.nsu.ostest.adapter.mapper.UserMapper;
import ru.nsu.ostest.adapter.out.persistence.entity.group.Group;
import ru.nsu.ostest.adapter.out.persistence.entity.laboratory.Laboratory;
import ru.nsu.ostest.adapter.out.persistence.entity.session.Attempt;
import ru.nsu.ostest.adapter.out.persistence.entity.session.Session;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;
import ru.nsu.ostest.domain.common.enums.TestCategory;
import ru.nsu.ostest.domain.repository.*;
import ru.nsu.ostest.session.SessionTestSetup;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@AutoConfigureMockMvc(addFilters = false)
@Import({TaskTestSetup.class, SessionTestSetup.class})
@SpringBootTest
public class TaskControllerIntegrationTest {

    private static final String TEST_NAME = "Test Test";
    private static final String TEST_DESCRIPTION = "Test Description";
    private static final String TEST_CODE = "Test Code";
    private static final String USER_NAME = "username";
    private static final String FIRST_NAME = "firstName";
    private static final String SECOND_NAME = "secondName";
    private static final String GROUP_NUMBER = "21207";
    private static final String LAB_NAME = "Laboratory";
    private static final String LAB_DESCRIPTION = "Description";
    private static final boolean IS_HIDDEN = false;
    private static final int SEMESTER_NUMBER = 1;
    private static final String REPOSITORY_URL = "Url";
    private static final String BRANCH_NAME = "Branch";
    private static final OffsetDateTime DEADLINE = OffsetDateTime.parse("2024-10-07T07:02:27Z");

    @Autowired
    private LaboratoryRepository laboratoryRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private AttemptRepository attemptRepository;

    @Autowired
    private LaboratoryMapper laboratoryMapper;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TestMapper testMapper;

    @Autowired
    private AttemptMapper attemptMapper;

    @Autowired
    private TaskTestSetup taskTestSetup;

    @Autowired
    private SessionTestSetup sessionTestSetup;

    private final List<Session> sessions = new ArrayList<>();

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:latest"
    );

    @Autowired
    private TransactionalHelper transactionalHelper;

    @BeforeEach
    void init() {
        transactionalHelper.runInTransaction(this::initInTransaction);
    }

    void initInTransaction() {
        testRepository.deleteAll();
        laboratoryRepository.deleteAll();
        userRepository.deleteAll();
        groupRepository.deleteAll();
        sessionRepository.deleteAll();
        groupRepository.flush();

        Group group = createGroup(GROUP_NUMBER);

        var test1 = createTest(1);
        var test2 = createTest(2);
        var test3 = createTest(3);
        var test4 = createTest(4);

        Laboratory laboratory1 = createLaboratory(createLaboratoryCreationRequestDto(1), List.of(test1, test2));
        Laboratory laboratory2 = createLaboratory(createLaboratoryCreationRequestDto(2), List.of(test3, test4));

        User student1 = createUser(createStudentCreationRequestDto(1), group);
        User student2 = createUser(createStudentCreationRequestDto(2), group);

        Session session11 = createSession(student1, laboratory1);
        Session session12 = createSession(student1, laboratory2);
        Session session21 = createSession(student2, laboratory1);
        Session session22 = createSession(student2, laboratory2);
        sessions.add(session11);
        sessions.add(session12);
        sessions.add(session21);
        sessions.add(session22);
    }

    @Test
    public void getAvailableTask_ShouldReturnOk_WhenValidRequest() throws Exception {
        Session session11 = sessions.getFirst();
        Attempt attempt11 = makeAttempt(1, session11);

        Session session12 = sessions.get(1);
        Attempt attempt12 = makeAttempt(2, session12);

        Session session21 = sessions.get(2);
        Attempt attempt21 = makeAttempt(3, session21);

        Session session22 = sessions.get(3);
        Attempt attempt22 = makeAttempt(4, session22);

        AvailableTaskResponse availableTaskResponse1 = taskTestSetup.getAvailableTask();
        assertEquals(attempt11.getId(), availableTaskResponse1.id());
        checkAvailableTaskResponse(availableTaskResponse1,
                taskTestSetup.getAvailableTaskResponse("task/available_task1.json"));

        AvailableTaskResponse availableTaskResponse2 = taskTestSetup.getAvailableTask();
        assertEquals(attempt12.getId(), availableTaskResponse2.id());
        checkAvailableTaskResponse(availableTaskResponse2,
                taskTestSetup.getAvailableTaskResponse("task/available_task2.json"));

        AvailableTaskResponse availableTaskResponse3 = taskTestSetup.getAvailableTask();
        assertEquals(attempt21.getId(), availableTaskResponse3.id());
        checkAvailableTaskResponse(availableTaskResponse3,
                taskTestSetup.getAvailableTaskResponse("task/available_task3.json"));

        AvailableTaskResponse availableTaskResponse4 = taskTestSetup.getAvailableTask();
        assertEquals(attempt22.getId(), availableTaskResponse4.id());
        checkAvailableTaskResponse(availableTaskResponse4,
                taskTestSetup.getAvailableTaskResponse("task/available_task4.json"));

        AvailableTaskResponse unavailableTaskResponse = taskTestSetup.getAvailableTask();
        checkAvailableTaskResponse(unavailableTaskResponse,
                taskTestSetup.getAvailableTaskResponse("task/unavailable_task.json"));

        Attempt attempt21Repeat = makeAttempt(3, session21);
        Attempt attempt22Repeat = makeAttempt(4, session22);

        availableTaskResponse3 = taskTestSetup.getAvailableTask();
        assertEquals(attempt21Repeat.getId(), availableTaskResponse3.id());
        checkAvailableTaskResponse(availableTaskResponse3,
                taskTestSetup.getAvailableTaskResponse("task/available_task3.json"));

        Attempt attempt12Repeat = makeAttempt(2, session12);

        availableTaskResponse4 = taskTestSetup.getAvailableTask();
        assertEquals(attempt22Repeat.getId(), availableTaskResponse4.id());
        checkAvailableTaskResponse(availableTaskResponse4,
                taskTestSetup.getAvailableTaskResponse("task/available_task4.json"));

        availableTaskResponse2 = taskTestSetup.getAvailableTask();
        assertEquals(attempt12Repeat.getId(), availableTaskResponse2.id());
        checkAvailableTaskResponse(availableTaskResponse2,
                taskTestSetup.getAvailableTaskResponse("task/available_task2.json"));

        unavailableTaskResponse = taskTestSetup.getAvailableTask();
        checkAvailableTaskResponse(unavailableTaskResponse,
                taskTestSetup.getAvailableTaskResponse("task/unavailable_task.json"));
    }

    @Test
    void saveResults_ShouldReturnOk_WhenValidRequest() throws Exception {
        Session session11 = sessions.getFirst();
        Attempt attempt11 = makeAttempt(1, session11);

        AvailableTaskResponse availableTaskResponse = taskTestSetup.getAvailableTask();
        List<TestResultsDto> testResults = List.of(
                new TestResultsDto(true, "Test 1 Passed", 1000, 500, "Test 1"),
                new TestResultsDto(false, "Test 2 Failed", 1200, 700, "Test 2")
        );

        AttemptResultSetRequest request = new AttemptResultSetRequest(
                availableTaskResponse.id(),
                true,
                1200L,
                testResults,
                false,
                "Some error details"
        );

        taskTestSetup.saveResult(request);
        AttemptDto attemptById = sessionTestSetup.getAttemptById(attempt11.getId());
        AttemptResultDto savedAttemptResult = attemptById.attemptResultDto();
        assertNotNull(savedAttemptResult);
        assertNotNull(savedAttemptResult.getTestResultsJson());
        checkAttemptResult(savedAttemptResult, taskTestSetup.getAttemptResultDto("attempt/attempt_results.json"));
    }

    @Test
    void saveAttemptResult_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        AttemptResultSetRequest invalidRequest = new AttemptResultSetRequest(
                null,
                null,
                -1L,
                null,
                null,
                null
        );
        taskTestSetup.saveResultInvalidRequest(invalidRequest);
    }

    private void checkAvailableTaskResponse(AvailableTaskResponse actual, AvailableTaskResponse expected) {
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    private Attempt makeAttempt(int order, Session session) {
        MakeAttemptDto makeAttemptDto = createMakeAttemptDto(order, session.getLaboratory().getId());
        Attempt attempt = attemptMapper.makeAttemptDtoToAttempt(makeAttemptDto);
        attempt = session.makeAttempt(attempt);
        return attemptRepository.save(attempt);
    }

    private Session createSession(User student, Laboratory laboratory) {
        Session session = new Session();
        session.setLaboratory(laboratory);
        session.setStudent(student);
        return sessionRepository.save(session);
    }

    private Laboratory createLaboratory(LaboratoryCreationRequestDto laboratoryCreationRequestDto,
                                        List<ru.nsu.ostest.adapter.out.persistence.entity.test.Test> tests) {
        Laboratory laboratory = laboratoryMapper.laboratoryCreationRequestDtoToLaboratory(laboratoryCreationRequestDto);
        tests.forEach(t -> laboratory.addTest(t, true));
        return laboratoryRepository.save(laboratory);
    }

    private User createUser(UserCreationRequestDto userCreationRequestDto, Group group) {
        User user = userMapper.userCreationRequestDtoToUser(userCreationRequestDto);
        user.setGroups(Set.of(group));
        return userRepository.save(user);
    }

    private Group createGroup(String name) {
        Group group = new Group();
        group.setName(name);
        return groupRepository.save(group);
    }

    private LaboratoryCreationRequestDto createLaboratoryCreationRequestDto(int order) {
        return new LaboratoryCreationRequestDto(LAB_NAME + order, order,
                LAB_DESCRIPTION + order, SEMESTER_NUMBER, DEADLINE, IS_HIDDEN, List.of());

    }

    private UserCreationRequestDto createStudentCreationRequestDto(int order) {
        return new UserCreationRequestDto(USER_NAME + order,
                FIRST_NAME + order,
                SECOND_NAME + order,
                GROUP_NUMBER, RoleEnum.STUDENT);

    }

    private MakeAttemptDto createMakeAttemptDto(int order, long laboratoryId) {
        return new MakeAttemptDto(REPOSITORY_URL + order, BRANCH_NAME + order, laboratoryId);

    }

    private ru.nsu.ostest.adapter.out.persistence.entity.test.Test createTest(int orderNumber) {
        TestCreationRequestDto testCreationRequestDto =
                new TestCreationRequestDto(TEST_NAME + orderNumber, TEST_DESCRIPTION,
                        TEST_CODE + orderNumber, TestCategory.DEFAULT);
        var test1 =
                testMapper.testCreationRequestDtoToTest(testCreationRequestDto);
        test1.setScriptBody(getBytesFromFile(createMultipartFile()));
        return testRepository.save(test1);
    }

    private MockMultipartFile createMultipartFile() {
        return new MockMultipartFile(
                "file",
                "testfile.txt",
                "text/plain",
                "test content".getBytes()
        );
    }

    private byte[] getBytesFromFile(MultipartFile file) {
        byte[] script;
        try {
            script = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return script;
    }

    private void checkAttemptResult(AttemptResultDto actual, AttemptResultDto expected) {
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
