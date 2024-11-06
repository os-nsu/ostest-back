package ru.nsu.ostest.adapter.in.rest.model.laboratory;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import ru.nsu.ostest.adapter.in.rest.model.test.TestLaboratoryLinkDto;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record LaboratoryCreationRequestDto(@NotEmpty String name,
                                           Integer number,
                                           String description,
                                           Integer semesterNumber,
                                           OffsetDateTime deadline,
                                           Boolean isHidden,
                                           List<TestLaboratoryLinkDto> testsLinks) {
}
