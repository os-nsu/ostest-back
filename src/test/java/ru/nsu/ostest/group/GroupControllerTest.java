package ru.nsu.ostest.group;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.nsu.ostest.TransactionalHelper;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupDto;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupEditionRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.user.*;
import ru.nsu.ostest.adapter.out.persistence.entity.group.Group;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;
import ru.nsu.ostest.domain.repository.GroupRepository;
import ru.nsu.ostest.domain.repository.UserRepository;
import ru.nsu.ostest.test.TestTestSetup;

import java.util.List;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


//@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, statements = "delete from test")
@Testcontainers
@AutoConfigureMockMvc(addFilters = true)
@Import({TestTestSetup.class})
@SpringBootTest(properties = {
//        "logging.level.sql=trace",
//        "logging.level.org.hibernate.orm.jdbc.bind=trace",
})
@Slf4j
public class GroupControllerTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16"
    );

    @Autowired private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired private GroupRepository groupRepository;

    @Autowired private TransactionalHelper transactionalHelper;
    @Autowired
    private UserRepository userRepository;

    @Test
    void test_addUserToGroup() throws Exception {
        GroupCreationRequestDto groupCreationRequestDto = new GroupCreationRequestDto("GROUP111", false);
        MockHttpServletResponse response = mockMvc.perform(post("/api/group")
                        .with(admin())
                        .content(objectMapper.writeValueAsString(groupCreationRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        GroupDto groupDto = objectMapper.readValue(response.getContentAsByteArray(), GroupDto.class);
        Long groupId = groupDto.id();

        UserCreationRequestDto dto = new UserCreationRequestDto("user1", "First", "Second", "ZZZ", RoleEnum.ADMIN);
        response = mockMvc.perform(post("/api/user/registration")
                        .with(admin())
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse();

        UserPasswordDto userPasswordDto = objectMapper.readValue(response.getContentAsByteArray(), UserPasswordDto.class);

        response = mockMvc.perform(post("/api/v1/login")
                        .content(objectMapper.writeValueAsString(userPasswordDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        JwtResponse jwtResponse = objectMapper.readValue(response.getContentAsByteArray(), JwtResponse.class);
        String accessToken = jwtResponse.getAccessToken();

        response = mockMvc.perform(get("/api/user/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        UserDto userDto = objectMapper.readValue(response.getContentAsByteArray(), UserDto.class);
        Long userId = userDto.id();

        GroupEditionRequestDto groupEditionRequestDto = new GroupEditionRequestDto(groupId, "GROUP222", false, Set.of(2L, userId), null);
        response = mockMvc.perform(put("/api/group")
                        .with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(groupEditionRequestDto))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        groupDto = objectMapper.readValue(response.getContentAsByteArray(), GroupDto.class);

        log.info("--> show groups 1");
        transactionalHelper.runInTransaction(this::showGroups);
        // todo: сделать assert на наличие группы у пользователя

        groupEditionRequestDto = new GroupEditionRequestDto(groupId, "GROUP222", false, null, Set.of(userId));
        response = mockMvc.perform(put("/api/group")
                        .with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(groupEditionRequestDto))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        log.info("--> show groups 2");
        transactionalHelper.runInTransaction(this::showGroups);
        // todo: сделать проверку, что пользователь уделён из группы

        response = mockMvc.perform(delete("/api/group/{id}", groupId)
                        .with(admin())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        log.info("--> show users");
        transactionalHelper.runInTransaction(this::showUsers);
        // todo: проверку

        response = mockMvc.perform(get("/api/group/search")
                        .param("size", "25")
//                        .param("page", "3")
                        .param("sort", "name,asc")
                        .with(admin())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        String contentAsString = response.getContentAsString();
        log.info("=-=-=> {}", contentAsString);
    }

    private void showUsers() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            log.info("user={}, {}, {}, {} ", user, user.getUsername(), user.getFirstName(), user.getSecondName());
        }
    }

    private UserRequestPostProcessor admin() {
        return user("admin")
                .authorities(new SimpleGrantedAuthority("ADMIN"));
    }

    private void showGroups() {
        List<Group> groups = groupRepository.findAll();
        for (Group group : groups) {
            log.info("group={}, {}", group, group.getName());
            Set<User> users = group.getUsers();
            for (User user : users) {
                log.info("user={}, {}, {}, {} ", user, user.getUsername(), user.getFirstName(), user.getSecondName());
            }
        }
    }
}