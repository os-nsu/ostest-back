package ru.nsu.ostest.adapter.in.rest.model.session;

import ru.nsu.ostest.domain.common.enums.AttemptStatus;

import java.util.UUID;

public record AttemptShortDto(
        UUID id,
        Long sequenceOrder,
        AttemptStatus status
) {
}
