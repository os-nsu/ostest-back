package ru.nsu.ostest.adapter.in.rest.model.group;

public record GroupCreationRequestDto(
        String name,
        Boolean isArchived
) {
}
