package ru.nsu.ostest.adapter.in.rest.model.laboratory;

import ru.nsu.ostest.adapter.in.rest.model.test.TestDto;

import java.time.OffsetDateTime;
import java.util.List;

public record LaboratoryDto(
        Long id,
        String name,
        Integer number,
        String description,
        Integer semesterNumber,
        OffsetDateTime deadline,
        Boolean isHidden,
        List<TestDto> tests
) {
}
