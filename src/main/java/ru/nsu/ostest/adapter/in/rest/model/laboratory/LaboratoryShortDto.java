package ru.nsu.ostest.adapter.in.rest.model.laboratory;

import java.time.OffsetDateTime;

public record LaboratoryShortDto(
        Long id,
        String name,
        String description,
        OffsetDateTime deadline,
        Boolean isHidden
) {
}
