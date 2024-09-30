package ru.nsu.ostest.adapter.in.rest.model.user;


import jakarta.validation.constraints.NotBlank;

@NotBlank
public record UserCreationRequestDto(
        String username,
        String firstName,
        String secondName,
        String groupNumber,
        RoleEnum role) {
}
