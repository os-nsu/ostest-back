package ru.nsu.ostest.group;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
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
import ru.nsu.ostest.adapter.in.rest.model.filter.*;
import ru.nsu.ostest.adapter.in.rest.model.group.*;
import ru.nsu.ostest.adapter.in.rest.model.user.auth.JwtResponse;
import ru.nsu.ostest.adapter.in.rest.model.user.password.UserPasswordDto;
import ru.nsu.ostest.adapter.in.rest.model.user.role.RoleEnum;
import ru.nsu.ostest.adapter.in.rest.model.user.userData.UserCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.user.userData.UserDto;
import ru.nsu.ostest.adapter.out.persistence.entity.group.Group;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;
import ru.nsu.ostest.domain.repository.GroupRepository;
import ru.nsu.ostest.domain.repository.UserRepository;
import ru.nsu.ostest.test.TestTestSetup;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Testcontainers
@AutoConfigureMockMvc(addFilters = true)
@Import({TestTestSetup.class})
@SpringBootTest(properties = {
})
@Slf4j
public class GroupControllerTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16"
    );

    @Autowired
    private MockMvc mockMvc;

    @Spy
    private ObjectMapper objectMapper;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        groupRepository.deleteAll();
    }


    @Test
    void testUserInGroupCreation_addsUserInGroup_ifCreateUserWithExistingGroupNameParameter() throws Exception {
        String groupName = "AAAAgroup1";

        GroupDto groupDto = createGroup(groupName);
        UserPasswordDto userPasswordDto = createUser(groupName, "user1");
        String accessToken = loginUser(userPasswordDto);
        UserDto userDto = getUserByAccessToken(accessToken);

        assertEquals(groupDto, userDto.group());
    }

    @Test
    void testDeleteGroup_deletesUserFromGroup_ifGroupDeleted() throws Exception {
        String groupName = "AAAAgroup1";

        GroupDto groupDto = createGroup(groupName);
        Long groupId = groupDto.id();
        UserPasswordDto userPasswordDto = createUser(groupName, "user1");
        String accessToken = loginUser(userPasswordDto);
        UserDto userDto = getUserByAccessToken(accessToken);

        assertEquals(groupDto, userDto.group());

        deleteGroup(groupId);

        String content = getDeletedGroupByIdMessage(groupId);
        assertTrue(content.contains(String.format("Group with id '%s' was not found", groupId)));

        UserDto userDto2 = getUserByAccessToken(accessToken);
        assertEquals(userDto2.id(), userDto.id());
        assertNull(userDto2.group());
    }

    @Test
    void testEditGroup_editsGroupAndUsers_ifGroupEdited() throws Exception {
        String groupName1 = "AAAAgroup1";
        String groupName2 = "AAAAgroup2";

        GroupDto group1Dto = createGroup(groupName1);
        Long group1Id = group1Dto.id();
        GroupDto group2Dto = createGroup(groupName2);
        Long group2Id = group2Dto.id();

        UserPasswordDto userPasswordDto1 = createUser(groupName1, "user1");
        String accessToken1 = loginUser(userPasswordDto1);
        UserDto userDto1 = getUserByAccessToken(accessToken1);

        UserPasswordDto userPasswordDto2 = createUser(groupName2, "user2");
        String accessToken2 = loginUser(userPasswordDto2);
        UserDto userDto2 = getUserByAccessToken(accessToken2);

        assertEquals(group1Dto, userDto1.group());
        assertEquals(group2Dto, userDto2.group());

        GroupDto editedGroup1Dto = editGroup(group1Id, "new name 1", new HashSet<>(List.of(userDto1.id())), new HashSet<>());
        GroupDto editedGroup2Dto = editGroup(group2Id, "new name 2", new HashSet<>(List.of(userDto2.id())), new HashSet<>(List.of(userDto1.id())));
        UserDto updatedUser1Dto = getUserByAccessToken(accessToken1);
        UserDto updatedUser2Dto = getUserByAccessToken(accessToken2);

        assertNotEquals(editedGroup1Dto, group1Dto);
        assertNotEquals(editedGroup2Dto, group2Dto);

        assertEquals(updatedUser1Dto.group(), editedGroup2Dto);
        assertNull(updatedUser2Dto.group());
    }

    private GroupDto editGroup(Long groupId, String newGroupName, Set<Long> usersToDelete, Set<Long> usersToAdd) throws Exception {

        GroupEditionRequestDto groupEditionRequestDto = new GroupEditionRequestDto(groupId, newGroupName, false, usersToAdd, usersToDelete);

        MockHttpServletResponse response = mockMvc.perform(put("/api/group")
                        .with(admin())
                        .content(objectMapper.writeValueAsString(groupEditionRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        GroupDto groupDto = objectMapper.readValue(response.getContentAsByteArray(), GroupDto.class);

        return groupDto;
    }

    private GroupDto createGroup(String groupName) throws Exception {
        GroupCreationRequestDto groupCreationRequestDto = new GroupCreationRequestDto(groupName, false);
        MockHttpServletResponse response = mockMvc.perform(post("/api/group")
                        .with(admin())
                        .content(objectMapper.writeValueAsString(groupCreationRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        GroupDto groupDto = objectMapper.readValue(response.getContentAsByteArray(), GroupDto.class);

        return groupDto;
    }

    private UserPasswordDto createUser(String groupName, String username) throws Exception {
        UserCreationRequestDto dto = new UserCreationRequestDto(username, "First", "Second", groupName, RoleEnum.ADMIN);
        MockHttpServletResponse response = mockMvc.perform(post("/api/user/registration")
                        .with(admin())
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse();

        UserPasswordDto userPasswordDto = objectMapper.readValue(response.getContentAsByteArray(), UserPasswordDto.class);

        return userPasswordDto;
    }

    private String loginUser(UserPasswordDto userPasswordDto) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(post("/api/v1/login")
                        .content(objectMapper.writeValueAsString(userPasswordDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        JwtResponse jwtResponse = objectMapper.readValue(response.getContentAsByteArray(), JwtResponse.class);
        String accessToken = jwtResponse.getAccessToken();

        return accessToken;
    }

    private UserDto getUserByAccessToken(String accessToken) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/api/user/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        UserDto userDto = objectMapper.readValue(response.getContentAsByteArray(), UserDto.class);

        return userDto;
    }

    private void deleteGroup(Long groupId) throws Exception {
        mockMvc.perform(delete("/api/group/{id}", groupId)
                        .with(admin())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
    }

    private String getDeletedGroupByIdMessage(Long groupId) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/api/group/{id}", groupId)
                        .with(admin())
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andReturn().getResponse();

        String content = response.getContentAsString();

        return content;
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
            log.error("group={}, {}", group, group.getGroupName());
            Set<User> users = group.getUsers();
            for (User user : users) {
                log.error("user={}, {}, {}, {} ", user, user.getUsername(), user.getFirstName(), user.getSecondName());
            }
        }
    }


    @Test
    void searchUser_ShouldReturnAllFiltersStatusOk_WhenEmptyRequest() throws Exception {

        GroupResponse userResponse = searchUserReturnsUserResponse(
                new SearchRequestDto(null, null)
        );
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.filters()).hasSize(2);
        assertTrue(userResponse.filters().stream().map(FilterDescriptor::fieldName).toList().containsAll(List.of("groupName")));
        assertThat(userResponse.fieldsDescriptors()).hasSize(1);
        assertTrue(userResponse.fieldsDescriptors().stream().map(FieldDescriptor::name).toList().containsAll(List.of("groupName")));
    }

    @Test
    void searchUser_ShouldReturnStatusOk_WhenRequestHasFilterAndPagination() throws Exception {
        createGroup("GROUP111");
        createGroup("GROUP112");

        SearchRequestDto userSearchRequestDto = new SearchRequestDto(new ArrayList<>(Collections.singleton(new Filter("groupName", false, "string", "GROUP"))), new Pagination(1, 1, 0, 0));
        GroupResponse groupResponse = searchUserReturnsUserResponse(userSearchRequestDto);
        assertThat(groupResponse).isNotNull();
        assertThat(groupResponse.groups()).hasSize(1);
        assertThat(groupResponse.groups().getFirst().groupName()).isEqualTo("GROUP111");
    }

    public GroupResponse searchUserReturnsUserResponse(SearchRequestDto searchRequest) throws Exception {
        var result = mockMvc.perform(post("/api/group/search")
                        .with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk()).andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), GroupResponse.class);
    }

    @Test
    void searchUser_ShouldReturnStatusOk_WhenRequestHasFilter() throws Exception {
        createGroup("GROUP111");

        SearchRequestDto userSearchRequestDto = new SearchRequestDto(new ArrayList<>(Collections.singleton(new Filter("groupName", false, "string", "111"))), null);
        GroupResponse userResponse = searchUserReturnsUserResponse(userSearchRequestDto);
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.groups()).hasSize(1);
        assertThat(userResponse.groups().getFirst().groupName()).isEqualTo("GROUP111");
    }

}