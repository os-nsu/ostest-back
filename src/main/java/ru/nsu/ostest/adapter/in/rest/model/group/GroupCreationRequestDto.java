package ru.nsu.ostest.adapter.in.rest.model.group;

import java.util.List;

public record GroupCreationRequestDto(
        String name,
        List<Long> usersIds
) {
}
