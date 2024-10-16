package ru.nsu.ostest.adapter.in.rest.model.laboratory;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.nsu.ostest.adapter.in.rest.model.test.TestDto;

import java.time.LocalDateTime;
import java.util.List;

public record LaboratoryDto(
        Long id,
        String name,
        String description,
        Integer semesterNumber,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime deadline,
        Boolean isHidden,
        List<TestDto> tests
) {
}
