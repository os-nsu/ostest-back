package ru.nsu.ostest.adapter.in.rest.model.session;

import lombok.Data;
import ru.nsu.ostest.adapter.in.rest.model.test.TestResultsDto;

import java.util.List;

@Data
public class AttemptResultDto {
    private List<TestResultsDto> testResults;
    private long duration;
    private String errorDetails;
}