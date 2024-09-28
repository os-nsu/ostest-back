package ru.nsu.ostest.domain.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.nsu.ostest.adapter.in.rest.model.user.JwtRequest;
import ru.nsu.ostest.adapter.in.rest.model.user.UserCreationRequestDto;
import ru.nsu.ostest.adapter.mapper.UserMapper;
import ru.nsu.ostest.adapter.out.persistence.entity.user.Role;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;
import ru.nsu.ostest.adapter.out.persistence.entity.user.UserPassword;
import ru.nsu.ostest.adapter.out.persistence.entity.user.UserRole;
import ru.nsu.ostest.domain.repository.GroupRepository;
import ru.nsu.ostest.domain.repository.RoleRepository;
import ru.nsu.ostest.domain.repository.UserRepository;
import ru.nsu.ostest.security.exceptions.NotFoundException;
import ru.nsu.ostest.security.utils.PasswordGenerator;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final GroupRepository groupRepository;
    private final GroupService groupService;

    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Couldn't find user with id: " + id));
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new NotFoundException("Couldn't find user with username: " + username));
    }

    public JwtRequest addUser(UserCreationRequestDto userDto) throws BadRequestException {
        log.info("Adding user");
        validateUserDto(userDto);
        User user = userMapper.userCreationRequestDtoToUser(userDto);
        checkIfUsernameExists(userDto.username());
        String password = prepareUserForSaving(user, userDto);
        User savedUser = userRepository.save(user);
        log.info("User [{}] added", savedUser.getUsername());
        return new JwtRequest(savedUser.getUsername(), password);
    }

    private void validateUserDto(UserCreationRequestDto userDto) throws BadRequestException {
        if (userDto.username() == null) {
            log.error("User login is null");
            throw new BadRequestException("Login is null");
        }
    }

    private void checkIfUsernameExists(String username) throws BadRequestException {
        if (userRepository.findByUsername(username).isPresent()) {
            log.error("Login [{}] is already used", username);
            throw new BadRequestException("Login is already used");
        }
    }

    private String prepareUserForSaving(User user, UserCreationRequestDto userDto) {
        user.setId(null);
        user.setUsername(userDto.username());
        user.setRoles(createUserRoles(user, userDto.role()));
        user.setGroup(groupService.findGroupByName(userDto.groupNumber()));

        String password = PasswordGenerator.generatePassword();
        UserPassword userPassword = new UserPassword();
        userPassword.setUser(user);
        userPassword.setPassword(passwordEncoder.encode(password));

        user.setUserPassword(userPassword);
        return password;
    }

    private List<UserRole> createUserRoles(User user, String role) {
        Role userRole = getRoles(role);
        UserRole userRoleEntity = new UserRole();
        userRoleEntity.setUser(user);
        userRoleEntity.setRole(userRole);

        return List.of(userRoleEntity);
    }

    private Role getRoles(String role) {
        String dbRoleName;
        switch (role.toLowerCase()) {
            case "teacher" -> dbRoleName = "TEACHER";
            case "student" -> dbRoleName = "STUDENT";
            default ->
                    throw new IllegalArgumentException("Unknown role: " + role);
        }
        return findRole(dbRoleName);
    }

    private Role findRole(String name) {
        Optional<Role> roleOptional = roleRepository.findByName(name);
        if (roleOptional.isEmpty()) {
            log.error("Couldn't find role [{}]", name);
            throw new NotFoundException("Couldn't find role " + name);
        }
        return roleOptional.get();
    }

}
