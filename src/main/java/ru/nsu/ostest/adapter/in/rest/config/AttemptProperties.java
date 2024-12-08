package ru.nsu.ostest.adapter.in.rest.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "attempt")
@Data
public class AttemptProperties {

    @Value("${attempt.timeout.minutes:10}")
    private long timeoutMinutes;

    @Value("${attempt.check.timeout.interval.ms:60000}")
    private long checkTimeoutIntervalMs;
}