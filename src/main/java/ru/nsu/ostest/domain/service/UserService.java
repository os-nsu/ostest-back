package ru.nsu.ostest.domain.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.ostest.adapter.in.rest.config.BeanNamesConfig;
import ru.nsu.ostest.adapter.in.rest.model.filter.SearchRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.user.password.ChangePasswordDto;
import ru.nsu.ostest.adapter.in.rest.model.user.password.UserPasswordDto;
import ru.nsu.ostest.adapter.in.rest.model.user.search.UserResponse;
import ru.nsu.ostest.adapter.in.rest.model.user.userData.UserCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.user.userData.UserDto;
import ru.nsu.ostest.adapter.in.rest.model.user.userData.UserEditionRequestDto;
import ru.nsu.ostest.adapter.mapper.JsonNullableMapper;
import ru.nsu.ostest.adapter.mapper.UserMapper;
import ru.nsu.ostest.adapter.mapper.UserUpdateMapper;
import ru.nsu.ostest.adapter.out.persistence.entity.group.Group;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;
import ru.nsu.ostest.adapter.out.persistence.entity.user.UserPassword;
import ru.nsu.ostest.domain.exception.NoRightsException;
import ru.nsu.ostest.domain.exception.validation.DuplicateUserNameException;
import ru.nsu.ostest.domain.exception.validation.InvalidOldPasswordException;
import ru.nsu.ostest.domain.exception.validation.UserNotFoundException;
import ru.nsu.ostest.domain.repository.UserRepository;
import ru.nsu.ostest.security.impl.AuthServiceCommon;
import ru.nsu.ostest.security.utils.PasswordGenerator;

import java.util.List;
import java.util.Set;

import static ru.nsu.ostest.adapter.in.rest.model.filter.Pagination.createPagination;

@Slf4j
@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final GroupService groupService;
    private final RoleService roleService;
    @Qualifier(BeanNamesConfig.USER_FILTER_SERVICE)
    private final FilterService<User> filterService;
    private final UserUpdateMapper userUpdateMapper;
    private final MetaProvider<User> userProvider;
    private JsonNullableMapper jsonNullableMapper;

    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> UserNotFoundException.notFoundUserWithId(id));
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> UserNotFoundException.notFoundUserWithUsername(username));
    }

    public UserPasswordDto addUser(UserCreationRequestDto userDto) {
        log.info("Adding user");
        validateUsername(userDto.username());
        User user = userMapper.userCreationRequestDtoToUser(userDto);
        String password = prepareUserForSaving(user, userDto);
        User savedUser = userRepository.save(user);
        log.info("User with login: {} added", savedUser.getUsername());
        return new UserPasswordDto(savedUser.getUsername(), password);
    }

    private void validateUsername(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw DuplicateUserNameException.of(username);
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

    public void changePassword(String username, ChangePasswordDto passwordDto) {
        User user = findUserByUsername(username);
        UserPassword userPassword = user.getUserPassword();
        if (!passwordEncoder.matches(passwordDto.oldPassword(), userPassword.getPassword())) {
            throw new InvalidOldPasswordException();
        }
        userPassword.setPassword(passwordEncoder.encode(passwordDto.newPassword()));
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
            throw new NoRightsException();
        }
    }

    private void updateUsernameIfNeeded(UserEditionRequestDto userEditDto, User user) {
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

    public UserDto updateUser(Long userId, UserEditionRequestDto userEditDto) {
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

    public UserResponse getUsers(SearchRequestDto userRequest) {
        Pageable pageable = PageRequest.of(userRequest.pagination().getIndex() - 1, userRequest.pagination().getPageSize());

        Specification<User> spec = filterService.createSpecification(userRequest.filters());
        Page<User> userPage = userRepository.findAll(spec, pageable);

        return buildUserResponse(userPage);

    }

    public UserResponse buildUserResponse(Page<User> userPage) {
        return new UserResponse(
                createPagination(userPage),
                "Users",
                userProvider.getFieldDescriptors(),
                userProvider.getFilterDescriptors(),
                convertToUserRows(userPage.getContent())
        );
    }

    private List<UserDto> convertToUserRows(List<User> users) {
        return users.stream().map(userMapper::userToUserDto).toList();
    }

}
