package ru.nsu.ostest.adapter.in.rest.model.session;

import ru.nsu.ostest.domain.common.model.TestResults;

public record AttemptDto(
        Long id,
        String name,
        TestResults testResults
) {
}
