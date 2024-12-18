package ru.nsu.ostest.task;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.nsu.ostest.adapter.in.rest.config.AttemptProperties;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryDto;
import ru.nsu.ostest.adapter.in.rest.model.session.MakeAttemptDto;
import ru.nsu.ostest.adapter.in.rest.model.session.StartSessionRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.user.role.RoleEnum;
import ru.nsu.ostest.adapter.in.rest.model.user.userData.UserCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.user.userData.UserDto;
import ru.nsu.ostest.adapter.out.persistence.entity.group.Group;
import ru.nsu.ostest.adapter.out.persistence.entity.session.Attempt;
import ru.nsu.ostest.domain.common.enums.AttemptStatus;
import ru.nsu.ostest.domain.common.enums.SessionStatus;
import ru.nsu.ostest.domain.repository.AttemptRepository;
import ru.nsu.ostest.domain.repository.GroupRepository;
import ru.nsu.ostest.domain.repository.SessionRepository;
import ru.nsu.ostest.domain.repository.UserRepository;
import ru.nsu.ostest.laboratory.LaboratoryTestSetup;
import ru.nsu.ostest.security.impl.JwtAuthentication;
import ru.nsu.ostest.session.SessionTestSetup;
import ru.nsu.ostest.user.UserTestSetup;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@Testcontainers
@AutoConfigureMockMvc(addFilters = false)
@EnableScheduling
@Import({SessionTestSetup.class, LaboratoryTestSetup.class, UserTestSetup.class})
@SpringBootTest
@Slf4j
public class TasksSchedulerTest {

    private static final String GROUP_NUMBER = "21207";
    private static final String REPOSITORY_URL = "Url";
    private static final String BRANCH_NAME = "Branch";

    @Autowired
    private SessionTestSetup sessionTestSetup;

    @Autowired
    private LaboratoryTestSetup laboratoryTestSetup;

    @Autowired
    private UserTestSetup userTestSetup;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private AttemptRepository attemptRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:latest"
    );

    @TestConfiguration
    public static class TestConfig {

        @Bean
        public AttemptProperties attemptProperties() {
            AttemptProperties mockProperties = mock(AttemptProperties.class);
            when(mockProperties.getTimeoutMinutes()).thenReturn(10L);
            when(mockProperties.getCheckTimeoutIntervalMs()).thenReturn(1000L);
            return mockProperties;
        }
    }

    @BeforeEach
    void init() {
        attemptRepository.deleteAll();
        sessionRepository.deleteAll();
        userRepository.deleteAll();
        groupRepository.deleteAll();

        try {
            String adminAuthority = "ADMIN";
            Authentication authentication = new JwtAuthentication(true, "password",
                    List.of(adminAuthority));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCheckAttemptTimeoutsChangesStatus() throws Exception {
        createGroup();
        LaboratoryDto laboratory1 =
                laboratoryTestSetup.createLaboratory(createLaboratoryCreationRequestDto(1));
        LaboratoryDto laboratory2 =
                laboratoryTestSetup.createLaboratory(createLaboratoryCreationRequestDto(2));

        UserDto student1 = userTestSetup.createUserReturnsUserDto(createStudentCreationRequestDto('1'));
        UserDto student2 = userTestSetup.createUserReturnsUserDto(createStudentCreationRequestDto('2'));

        var sessionDto11 =
                sessionTestSetup.startSession(new StartSessionRequestDto(student1.id(), laboratory1.id()));
        var sessionDto12 =
                sessionTestSetup.startSession(new StartSessionRequestDto(student1.id(), laboratory2.id()));
        var sessionDto21 =
                sessionTestSetup.startSession(new StartSessionRequestDto(student2.id(), laboratory1.id()));
        var sessionDto22 =
                sessionTestSetup.startSession(new StartSessionRequestDto(student2.id(), laboratory2.id()));

        var makeAttemptLab1Dto = new MakeAttemptDto(REPOSITORY_URL, BRANCH_NAME, laboratory1.id());
        var attemptDto11 = sessionTestSetup.makeAttempt(makeAttemptLab1Dto, sessionDto11.id());
        var attemptDto21 = sessionTestSetup.makeAttempt(makeAttemptLab1Dto, sessionDto21.id());

        var makeAttemptLab2Dto = new MakeAttemptDto(REPOSITORY_URL, BRANCH_NAME, laboratory2.id());
        var attemptDto12 = sessionTestSetup.makeAttempt(makeAttemptLab2Dto, sessionDto12.id());
        var attemptDto22 = sessionTestSetup.makeAttempt(makeAttemptLab2Dto, sessionDto22.id());

        Attempt attempt11 = attemptRepository.findById(attemptDto11.id()).orElse(null);
        Attempt attempt12 = attemptRepository.findById(attemptDto12.id()).orElse(null);
        Attempt attempt21 = attemptRepository.findById(attemptDto21.id()).orElse(null);
        Attempt attempt22 = attemptRepository.findById(attemptDto22.id()).orElse(null);

        if (attempt11 != null) {
            attempt11.setCreatedAt(OffsetDateTime.now().minusMinutes(11L));
            attempt11.setStatus(AttemptStatus.IN_PROGRESS);
            attemptRepository.save(attempt11);
        }

        if (attempt12 != null) {
            attempt12.setCreatedAt(OffsetDateTime.now().minusMinutes(11L));
            attemptRepository.save(attempt12);
        }

        if (attempt21 != null) {
            attempt21.setCreatedAt(OffsetDateTime.now().minusMinutes(4L));
            attempt21.setStatus(AttemptStatus.IN_PROGRESS);
            attemptRepository.save(attempt21);
        }

        if (attempt22 != null) {
            attempt22.setCreatedAt(OffsetDateTime.now().minusMinutes(10L));
            attempt22.setStatus(AttemptStatus.IN_PROGRESS);
            attemptRepository.save(attempt22);
        }

        Thread.sleep(1100L);

        attempt11 = attemptRepository.findById(attemptDto11.id()).orElse(null);
        attempt12 = attemptRepository.findById(attemptDto12.id()).orElse(null);
        attempt21 = attemptRepository.findById(attemptDto21.id()).orElse(null);
        attempt22 = attemptRepository.findById(attemptDto22.id()).orElse(null);

        assertNotNull(attempt11);
        assertEquals(AttemptStatus.TIMEOUT, attempt11.getStatus());
        assertEquals(SessionStatus.FAILURE, attempt11.getSession().getStatus());

        assertNotNull(attempt12);
        assertEquals(AttemptStatus.IN_QUEUE, attempt12.getStatus());
        assertEquals(SessionStatus.IN_PROGRESS, attempt12.getSession().getStatus());

        assertNotNull(attempt21);
        assertEquals(AttemptStatus.IN_PROGRESS, attempt21.getStatus());
        assertEquals(SessionStatus.IN_PROGRESS, attempt21.getSession().getStatus());

        assertNotNull(attempt22);
        assertEquals(AttemptStatus.TIMEOUT, attempt22.getStatus());
        assertEquals(SessionStatus.FAILURE, attempt22.getSession().getStatus());
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
}
