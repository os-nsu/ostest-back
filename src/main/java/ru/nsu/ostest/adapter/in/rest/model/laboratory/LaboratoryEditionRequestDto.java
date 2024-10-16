
package ru.nsu.ostest.adapter.in.rest.model.laboratory;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record LaboratoryEditionRequestDto(Long id,
                                          String name,
                                          String description,
                                          Integer semesterNumber,
                                          @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
                                          LocalDateTime deadline,
                                          Boolean isHidden) {
}
