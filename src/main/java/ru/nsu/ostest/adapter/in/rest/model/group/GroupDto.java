package ru.nsu.ostest.adapter.in.rest.model.group;

public record GroupDto(
        Long id,
        String groupName,
        Boolean isArchived
) {
}
