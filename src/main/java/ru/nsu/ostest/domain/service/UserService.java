package ru.nsu.ostest.domain.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.ostest.adapter.in.rest.model.filter.FieldDescriptor;
import ru.nsu.ostest.adapter.in.rest.model.filter.FilterDescriptor;
import ru.nsu.ostest.adapter.in.rest.model.filter.Pagination;
import ru.nsu.ostest.adapter.in.rest.model.user.password.UserPasswordDto;
import ru.nsu.ostest.adapter.in.rest.model.user.search.UserResponse;
import ru.nsu.ostest.adapter.in.rest.model.user.search.UserSearchRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.user.userData.UserCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.user.userData.UserDto;
import ru.nsu.ostest.adapter.in.rest.model.user.userData.UserEditionRequestDto;
import ru.nsu.ostest.adapter.mapper.JsonNullableMapper;
import ru.nsu.ostest.adapter.mapper.UserMapper;
import ru.nsu.ostest.adapter.mapper.UserUpdateMapper;
import ru.nsu.ostest.adapter.out.persistence.entity.group.Group;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;
import ru.nsu.ostest.adapter.out.persistence.entity.user.UserPassword;
import ru.nsu.ostest.domain.repository.UserRepository;
import ru.nsu.ostest.security.exceptions.AuthException;
import ru.nsu.ostest.security.exceptions.NotFoundException;
import ru.nsu.ostest.security.impl.AuthServiceCommon;
import ru.nsu.ostest.security.utils.PasswordGenerator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final GroupService groupService;
    private final RoleService roleService;
    private final FilterService filterService;
    private final UserUpdateMapper userUpdateMapper;
    private JsonNullableMapper jsonNullableMapper;


    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Couldn't find user with id: " + id));
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new NotFoundException("Couldn't find user with username: " + username));
    }

    public UserPasswordDto addUser(UserCreationRequestDto userDto) throws BadRequestException {
        log.info("Adding user");
        validateUsername(userDto.username());
        User user = userMapper.userCreationRequestDtoToUser(userDto);
        String password = prepareUserForSaving(user, userDto);
        User savedUser = userRepository.save(user);
        log.info("User with login: {} added", savedUser.getUsername());
        return new UserPasswordDto(savedUser.getUsername(), password);
    }

    private void validateUsername(String username) throws BadRequestException {
        if (userRepository.findByUsername(username).isPresent()) {
            log.error("Login {} is already used", username);
            throw new BadRequestException("Login is already used");
        }
    }

    private String prepareUserForSaving(User user, UserCreationRequestDto userDto) {
        user.setId(null);
        user.addRole(roleService.findRole(userDto.role()));
        Group group = groupService.findGroupByName(userDto.groupNumber());
        user.getGroups().add(group);

        String password = PasswordGenerator.generatePassword();
        setUserPassword(password, user);
        return password;
    }

    public void changePassword(String username, String newPassword) {
        User user = findUserByUsername(username);
        UserPassword userPassword = user.getUserPassword();
        userPassword.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void changePassword(Long userId, String newPassword) {
        User user = findUserById(userId);
        UserPassword userPassword = user.getUserPassword();
        userPassword.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private void setUserPassword(String newPassword, User user) {
        UserPassword userPassword = UserPassword.builder()
                .user(user)
                .password(passwordEncoder.encode(newPassword))
                .build();

        user.setUserPassword(userPassword);
    }

    public UserDto getUserDtoById(Long userId) {
        User user = findUserById(userId);
        return userMapper.userToUserDto(user);
    }

    private void validateUserAccess(User user) {
        if (!AuthServiceCommon.hasAccessOrAdminRole(user.getUsername())) {
            log.error("User has no rights to update profile");
            throw new AuthException("No rights");
        }
    }

    private void updateUsernameIfNeeded(UserEditionRequestDto userEditDto, User user) throws BadRequestException {
        if (userEditDto.username().isPresent() && !user.getUsername().equals(userEditDto.username().get())) {
            validateUsername(userEditDto.username().get());
        }
    }

    private void updateGroupIfNeeded(UserEditionRequestDto userEditDto, User user) {
        if (jsonNullableMapper.isPresentAndNotNull(userEditDto.groupId())) {
            Group updatedGroup = groupService.findGroupById(userEditDto.groupId().get());
            user.setGroups(Set.of(updatedGroup));
        }
    }

    public UserDto updateUser(Long userId, UserEditionRequestDto userEditDto) throws BadRequestException {
        log.info("Processing update profile request");
        User user = findUserById(userId);
        validateUserAccess(user);
        updateUsernameIfNeeded(userEditDto, user);
        updateGroupIfNeeded(userEditDto, user);
        userUpdateMapper.update(userEditDto, user);

        userRepository.save(user);
        return userMapper.userToUserDto(user);
    }

    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public UserResponse getUsers(UserSearchRequestDto userRequest) {
        Pageable pageable = PageRequest.of(userRequest.pagination().index() - 1, userRequest.pagination().pageSize());

        Specification<User> spec = filterService.createSpecification(userRequest.filters());
        Page<User> userPage = userRepository.findAll(spec, pageable);

        return buildUserResponse(userPage);

    }

    public UserResponse buildUserResponse(Page<User> userPage) {
        return new UserResponse(
                createPagination(userPage),
                "Users",
                getFieldDescriptors(),
                getFilterDescriptors(),
                convertToUserDtos(userPage.getContent())
        );
    }

    private Pagination createPagination(Page<User> userPage) {
        return new Pagination(
                userPage.getNumber() + 1,
                userPage.getSize(),
                (int) userPage.getTotalElements(),
                userPage.getTotalPages()
        );
    }

    private List<FieldDescriptor> getFieldDescriptors() {
        List<FieldDescriptor> descriptors = new ArrayList<>();

        Field[] fields = User.class.getDeclaredFields();
        for (Field field : fields) {
            if (!field.getName().equals("userPassword")) {
                descriptors.add(new FieldDescriptor(field.getType().getSimpleName(), field.getName()));
            }
        }

        return descriptors;
    }


    private List<FilterDescriptor> getFilterDescriptors() {
        List<FilterDescriptor> filters = new ArrayList<>();

        Field[] fields = User.class.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            String fieldType = field.getType().getSimpleName();
            if (fieldType.equals("groups") || fieldType.equals("roles")) {
                fieldType = "String";
            }
            if (!fieldName.equals("userPassword")) {
                filters.add(new FilterDescriptor(fieldType, fieldName, false));
                if (fieldType.equals("String")) {
                    filters.add(new FilterDescriptor(fieldType, fieldName, true));
                }
            }
        }

        return filters;
    }


    private List<UserDto> convertToUserDtos(List<User> users) {
        return users.stream().map(userMapper::userToUserDto).toList();
    }

}
