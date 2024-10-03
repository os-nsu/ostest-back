package ru.nsu.ostest.adapter.in.rest.model.laboratory;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LaboratoryCreationRequestDto(String name,
                                           String description,
                                           Integer semesterNumber,
                                           LocalDateTime deadline,
                                           Boolean isHidden) {
}
