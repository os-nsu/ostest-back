package ru.nsu.ostest.adapter.in.rest.model.laboratory;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record LaboratoryShortDto(
        Long id,
        String name,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime deadline,
        Boolean isHidden
) {
}
