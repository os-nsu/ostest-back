package ru.nsu.ostest.adapter.in.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nsu.ostest.adapter.in.rest.model.user.*;
import ru.nsu.ostest.domain.service.UserService;
import ru.nsu.ostest.security.AuthService;

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

    @GetMapping("/me")
    public UserDto getCurrentUserInfo() {
        // Тут через security можно выцепить инфу по текущему пользователю
        throw new IllegalArgumentException("Not implemented");
    }

    @PostMapping("/search")
    public List<UserDto> searchUsers(@RequestBody UserSearchRequestDto request) {
        throw new IllegalArgumentException("Not implemented");
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserCreationRequestDto request) {
        throw new IllegalArgumentException("Not implemented");
    }

    @PostMapping("/batch")
    public List<UserDto> createUsers(@RequestBody UsersBatchCreationRequestDto request) {
        throw new IllegalArgumentException("Not implemented");
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> editUser(@PathVariable Long id, @RequestBody UserUpdateRequestDto userUpdateRequest) {
        userService.updateUser(id, userUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        throw new IllegalArgumentException("Not implemented");
    }

}
