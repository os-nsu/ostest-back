package ru.nsu.ostest.domain.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.ostest.adapter.in.rest.model.user.UserCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.user.UserDto;
import ru.nsu.ostest.adapter.in.rest.model.user.UserEditionRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.user.UserPasswordDto;
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

@Slf4j
@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final GroupService groupService;
    private final RoleService roleService;
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
        user.setGroup(groupService.findGroupByName(userDto.groupNumber()));

        String password = PasswordGenerator.generatePassword();
        UserPassword userPassword = UserPassword.builder()
                .user(user)
                .password(passwordEncoder.encode(password))
                .build();

        user.setUserPassword(userPassword);
        return password;
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
            user.setGroup(updatedGroup);
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
}
