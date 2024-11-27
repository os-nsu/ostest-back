package ru.nsu.ostest.adapter.in.rest.model.test;

import ru.nsu.ostest.domain.common.enums.TestCategory;

public record LaboratoryTestDto(
        Long id,
        String name,
        String description,
        String code,
        TestCategory category,
        Boolean isSwitchedOn
) {
}
