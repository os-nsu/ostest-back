package ru.nsu.ostest.adapter.in.rest.model.session;

import ru.nsu.ostest.domain.common.model.TestResults;

import java.util.UUID;

public record AttemptDto(
        UUID id,
        String name,
        TestResults testResults
) {
}
