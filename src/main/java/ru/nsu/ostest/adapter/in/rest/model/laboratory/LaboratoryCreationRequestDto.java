package ru.nsu.ostest.adapter.in.rest.model.laboratory;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import ru.nsu.ostest.adapter.in.rest.model.test.TestDto;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record LaboratoryCreationRequestDto(@NotEmpty String name,
                                           String description,
                                           Integer semesterNumber,
                                           LocalDateTime deadline,
                                           Boolean isHidden,
                                           List<TestDto> tests) {
}
