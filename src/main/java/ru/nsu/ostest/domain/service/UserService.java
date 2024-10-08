package ru.nsu.ostest.domain.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.mapstruct.MappingTarget;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.nsu.ostest.adapter.in.rest.model.user.UserCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.user.UserPasswordDto;
import ru.nsu.ostest.adapter.in.rest.model.user.UserUpdateRequestDto;
import ru.nsu.ostest.adapter.mapper.UserMapper;
import ru.nsu.ostest.adapter.mapper.UserUpdateDtoMapper;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;
import ru.nsu.ostest.adapter.out.persistence.entity.user.UserPassword;
import ru.nsu.ostest.domain.exception.UserNotFoundException;
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
    private final UserUpdateDtoMapper postMapper;

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
        validateCreationRequest(userDto);
        User user = userMapper.userCreationRequestDtoToUser(userDto);
        String password = prepareUserForSaving(user, userDto);
        User savedUser = userRepository.save(user);
        log.info("User with login: {} added", savedUser.getUsername());
        return new UserPasswordDto(savedUser.getUsername(), password);
    }

    private void validateCreationRequest(UserCreationRequestDto userDto) throws BadRequestException {
        if (userRepository.findByUsername(userDto.username()).isPresent()) {
            log.error("Login {} is already used", userDto.username());
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


    public void updateUser(Long userId, @MappingTarget UserUpdateRequestDto userUpdateDto) {
        log.info("Processing update profile request");
        if (userUpdateDto == null) {
            return;
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!AuthServiceCommon.checkAuthorities(user.getUsername())) {
            log.error("User has no rights to update profile");
            throw new AuthException("No rights");
        }

        postMapper.update(userUpdateDto, user);
        postMapper.mapGroup(userUpdateDto, user, groupService);

        userRepository.save(user);
    }
}
