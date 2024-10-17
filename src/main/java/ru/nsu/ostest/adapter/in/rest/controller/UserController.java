package ru.nsu.ostest.adapter.in.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.nsu.ostest.adapter.in.rest.model.user.*;
import ru.nsu.ostest.domain.service.UserService;
import ru.nsu.ostest.security.AuthService;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        throw new IllegalArgumentException("Not implemented");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public UserPasswordDto register(@RequestBody UserCreationRequestDto userDto) throws BadRequestException {
        return userService.addUser(userDto);
    }

    @GetMapping("/me")
    public UserDto getCurrentUserInfo(HttpServletRequest request) {
        return userService.getUserDtoById(authService.getUserIdFromJwt(request));
    }

    @PostMapping("/search")
    public List<UserDto> searchUsers(@RequestBody UserSearchRequestDto request) {
        throw new IllegalArgumentException("Not implemented");
    }

    @PostMapping("/batch")
    public List<UserDto> createUsers(@RequestBody UsersBatchCreationRequestDto request) {
        throw new IllegalArgumentException("Not implemented");
    }

    @PatchMapping("/{id}")
    public UserDto editUser(@PathVariable Long id, @NotNull @RequestBody UserEditionRequestDto userUpdateRequest) throws BadRequestException {
        return userService.updateUser(id, userUpdateRequest);
    }

    @PutMapping("/change-password")
    public void changePassword(@RequestBody ChangePasswordDto changePasswordDto, @NotNull Principal principal) {
        String username = principal.getName();
        userService.changePassword(username, changePasswordDto.newPassword());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/change-password/{id}")
    public void changePassword(@RequestBody ChangePasswordDto changePasswordDto, @PathVariable Long id) {
        userService.changePassword(id, changePasswordDto.newPassword());
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
    }

}