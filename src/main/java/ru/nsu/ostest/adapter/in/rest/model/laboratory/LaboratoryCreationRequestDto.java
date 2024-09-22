package ru.nsu.ostest.adapter.in.rest.model.laboratory;

import java.time.LocalDateTime;

public record LaboratoryCreationRequestDto(String name,
                                           String description,
                                           Byte semesterNumber,
                                           LocalDateTime deadline,
                                           Boolean isHidden) {
}
