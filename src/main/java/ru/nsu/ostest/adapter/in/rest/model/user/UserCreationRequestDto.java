package ru.nsu.ostest.adapter.in.rest.model.user;

public record UserCreationRequestDto(
        String username,
        String firstName,
        String secondName,
        String groupNumber,
        String role) {
}
