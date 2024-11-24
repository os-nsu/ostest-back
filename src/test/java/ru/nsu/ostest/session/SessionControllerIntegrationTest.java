package ru.nsu.ostest.session;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.nsu.ostest.TransactionalHelper;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryDto;
import ru.nsu.ostest.adapter.in.rest.model.session.*;
import ru.nsu.ostest.adapter.in.rest.model.user.role.RoleEnum;
import ru.nsu.ostest.adapter.in.rest.model.user.userData.UserCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.user.userData.UserDto;
import ru.nsu.ostest.adapter.out.persistence.entity.group.Group;
import ru.nsu.ostest.domain.repository.GroupRepository;
import ru.nsu.ostest.domain.repository.SessionRepository;
import ru.nsu.ostest.laboratory.LaboratoryTestSetup;
import ru.nsu.ostest.security.impl.JwtAuthentication;
import ru.nsu.ostest.user.UserTestSetup;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@Testcontainers
@AutoConfigureMockMvc(addFilters = false)
@Import({SessionTestSetup.class, LaboratoryTestSetup.class, UserTestSetup.class})
@SpringBootTest
public class SessionControllerIntegrationTest {
    private static final String GROUP_NUMBER = "21207";
    private static final String REPOSITORY_URL = "Url";
    private static final String BRANCH_NAME = "Branch";
    private static final String SESSION_USER1_LAB1_DTO = "session/session_user1_lab1.json";
    private static final String SESSION_USER2_LAB2_DTO = "session/session_user2_lab2.json";
    private static final String ATTEMPT1_DTO = "session/attempt1.json";
    private static final String ATTEMPT2_DTO = "session/attempt2.json";
    private static final List<UserDto> STUDENTS = new ArrayList<>();
    private static final List<LaboratoryDto> LABORATORIES = new ArrayList<>();

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:latest"
    );

    @Autowired
    private SessionTestMapper sessionTestMapper;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private SessionTestSetup sessionTestSetup;

    @Autowired
    private LaboratoryTestSetup laboratoryTestSetup;

    @Autowired
    private UserTestSetup userTestSetup;

    @Autowired
    private TransactionalHelper transactionalHelper;
    private static boolean setUpIsDone = false;

    @BeforeEach
    void init() {
        transactionalHelper.runInTransaction(this::initInTransaction);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void initInTransaction() {
        sessionRepository.deleteAll();
        if (setUpIsDone) {
            return;
        }
        try {
            String adminAuthority = "ADMIN";
            Authentication authentication = new JwtAuthentication(true, "password",
                    List.of(adminAuthority));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            createGroup();
            LaboratoryDto laboratory1 = createLaboratory(createLaboratoryCreationRequestDto(1));
            LaboratoryDto laboratory2 = createLaboratory(createLaboratoryCreationRequestDto(2));
            LABORATORIES.add(laboratory1);
            LABORATORIES.add(laboratory2);

            UserDto student1 = createUser(createStudentCreationRequestDto('1'));
            UserDto student2 = createUser(createStudentCreationRequestDto('2'));
            STUDENTS.add(student1);
            STUDENTS.add(student2);
            setUpIsDone = true;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Test
    public void createSession_ShouldReturnCreated_WhenValidRequest() throws Exception {
        UserDto student = STUDENTS.getFirst();
        LaboratoryDto laboratory = LABORATORIES.getFirst();

        var sessionDto = sessionTestSetup.startSession(new StartSessionRequestDto(student.id(), laboratory.id()));

        checkSession(sessionDto, sessionTestSetup.getSessionDto(SESSION_USER1_LAB1_DTO));
    }

    @Test
    public void makeAttempt_ShouldReturnCreated_WhenValidRequest() throws Exception {
        UserDto student = STUDENTS.getFirst();
        LaboratoryDto laboratory = LABORATORIES.getFirst();

        var sessionDto = sessionTestSetup.startSession(new StartSessionRequestDto(student.id(), laboratory.id()));
        checkSession(sessionDto, sessionTestSetup.getSessionDto(SESSION_USER1_LAB1_DTO));

        var makeAttemptDto = new MakeAttemptDto(REPOSITORY_URL, BRANCH_NAME, laboratory.id());

        var attemptDto = sessionTestSetup.makeAttempt(makeAttemptDto, sessionDto.id());
        checkAttempt(attemptDto, sessionTestSetup.getAttemptDto(ATTEMPT1_DTO));

        attemptDto = sessionTestSetup.makeAttempt(makeAttemptDto, sessionDto.id());
        checkAttempt(attemptDto, sessionTestSetup.getAttemptDto(ATTEMPT2_DTO));

        sessionDto = sessionTestSetup.getSessionById(sessionDto.id());
        checkSession(sessionDto, sessionTestSetup.getSessionDto("session/session_user1_lab1_with_attempts.json"));
    }

    @Test
    public void getAttemptById_ShouldReturnOk_WhenValidRequest() throws Exception {
        UserDto student = STUDENTS.getFirst();
        LaboratoryDto laboratory = LABORATORIES.getFirst();

        var sessionDto = sessionTestSetup.startSession(new StartSessionRequestDto(student.id(), laboratory.id()));
        checkSession(sessionDto, sessionTestSetup.getSessionDto(SESSION_USER1_LAB1_DTO));

        var makeAttemptDto = new MakeAttemptDto(REPOSITORY_URL, BRANCH_NAME, laboratory.id());

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
        UserDto student1 = STUDENTS.getFirst();
        UserDto student2 = STUDENTS.get(1);

        LaboratoryDto laboratory1 = LABORATORIES.getFirst();
        LaboratoryDto laboratory2 = LABORATORIES.get(1);

        var sessionDto1 =
                sessionTestSetup.startSession(new StartSessionRequestDto(student1.id(), laboratory1.id()));
        var sessionDto2 =
                sessionTestSetup.startSession(new StartSessionRequestDto(student2.id(), laboratory2.id()));

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
        UserDto student1 = STUDENTS.getFirst();
        UserDto student2 = STUDENTS.get(1);

        LaboratoryDto laboratory1 = LABORATORIES.getFirst();
        LaboratoryDto laboratory2 = LABORATORIES.get(1);

        var sessionDto1 =
                sessionTestSetup.startSession(new StartSessionRequestDto(student1.id(), laboratory1.id()));
        var sessionDto2 =
                sessionTestSetup.startSession(new StartSessionRequestDto(student2.id(), laboratory2.id()));

        checkSession(sessionDto1, sessionTestSetup.getSessionDto(SESSION_USER1_LAB1_DTO));
        checkSession(sessionDto2, sessionTestSetup.getSessionDto(SESSION_USER2_LAB2_DTO));

        var makeAttemptDto = new MakeAttemptDto(REPOSITORY_URL, BRANCH_NAME, laboratory1.id());
        sessionTestSetup.makeAttempt(makeAttemptDto, sessionDto1.id());
        sessionTestSetup.makeAttempt(makeAttemptDto, sessionDto1.id());

        makeAttemptDto = new MakeAttemptDto(REPOSITORY_URL, BRANCH_NAME, laboratory2.id());
        sessionTestSetup.makeAttempt(makeAttemptDto, sessionDto2.id());

        long session1Id = sessionDto1.id();
        long session2Id = sessionDto2.id();

        var sessionShortDto1 = sessionTestSetup.getLabSessionForStudent(
                new GetLabSessionFromStudentRequestDto(student1.id(), laboratory1.id()));
        var sessionShortDto2 = sessionTestSetup.getLabSessionForStudent(
                new GetLabSessionFromStudentRequestDto(student2.id(), laboratory2.id()));

        assertEquals(session1Id, sessionShortDto1.id());
        assertEquals(session2Id, sessionShortDto2.id());
        assertEquals(laboratory1.id(), sessionDto1.laboratory().id());
        assertEquals(laboratory2.id(), sessionDto2.laboratory().id());
        assertEquals(student1.id(), sessionDto1.student().id());
        assertEquals(student2.id(), sessionDto2.student().id());

        checkSessionShort(sessionShortDto1,
                sessionTestSetup.getSessionShortDto("session/session_short_user1_lab1.json"));
        checkSessionShort(sessionShortDto2,
                sessionTestSetup.getSessionShortDto("session/session_short_user2_lab2.json"));
    }

    @Test
    public void getUserSessions_ShouldReturnOk_WhenValidRequest() throws Exception {
        UserDto student1 = STUDENTS.getFirst();
        UserDto student2 = STUDENTS.get(1);

        LaboratoryDto laboratory1 = LABORATORIES.getFirst();
        LaboratoryDto laboratory2 = LABORATORIES.get(1);

        var sessionDto11 =
                sessionTestSetup.startSession(new StartSessionRequestDto(student1.id(), laboratory1.id()));
        var sessionDto22 =
                sessionTestSetup.startSession(new StartSessionRequestDto(student2.id(), laboratory2.id()));
        var sessionDto12 =
                sessionTestSetup.startSession(new StartSessionRequestDto(student1.id(), laboratory2.id()));
        var sessionDto21 =
                sessionTestSetup.startSession(new StartSessionRequestDto(student2.id(), laboratory1.id()));

        checkSession(sessionDto11, sessionTestSetup.getSessionDto(SESSION_USER1_LAB1_DTO));
        checkSession(sessionDto22, sessionTestSetup.getSessionDto(SESSION_USER2_LAB2_DTO));
        checkSession(sessionDto12, sessionTestSetup.getSessionDto("session/session_user1_lab2.json"));
        checkSession(sessionDto21, sessionTestSetup.getSessionDto("session/session_user2_lab1.json"));

        var user1Sessions = sessionTestSetup.getUserSessions(student1.id());
        var user2Sessions = sessionTestSetup.getUserSessions(student2.id());

        assertEquals(2, user1Sessions.getContent().size());
        assertEquals(2, user2Sessions.getContent().size());

        assertTrue(user1Sessions.getContent().contains(sessionTestMapper.sessionDtoToSessionShortDto(sessionDto11)));
        assertTrue(user1Sessions.getContent().contains(sessionTestMapper.sessionDtoToSessionShortDto(sessionDto12)));
        assertTrue(user2Sessions.getContent().contains(sessionTestMapper.sessionDtoToSessionShortDto(sessionDto21)));
        assertTrue(user2Sessions.getContent().contains(sessionTestMapper.sessionDtoToSessionShortDto(sessionDto22)));
    }

    private LaboratoryDto createLaboratory(LaboratoryCreationRequestDto laboratoryCreationRequestDto) throws Exception {
        return laboratoryTestSetup.createLaboratory(laboratoryCreationRequestDto);
    }

    private UserDto createUser(UserCreationRequestDto userCreationRequestDto) throws Exception {
        return userTestSetup.createUserReturnsUserDto(userCreationRequestDto);
    }

    private void createGroup() {
        Group group = new Group();
        group.setGroupName(GROUP_NUMBER);
        groupRepository.save(group);
    }

    private LaboratoryCreationRequestDto createLaboratoryCreationRequestDto(int order) {
        return new LaboratoryCreationRequestDto("Laboratory" + order, order,
                "Description" + order, 1, OffsetDateTime.parse("2024-10-07T07:02:27Z"),
                false, List.of());

    }

    private UserCreationRequestDto createStudentCreationRequestDto(char order) {
        return new UserCreationRequestDto("username" + order,
                "firstName" + order,
                "secondName" + order,
                GROUP_NUMBER, RoleEnum.STUDENT);

    }

    private void checkSession(SessionDto actual, SessionDto expected) {
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .ignoringFieldsMatchingRegexes(".*\\.id")
                .isEqualTo(expected);
    }

    private void checkSessionShort(SessionShortDto actual, SessionShortDto expected) {
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
