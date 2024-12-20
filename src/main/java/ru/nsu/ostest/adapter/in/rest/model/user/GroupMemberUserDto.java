package ru.nsu.ostest.adapter.in.rest.model.user;

import ru.nsu.ostest.adapter.in.rest.model.user.role.RoleDto;

import java.util.List;

public record GroupMemberUserDto(
        Long id,
        String username,
        String firstName,
        String secondName,
        List<RoleDto> roles
) {
}
