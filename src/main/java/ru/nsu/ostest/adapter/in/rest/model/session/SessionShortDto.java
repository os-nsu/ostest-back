package ru.nsu.ostest.adapter.in.rest.model.session;

import ru.nsu.ostest.adapter.in.rest.model.user.UserDto;
import ru.nsu.ostest.domain.common.enums.SessionStatus;

public record SessionShortDto(
        Long id,
        String laboratoryName,
        Integer attemptsNumber,
        SessionStatus status,
        UserDto student,
        UserDto teacher
) {
}
