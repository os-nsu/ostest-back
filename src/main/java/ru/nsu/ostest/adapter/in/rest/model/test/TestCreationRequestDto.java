package ru.nsu.ostest.adapter.in.rest.model.test;

import ru.nsu.ostest.domain.common.enums.TestCategory;

public record TestCreationRequestDto(
        String name,
        String description,
        TestCategory testCategory
) {
}
