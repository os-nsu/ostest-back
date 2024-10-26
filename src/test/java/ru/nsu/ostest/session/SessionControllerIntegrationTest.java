package ru.nsu.ostest.session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.nsu.ostest.TransactionalHelper;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.session.GetLabSessionFroStudentRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.session.SessionDto;
import ru.nsu.ostest.adapter.in.rest.model.session.StartSessionRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.user.role.RoleEnum;
import ru.nsu.ostest.adapter.in.rest.model.user.userData.UserCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.session.*;
import ru.nsu.ostest.adapter.mapper.LaboratoryMapper;
import ru.nsu.ostest.adapter.mapper.UserMapper;
import ru.nsu.ostest.adapter.out.persistence.entity.group.Group;
import ru.nsu.ostest.adapter.out.persistence.entity.laboratory.Laboratory;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;
import ru.nsu.ostest.domain.repository.GroupRepository;
import ru.nsu.ostest.domain.repository.LaboratoryRepository;
import ru.nsu.ostest.domain.repository.SessionRepository;
import ru.nsu.ostest.domain.repository.UserRepository;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@AutoConfigureMockMvc(addFilters = false)
@Import({SessionTestSetup.class})
@SpringBootTest
public class SessionControllerIntegrationTest {
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
    private static final String SESSION_USER1_LAB1_DTO = "session/session_user1_lab1.json";
    private static final String SESSION_USER2_LAB2_DTO = "session/session_user2_lab2.json";
    private static final String ATTEMPT1_DTO = "session/attempt1.json";
    private static final String ATTEMPT2_DTO = "session/attempt2.json";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:latest"
    );

    @Autowired
    private LaboratoryMapper laboratoryMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LaboratoryRepository laboratoryRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private SessionTestSetup sessionTestSetup;
    private final List<User> students = new ArrayList<>();
    private final List<Laboratory> laboratories = new ArrayList<>();

    @Autowired
    private TransactionalHelper transactionalHelper;

    @BeforeEach
    void init() {
        transactionalHelper.runInTransaction(this::initInTransaction);
    }

    void initInTransaction() {
        laboratoryRepository.deleteAll();
        userRepository.deleteAll();
        groupRepository.deleteAll();
        sessionRepository.deleteAll();
        groupRepository.flush();

        Group group = createGroup(GROUP_NUMBER);
        Laboratory laboratory1 = createLaboratory(createLaboratoryCreationRequestDto(1));
        Laboratory laboratory2 = createLaboratory(createLaboratoryCreationRequestDto(2));
        laboratories.add(laboratory1);
        laboratories.add(laboratory2);

        User student1 = createUser(createStudentCreationRequestDto('1'), group);
        User student2 = createUser(createStudentCreationRequestDto('2'), group);
        students.add(student1);
        students.add(student2);
    }

    @Test
    public void createSession_ShouldReturnCreated_WhenValidRequest() throws Exception {
        User student = students.getFirst();
        Laboratory laboratory = laboratories.getFirst();

        var sessionDto = sessionTestSetup.startSession(new StartSessionRequestDto(student.getId(), laboratory.getId()));

        checkSession(sessionDto, sessionTestSetup.getSessionDto(SESSION_USER1_LAB1_DTO));
    }

    @Test
    public void makeAttempt_ShouldReturnCreated_WhenValidRequest() throws Exception {
        User student = students.getFirst();
        Laboratory laboratory = laboratories.getFirst();

        var sessionDto = sessionTestSetup.startSession(new StartSessionRequestDto(student.getId(), laboratory.getId()));
        checkSession(sessionDto, sessionTestSetup.getSessionDto(SESSION_USER1_LAB1_DTO));

        var makeAttemptDto = new MakeAttemptDto(REPOSITORY_URL, BRANCH_NAME, laboratory.getId());

        var attemptDto = sessionTestSetup.makeAttempt(makeAttemptDto, sessionDto.id());
        checkAttempt(attemptDto, sessionTestSetup.getAttemptDto(ATTEMPT1_DTO));

        attemptDto = sessionTestSetup.makeAttempt(makeAttemptDto, sessionDto.id());
        checkAttempt(attemptDto, sessionTestSetup.getAttemptDto(ATTEMPT2_DTO));

        sessionDto = sessionTestSetup.getSessionById(sessionDto.id());
        checkSession(sessionDto, sessionTestSetup.getSessionDto("session/session_user1_lab1_with_attempts.json"));
    }

    @Test
    public void getAttemptById_ShouldReturnOk_WhenValidRequest() throws Exception {
        User student = students.getFirst();
        Laboratory laboratory = laboratories.getFirst();

        var sessionDto = sessionTestSetup.startSession(new StartSessionRequestDto(student.getId(), laboratory.getId()));
        checkSession(sessionDto, sessionTestSetup.getSessionDto(SESSION_USER1_LAB1_DTO));

        var makeAttemptDto = new MakeAttemptDto(REPOSITORY_URL, BRANCH_NAME, laboratory.getId());

        var attemptDto1 = sessionTestSetup.makeAttempt(makeAttemptDto, sessionDto.id());
        checkAttempt(attemptDto1, sessionTestSetup.getAttemptDto(ATTEMPT1_DTO));

        var attemptDto2 = sessionTestSetup.makeAttempt(makeAttemptDto, sessionDto.id());
        checkAttempt(attemptDto2, sessionTestSetup.getAttemptDto(ATTEMPT2_DTO));

        UUID attempt1Id = attemptDto1.id();
        UUID attempt2Id = attemptDto2.id();

        attemptDto1 = sessionTestSetup.getAttemptById(attempt1Id);
        attemptDto2 = sessionTestSetup.getAttemptById(attempt2Id);

        assertEquals(attempt1Id, attemptDto1.id());
        assertEquals(attempt2Id, attemptDto2.id());

        checkAttempt(attemptDto1, sessionTestSetup.getAttemptDto(ATTEMPT1_DTO));
        checkAttempt(attemptDto2, sessionTestSetup.getAttemptDto(ATTEMPT2_DTO));
    }

    @Test
    public void getSessionById_ShouldReturnOk_WhenValidRequest() throws Exception {
        User student1 = students.getFirst();
        User student2 = students.get(1);

        Laboratory laboratory1 = laboratories.getFirst();
        Laboratory laboratory2 = laboratories.get(1);

        var sessionDto1 =
                sessionTestSetup.startSession(new StartSessionRequestDto(student1.getId(), laboratory1.getId()));
        var sessionDto2 =
                sessionTestSetup.startSession(new StartSessionRequestDto(student2.getId(), laboratory2.getId()));

        checkSession(sessionDto1, sessionTestSetup.getSessionDto(SESSION_USER1_LAB1_DTO));
        checkSession(sessionDto2, sessionTestSetup.getSessionDto(SESSION_USER2_LAB2_DTO));

        long session1Id = sessionDto1.id();
        long session2Id = sessionDto2.id();

        sessionDto1 = sessionTestSetup.getSessionById(session1Id);
        sessionDto2 = sessionTestSetup.getSessionById(session2Id);

        assertEquals(session1Id, sessionDto1.id());
        assertEquals(session2Id, sessionDto2.id());

        checkSession(sessionDto1, sessionTestSetup.getSessionDto(SESSION_USER1_LAB1_DTO));
        checkSession(sessionDto2, sessionTestSetup.getSessionDto(SESSION_USER2_LAB2_DTO));
    }

    @Test
    public void getLabSessionForStudent_ShouldReturnOk_WhenValidRequest() throws Exception {
        User student1 = students.getFirst();
        User student2 = students.get(1);

        Laboratory laboratory1 = laboratories.getFirst();
        Laboratory laboratory2 = laboratories.get(1);

        var sessionDto1 =
                sessionTestSetup.startSession(new StartSessionRequestDto(student1.getId(), laboratory1.getId()));
        var sessionDto2 =
                sessionTestSetup.startSession(new StartSessionRequestDto(student2.getId(), laboratory2.getId()));

        checkSession(sessionDto1, sessionTestSetup.getSessionDto(SESSION_USER1_LAB1_DTO));
        checkSession(sessionDto2, sessionTestSetup.getSessionDto(SESSION_USER2_LAB2_DTO));

        long session1Id = sessionDto1.id();
        long session2Id = sessionDto2.id();

        sessionDto1 = sessionTestSetup.getLabSessionForStudent(
                new GetLabSessionFromStudentRequestDto(student1.getId(), laboratory1.getId()));
        sessionDto2 = sessionTestSetup.getLabSessionForStudent(
                new GetLabSessionFromStudentRequestDto(student2.getId(), laboratory2.getId()));

        assertEquals(session1Id, sessionDto1.id());
        assertEquals(session2Id, sessionDto2.id());
        assertEquals(laboratory1.getId(), sessionDto1.laboratory().id());
        assertEquals(laboratory2.getId(), sessionDto2.laboratory().id());
        assertEquals(student1.getId(), sessionDto1.student().id());
        assertEquals(student2.getId(), sessionDto2.student().id());

        checkSession(sessionDto1, sessionTestSetup.getSessionDto(SESSION_USER1_LAB1_DTO));
        checkSession(sessionDto2, sessionTestSetup.getSessionDto(SESSION_USER2_LAB2_DTO));
    }

    @Test
    public void getUserSessions_ShouldReturnOk_WhenValidRequest() throws Exception {
        User student1 = students.getFirst();
        User student2 = students.get(1);

        Laboratory laboratory1 = laboratories.getFirst();
        Laboratory laboratory2 = laboratories.get(1);

        var sessionDto11 =
                sessionTestSetup.startSession(new StartSessionRequestDto(student1.getId(), laboratory1.getId()));
        var sessionDto22 =
                sessionTestSetup.startSession(new StartSessionRequestDto(student2.getId(), laboratory2.getId()));
        var sessionDto12 =
                sessionTestSetup.startSession(new StartSessionRequestDto(student1.getId(), laboratory2.getId()));
        var sessionDto21 =
                sessionTestSetup.startSession(new StartSessionRequestDto(student2.getId(), laboratory1.getId()));

        checkSession(sessionDto11, sessionTestSetup.getSessionDto(SESSION_USER1_LAB1_DTO));
        checkSession(sessionDto22, sessionTestSetup.getSessionDto(SESSION_USER2_LAB2_DTO));
        checkSession(sessionDto12, sessionTestSetup.getSessionDto("session/session_user1_lab2.json"));
        checkSession(sessionDto21, sessionTestSetup.getSessionDto("session/session_user2_lab1.json"));

        var user1Sessions = sessionTestSetup.getUserSessions(student1.getId());
        var user2Sessions = sessionTestSetup.getUserSessions(student2.getId());

        assertEquals(2, user1Sessions.size());
        assertEquals(2, user2Sessions.size());

        assertTrue(user1Sessions.contains(sessionDto11));
        assertTrue(user1Sessions.contains(sessionDto12));
        assertTrue(user2Sessions.contains(sessionDto21));
        assertTrue(user2Sessions.contains(sessionDto22));
    }

    private Laboratory createLaboratory(LaboratoryCreationRequestDto laboratoryCreationRequestDto) {
        Laboratory laboratory = laboratoryMapper.laboratoryCreationRequestDtoToLaboratory(laboratoryCreationRequestDto);
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

    private UserCreationRequestDto createStudentCreationRequestDto(char order) {
        return new UserCreationRequestDto(USER_NAME + order,
                FIRST_NAME + order,
                SECOND_NAME + order,
                GROUP_NUMBER, RoleEnum.STUDENT);

    }

    private void checkSession(SessionDto actual, SessionDto expected) {
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .ignoringFieldsMatchingRegexes(".*\\.id")
                .isEqualTo(expected);
    }

    private void checkAttempt(AttemptDto actual, AttemptDto expected) {
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }
}
