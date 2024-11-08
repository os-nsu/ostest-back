package ru.nsu.ostest.adapter.in.rest.model.laboratory;

import ru.nsu.ostest.adapter.in.rest.model.test.TestLaboratoryLinkDto;

import java.time.OffsetDateTime;
import java.util.List;

public record LaboratoryEditionRequestDto(Long id,
                                          String name,
                                          Integer number,
                                          String description,
                                          Integer semesterNumber,
                                          OffsetDateTime deadline,
                                          Boolean isHidden,
                                          List<TestLaboratoryLinkDto> addTestsLinks,
                                          List<TestLaboratoryLinkDto> editTestsLinks,
                                          List<TestLaboratoryLinkDto> deleteTestsLinks) {
}
