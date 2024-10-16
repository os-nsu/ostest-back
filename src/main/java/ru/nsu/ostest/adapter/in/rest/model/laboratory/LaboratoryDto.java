package ru.nsu.ostest.adapter.in.rest.model.laboratory;

import ru.nsu.ostest.adapter.in.rest.model.test.TestDto;

import java.time.LocalDateTime;
import java.util.List;

public record LaboratoryDto(
        Long id,
        String name,
        String description,
        Integer semesterNumber,
        LocalDateTime deadline,
        Boolean isHidden,
        List<TestDto> tests
) {
}
