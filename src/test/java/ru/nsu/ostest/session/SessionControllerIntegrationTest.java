package ru.nsu.ostest.session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.session.SessionDto;
import ru.nsu.ostest.adapter.in.rest.model.session.StartSessionRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.user.RoleEnum;
import ru.nsu.ostest.adapter.in.rest.model.user.UserCreationRequestDto;
import ru.nsu.ostest.adapter.mapper.LaboratoryMapper;
import ru.nsu.ostest.adapter.mapper.UserMapper;
import ru.nsu.ostest.adapter.out.persistence.entity.group.Group;
import ru.nsu.ostest.adapter.out.persistence.entity.laboratory.Laboratory;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;
import ru.nsu.ostest.domain.repository.GroupRepository;
import ru.nsu.ostest.domain.repository.LaboratoryRepository;
import ru.nsu.ostest.domain.repository.SessionRepository;
import ru.nsu.ostest.domain.repository.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import({SessionTestSetup.class})
@AutoConfigureMockMvc(addFilters = false)
public class SessionControllerIntegrationTest {
    private static final String USER_NAME = "username";
    private static final String FIRST_NAME = "firstName";
    private static final String SECOND_NAME = "secondName";
    private static final String GROUP_NUMBER = "21207";
    private static final RoleEnum ROLE = RoleEnum.STUDENT;
    private static final String LAB_NAME = "Laboratory";
    private static final String LAB_DESCRIPTION = "Description";
    private static final boolean IS_HIDDEN = false;
    private static final int SEMESTER_NUMBER = 1;
    private static final LocalDateTime DEADLINE = LocalDateTime.parse("2024-10-07T07:02:27");

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

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        sessionRepository.deleteAll();
        laboratoryRepository.deleteAll();
    }

    @Test
    public void createSession_ShouldReturnOk_WhenValidRequest() throws Exception {
        Group group = createGroup(GROUP_NUMBER);

        Laboratory laboratory = createLaboratory(
                new LaboratoryCreationRequestDto(LAB_NAME, LAB_DESCRIPTION, SEMESTER_NUMBER, DEADLINE, IS_HIDDEN));

        User user =
                createUser(new UserCreationRequestDto(USER_NAME, FIRST_NAME, SECOND_NAME, GROUP_NUMBER, ROLE), group);

        var sessionDto = sessionTestSetup.startSession(new StartSessionRequestDto(user.getId(), laboratory.getId()));

        checkSession(sessionDto, sessionTestSetup.getSessionDto("session/session_started.json"));
    }

    private Laboratory createLaboratory(LaboratoryCreationRequestDto laboratoryCreationRequestDto) {
        Laboratory laboratory = laboratoryMapper.laboratoryCreationRequestDtoToLaboratory(laboratoryCreationRequestDto);
        return laboratoryRepository.save(laboratory);
    }

    private User createUser(UserCreationRequestDto userCreationRequestDto, Group group) {
        User user = userMapper.userCreationRequestDtoToUser(userCreationRequestDto);
        user.setGroup(group);
        return userRepository.save(user);
    }

    private Group createGroup(String name) {
        Group group = new Group();
        group.setName(name);
        return groupRepository.save(group);
    }

    private void checkSession(SessionDto actual, SessionDto expected) {
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .ignoringFieldsMatchingRegexes(".*\\.id")
                .isEqualTo(expected);
    }
}
