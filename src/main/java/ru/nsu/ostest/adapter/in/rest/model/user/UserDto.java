package ru.nsu.ostest.adapter.in.rest.model.user;

public record UserDto(
        Long id,
        String username,
        String firstName,
        String secondName
) {
}
