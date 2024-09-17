package ru.nsu.ostest.adapter.in.rest.controller;

import org.springframework.web.bind.annotation.*;
import ru.nsu.ostest.adapter.in.rest.model.user.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

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

    @PutMapping
    public UserDto editUser(@RequestBody UserEditionRequestDto request) {
        throw new IllegalArgumentException("Not implemented");
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        throw new IllegalArgumentException("Not implemented");
    }

}
