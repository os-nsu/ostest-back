package ru.nsu.ostest.adapter.in.rest.model.user.userData;


import jakarta.validation.constraints.NotBlank;
import ru.nsu.ostest.adapter.in.rest.model.user.role.RoleEnum;

@NotBlank
public record UserCreationRequestDto(
        String username,
        String firstName,
        String secondName,
        String groupNumber,
        RoleEnum role) {
}
