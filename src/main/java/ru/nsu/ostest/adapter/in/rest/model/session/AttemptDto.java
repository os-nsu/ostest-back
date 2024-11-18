package ru.nsu.ostest.adapter.in.rest.model.session;

import ru.nsu.ostest.domain.common.enums.AttemptStatus;

import java.util.UUID;

public record AttemptDto(
        UUID id,
        String repositoryUrl,
        String branch,
        Long sequenceOrder,
        AttemptStatus status,
        AttemptResultDto attemptResultDto
) {
}
