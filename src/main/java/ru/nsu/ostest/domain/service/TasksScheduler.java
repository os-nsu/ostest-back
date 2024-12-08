package ru.nsu.ostest.domain.service;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.nsu.ostest.adapter.in.rest.config.AttemptProperties;

@Service
@EnableScheduling
@AllArgsConstructor
public class TasksScheduler {

    private final AttemptService attemptService;

    private final AttemptProperties attemptProperties;

    @Scheduled(fixedRateString = "#{attemptProperties.checkTimeoutIntervalMs}")
    public void checkAttemptTimeouts() {
        attemptService.checkForTimeouts(attemptProperties.getTimeoutMinutes());
    }
}