package ru.nsu.ostest.domain.common.model;

import lombok.Data;

@Data
public class TestResults {
    private boolean isPassed;
    private String description;
    private long memoryUsed;
    private long duration;
    private String name;
}
