package ru.nsu.ostest.adapter.in.rest.model.user.userData;

import org.openapitools.jackson.nullable.JsonNullable;

public record UserEditionRequestDto(
        JsonNullable<String> username,
        JsonNullable<String> firstName,
        JsonNullable<String> secondName,
        JsonNullable<Long> groupId) {
}
