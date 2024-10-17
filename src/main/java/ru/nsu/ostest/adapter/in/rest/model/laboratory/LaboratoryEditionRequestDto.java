package ru.nsu.ostest.adapter.in.rest.model.laboratory;

import java.time.LocalDateTime;

public record LaboratoryEditionRequestDto(Long id,
                                          String name,
                                          String description,
                                          Integer semesterNumber,
                                          LocalDateTime deadline,
                                          Boolean isHidden) {
}
