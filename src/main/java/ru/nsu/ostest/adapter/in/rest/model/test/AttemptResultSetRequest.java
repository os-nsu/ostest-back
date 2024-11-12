package ru.nsu.ostest.adapter.in.rest.model.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttemptResultSetRequest {
    private UUID id;
    private Boolean isPassed;
    private long duration;
    private List<TestResultsDto> testResults;
    private Boolean isError;
    private String errorDetails;
}