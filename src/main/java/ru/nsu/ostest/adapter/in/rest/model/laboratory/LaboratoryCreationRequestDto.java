package ru.nsu.ostest.adapter.in.rest.model.laboratory;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LaboratoryCreationRequestDto(String name,
                                           String description,
                                           Integer semesterNumber,
                                           @JsonFormat(shape = JsonFormat.Shape.STRING,
                                                   pattern = "yyyy-MM-dd'T'HH:mm:ss")
                                           LocalDateTime deadline,
                                           Boolean isHidden) {
}
