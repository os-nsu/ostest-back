package ru.nsu.ostest.adapter.in.rest.model.laboratory;

import java.time.OffsetDateTime;

public record LaboratoryShortDto(
        Long id,
        String name,
        OffsetDateTime deadline,
        String description,
        Boolean isHidden
) {
}
