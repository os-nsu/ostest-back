package ru.nsu.ostest.adapter.in.rest.model.group;

import ru.nsu.ostest.adapter.in.rest.model.user.GroupMemberUserDto;

import java.util.List;

public record GroupFullDto(
        Long id,
        String name,
        List<GroupMemberUserDto> users
) {
}
