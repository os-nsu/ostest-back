package ru.nsu.ostest.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.nsu.ostest.adapter.in.rest.model.filter.*;
import ru.nsu.ostest.adapter.in.rest.model.user.password.AdminChangePasswordDto;
import ru.nsu.ostest.adapter.in.rest.model.user.password.ChangePasswordDto;
import ru.nsu.ostest.adapter.in.rest.model.user.password.UserPasswordDto;
import ru.nsu.ostest.adapter.in.rest.model.user.role.RoleEnum;
import ru.nsu.ostest.adapter.in.rest.model.user.search.UserResponse;
import ru.nsu.ostest.adapter.in.rest.model.user.userData.UserCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.user.userData.UserDto;
import ru.nsu.ostest.adapter.in.rest.model.user.userData.UserEditionRequestDto;
import ru.nsu.ostest.adapter.out.persistence.entity.group.Group;
import ru.nsu.ostest.adapter.out.persistence.entity.user.StudentRating;
import ru.nsu.ostest.domain.repository.GroupRepository;
import ru.nsu.ostest.domain.repository.RatingRepository;
import ru.nsu.ostest.domain.repository.SessionRepository;
import ru.nsu.ostest.domain.repository.UserRepository;
import ru.nsu.ostest.security.AuthService;
import ru.nsu.ostest.security.impl.JwtAuthentication;
import ru.nsu.ostest.security.impl.JwtProviderImpl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Testcontainers
@SpringBootTest
@Import({UserTestSetup.class})
@AutoConfigureMockMvc(addFilters = false)
class UserControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:latest"
    );

    private static final String USER_USERNAME = "Test Username";
    private static final String USER_FIRSTNAME = "Test FirstName";
    private static final String USER_SECONDNAME = "Test SecondName";
    private static final String USER_GROUPNUMBER = "Test GroupNumber";
    private static final RoleEnum USER_ROLE = RoleEnum.STUDENT;

    @Autowired
    private UserTestSetup userTestSetup;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @MockBean
    private RatingRepository ratingRepository;

    @MockBean
    private JwtProviderImpl jwtProviderImpl;

    @MockBean
    private AuthService authService;

    @Autowired
    private SessionRepository sessionRepository;

    @BeforeEach
    void setUp() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
        groupRepository.deleteAll();

        groupRepository.save(new Group(USER_GROUPNUMBER, false));
        String adminAuthority = "ADMIN";
        Authentication authentication = new JwtAuthentication(true, "password", List.of(adminAuthority));
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createUser_ShouldReturnCreated_WhenValidRequest() throws Exception {
        var userDto = userTestSetup.createUserReturnsUserDto(
                new UserCreationRequestDto(USER_USERNAME, USER_FIRSTNAME, USER_SECONDNAME, USER_GROUPNUMBER, USER_ROLE)
        );

        checkUser(userDto, userTestSetup.getUserDto("user/user_created.json"));
    }

    @Test
    void createUser_ShouldReturnConflict_WhenDuplicateName() throws Exception {
        userTestSetup.createUserReturnsUserDto(
                new UserCreationRequestDto(USER_USERNAME, USER_FIRSTNAME, USER_SECONDNAME, USER_GROUPNUMBER, USER_ROLE)
        );

        userTestSetup.createUserBad(
                new UserCreationRequestDto(
                        USER_USERNAME,
                        "Second User FirstName",
                        "Second User SecondName",
                        "Second User GroupNumber",
                        USER_ROLE
                ));
    }

    @Test
    void deleteUser_ShouldReturnStatusOk_WhenUserExists() throws Exception {

        UserDto userDto = userTestSetup.createUserReturnsUserDto(
                new UserCreationRequestDto(USER_USERNAME, USER_FIRSTNAME, USER_SECONDNAME, USER_GROUPNUMBER, USER_ROLE)
        );

        userTestSetup.deleteUser(userDto.id());
    }

    @Test
    void searchUser_ShouldReturnAllFiltersStatusOk_WhenEmptyRequest() throws Exception {

        UserResponse userResponse = userTestSetup.searchUserReturnsUserResponse(
                new SearchRequestDto(null, null)
        );
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.filters()).hasSize(10);
        assertTrue(userResponse.filters().stream().map(FilterDescriptor::fieldName).toList().containsAll(List.of("username", "firstName", "secondName", "groupName", "roleName")));
        assertThat(userResponse.fieldsDescriptors()).hasSize(5);
        assertTrue(userResponse.fieldsDescriptors().stream().map(FieldDescriptor::name).toList().containsAll(List.of("username", "firstName", "secondName", "groupName", "roleName")));
    }

    @Test
    void searchUser_ShouldReturnStatusOk_WhenRequestHasFilter() throws Exception {
        userTestSetup.createUserReturnsUserDto(
                new UserCreationRequestDto(USER_USERNAME, USER_FIRSTNAME, USER_SECONDNAME, USER_GROUPNUMBER, USER_ROLE)
        );
        userTestSetup.createUserReturnsUserDto(
                new UserCreationRequestDto(
                        "Second User Username",
                        "Second User FirstName",
                        "Second User SecondName",
                        USER_GROUPNUMBER,
                        USER_ROLE
                ));

        SearchRequestDto userSearchRequestDto = new SearchRequestDto(new ArrayList<>(Collections.singleton(new Filter("username", false, "string", "Test"))), new Pagination(1, 2, 0, 0));
        UserResponse userResponse = userTestSetup.searchUserReturnsUserResponse(userSearchRequestDto);
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.users()).hasSize(1);
        assertThat(userResponse.users().getFirst().username()).isEqualTo(USER_USERNAME);
    }

    @Test
    void searchUser_ShouldReturnStatusOk_WhenRequestHasFilterAndPagination() throws Exception {
        userTestSetup.createUserReturnsUserDto(
                new UserCreationRequestDto(USER_USERNAME, USER_FIRSTNAME, USER_SECONDNAME, USER_GROUPNUMBER, USER_ROLE)
        );
        SearchRequestDto userSearchRequestDto = new SearchRequestDto(new ArrayList<>(Collections.singleton(new Filter("username", false, "string", "Test"))), null);
        UserResponse userResponse = userTestSetup.searchUserReturnsUserResponse(userSearchRequestDto);
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.users()).hasSize(1);
        assertThat(userResponse.users().getFirst().username()).isEqualTo(USER_USERNAME);
    }


    @Test
    void changeUserPassword_ShouldReturnStatusOk_WhenUserExists() throws Exception {
        var passwordDto = userTestSetup.createUserReturnsUserPasswordDto(
                new UserCreationRequestDto(USER_USERNAME, USER_FIRSTNAME, USER_SECONDNAME, USER_GROUPNUMBER, USER_ROLE)
        );
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn(USER_USERNAME);
        String newPassword = "newPass";
        String oldPassword = passwordDto.password();
        userTestSetup.changeUserPassword(new ChangePasswordDto(oldPassword, newPassword), mockPrincipal);
        userTestSetup.loginUser(new UserPasswordDto(USER_USERNAME, newPassword));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void changeUserPasswordFromAdmin_ShouldReturnStatusOk_WhenUserExists() throws Exception {
        UserDto user = userTestSetup.createUserReturnsUserDto(
                new UserCreationRequestDto(USER_USERNAME, USER_FIRSTNAME, USER_SECONDNAME, USER_GROUPNUMBER, USER_ROLE)
        );
        String newPassword = "newPass";
        userTestSetup.changeUserPassword(new AdminChangePasswordDto(newPassword), user.id());
        userTestSetup.loginUser(new UserPasswordDto(USER_USERNAME, newPassword));
    }

    @Test
    void loginUser_ShouldReturnStatusOk_WhenUserExists() throws Exception {
        UserPasswordDto userPasswordDto = userTestSetup.createUserReturnsUserPasswordDto(
                new UserCreationRequestDto(USER_USERNAME, USER_FIRSTNAME, USER_SECONDNAME, USER_GROUPNUMBER, USER_ROLE)
        );

        userTestSetup.loginUser(new UserPasswordDto(USER_USERNAME, userPasswordDto.password()));
    }

    @Test
    void getUser_ShouldReturnStatusOk_WhenUserExists() throws Exception {

        UserDto user = userTestSetup.createUserReturnsUserDto(
                new UserCreationRequestDto(USER_USERNAME, USER_FIRSTNAME, USER_SECONDNAME, USER_GROUPNUMBER, USER_ROLE)
        );

        mockUserAuthorization(user);

        UserDto userDto = userTestSetup.getCurrentUser();
        checkUser(userDto, userTestSetup.getUserDto("user/user_created.json"));
    }

    private void mockUserAuthorization(UserDto user) {
        String mockJwtToken = "mockToken";
        Long mockUserId = user.id();

        when(jwtProviderImpl.getTokenFromRequest(any(HttpServletRequest.class)))
                .thenReturn(mockJwtToken);

        when(jwtProviderImpl.validateAccessToken(mockJwtToken))
                .thenReturn(true);

        when(jwtProviderImpl.getUserIdFromJwt(mockJwtToken))
                .thenReturn(mockUserId);

        when(authService.getUserIdFromJwt(any(HttpServletRequest.class)))
                .thenReturn(mockUserId);
    }

    @Test
    void editUser_ShouldReturnCreated_WhenValidRequest() throws Exception {
        var userDto = userTestSetup.createUserReturnsUserDto(
                new UserCreationRequestDto(USER_USERNAME, USER_FIRSTNAME, USER_SECONDNAME, USER_GROUPNUMBER, USER_ROLE)
        );
        UserDto editedUserDto = userTestSetup.editUser(
                new UserEditionRequestDto(
                        JsonNullable.of("NewUserUserName"),
                        JsonNullable.of("NewUserFirstName"),
                        JsonNullable.of("NewUserSecondName"),
                        JsonNullable.undefined()
                ), userDto.id()
        );

        checkUser(editedUserDto,
                userTestSetup.getUserDto("user/user_edited.json"));
    }

    @Test
    void getTopUsers_ShouldReturnStatusOk() throws Exception {
        List<StudentRating> mockedUsers = List.of(new StudentRating(1l, "User1", "User1", "User1", 2l), new StudentRating(2l, "User2", "User2", "User2", 1l));

        when(ratingRepository.findTopNStudents(Integer.MAX_VALUE)).thenReturn(mockedUsers);

        var users = userTestSetup.getTopUsers();

        assertThat(users).isNotNull();
        assertThat(users).hasSize(2);
        assertThat(users.get(0).getUsername()).isEqualTo("User1");
    }

    @Test
    @Transactional
    void editUser_ShouldReturnConflict_WhenDuplicateName() throws Exception {
        UserDto createdUserDto = userTestSetup.createUserReturnsUserDto(
                new UserCreationRequestDto(USER_USERNAME, USER_FIRSTNAME, USER_SECONDNAME, USER_GROUPNUMBER, USER_ROLE)
        );

        userTestSetup.createUserReturnsUserDto(
                new UserCreationRequestDto("Second Username", USER_FIRSTNAME, USER_SECONDNAME, USER_GROUPNUMBER, USER_ROLE)
        );

        userTestSetup.editUserBad(
                new UserEditionRequestDto(
                        JsonNullable.of("Second Username"),
                        JsonNullable.undefined(),
                        JsonNullable.undefined(),
                        JsonNullable.undefined()
                ), createdUserDto.id()
        );
    }

    private void checkUser(UserDto actual, UserDto expected) {
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .ignoringFieldsMatchingRegexes("group.*")
                .isEqualTo(expected);
    }

}