package ru.nsu.ostest.adapter.in.rest.model.laboratory;

import java.time.LocalDateTime;

public record LaboratoryShortDto(
        Long id,
        String name,
        LocalDateTime deadline,
        Boolean isHidden
) {
}
