package ru.nsu.ostest.adapter.in.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.nsu.ostest.adapter.in.rest.model.filter.SearchRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.user.password.ChangePasswordDto;
import ru.nsu.ostest.adapter.in.rest.model.user.password.UserPasswordDto;
import ru.nsu.ostest.adapter.in.rest.model.user.search.UserResponse;
import ru.nsu.ostest.adapter.in.rest.model.user.userData.*;
import ru.nsu.ostest.domain.service.RatingService;
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
    private final RatingService ratingService;

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        throw new IllegalArgumentException("Not implemented");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public UserPasswordDto register(@RequestBody UserCreationRequestDto userDto) {
        return userService.addUser(userDto);
    }

    @GetMapping("/me")
    public UserDto getCurrentUserInfo(HttpServletRequest request) {
        return userService.getUserDtoById(authService.getUserIdFromJwt(request));
    }

    @PostMapping("/search")
    public UserResponse searchUsers(@RequestBody SearchRequestDto userRequest) {
        return userService.getUsers(userRequest);
    }

    @PostMapping("/batch")
    public List<UserDto> createUsers(@RequestBody UsersBatchCreationRequestDto request) {
        throw new IllegalArgumentException("Not implemented");
    }

    @PatchMapping("/{id}")
    public UserDto editUser(@PathVariable Long id, @NotNull @RequestBody UserEditionRequestDto userUpdateRequest) {
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

    @GetMapping("/rating")
    public List<StudentRatingDTO> getRating(@RequestParam(value = "topN", required = false) Integer topN) {
        return ratingService.getRating(topN);
    }

}