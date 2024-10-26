package ru.nsu.ostest.adapter.in.rest.model.user.userData;

import ru.nsu.ostest.adapter.in.rest.model.group.GroupDto;

public record UserDto(
        Long id,
        String username,
        String firstName,
        String secondName,
        GroupDto group
) {
}
