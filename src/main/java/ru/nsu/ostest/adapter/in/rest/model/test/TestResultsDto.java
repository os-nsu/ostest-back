package ru.nsu.ostest.adapter.in.rest.model.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestResultsDto implements Serializable {
    private Boolean isPassed;
    private String description;
    private long memoryUsed;
    private long duration;
    private String name;
}