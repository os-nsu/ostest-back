package ru.nsu.ostest.task;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.nsu.ostest.adapter.in.rest.config.AttemptProperties;
import ru.nsu.ostest.adapter.out.persistence.entity.session.Attempt;
import ru.nsu.ostest.adapter.out.persistence.entity.session.Session;
import ru.nsu.ostest.domain.common.enums.AttemptStatus;
import ru.nsu.ostest.domain.common.enums.SessionStatus;
import ru.nsu.ostest.domain.repository.AttemptRepository;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@Testcontainers
@AutoConfigureMockMvc(addFilters = false)
@EnableScheduling
@SpringBootTest
public class TasksSchedulerTest {

    @MockBean
    private AttemptRepository attemptRepository;

    @TestConfiguration
    public static class TestConfig {

        @Bean
        public AttemptProperties attemptProperties() {
            AttemptProperties mockProperties = mock(AttemptProperties.class);
            when(mockProperties.getTimeoutMinutes()).thenReturn(10L);
            when(mockProperties.getCheckTimeoutIntervalMs()).thenReturn(1000L);
            return mockProperties;
        }

        @Bean
        public ThreadPoolTaskScheduler taskScheduler() {
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setPoolSize(1);
            scheduler.initialize();
            return scheduler;
        }
    }

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:latest"
    );

    @Test
    public void testCheckAttemptTimeoutsChangesStatus() throws InterruptedException {
        Attempt mockAttempt1 = mock(Attempt.class);
        Attempt mockAttempt2 = mock(Attempt.class);
        Attempt mockAttempt3 = mock(Attempt.class);
        Attempt mockAttempt4 = mock(Attempt.class);
        Session mockSession1 = mock(Session.class);
        Session mockSession2 = mock(Session.class);

        Mockito.when(attemptRepository.findAll())
                .thenReturn(new ArrayList<>(List.of(mockAttempt1, mockAttempt2, mockAttempt3, mockAttempt4)));
        Mockito.when(attemptRepository.findAllByStatusAndCreatedAtBefore(any(), any()))
                .thenAnswer((Answer<List<Attempt>>) invocation -> {
                    Object[] args = invocation.getArguments();
                    AttemptStatus status = (AttemptStatus) args[0];
                    OffsetDateTime threshold = (OffsetDateTime) args[1];

                    return attemptRepository.findAll().stream()
                            .filter(attempt -> attempt.getStatus().equals(status)
                                    && attempt.getCreatedAt().isBefore(threshold))
                            .toList();
                });

        Mockito.when(mockAttempt1.getStatus()).thenReturn(AttemptStatus.IN_PROGRESS);
        Mockito.when(mockAttempt2.getStatus()).thenReturn(AttemptStatus.IN_QUEUE);
        Mockito.when(mockAttempt3.getStatus()).thenReturn(AttemptStatus.IN_PROGRESS);
        Mockito.when(mockAttempt4.getStatus()).thenReturn(AttemptStatus.IN_PROGRESS);

        Mockito.when(mockAttempt1.getCreatedAt()).thenReturn(OffsetDateTime.now().minusMinutes(11));
        Mockito.when(mockAttempt2.getCreatedAt()).thenReturn(OffsetDateTime.now().minusMinutes(11));
        Mockito.when(mockAttempt3.getCreatedAt()).thenReturn(OffsetDateTime.now().minusMinutes(4));
        Mockito.when(mockAttempt4.getCreatedAt()).thenReturn(OffsetDateTime.now().minusMinutes(10));

        Mockito.when(mockAttempt1.getSession()).thenReturn(mockSession1);
        Mockito.when(mockAttempt2.getSession()).thenReturn(mockSession1);

        Mockito.when(mockAttempt3.getSession()).thenReturn(mockSession2);
        Mockito.when(mockAttempt4.getSession()).thenReturn(mockSession2);

        Thread.sleep(1100L);

        verify(mockAttempt1, times(1)).setStatus(AttemptStatus.TIMEOUT);
        verify(mockAttempt2, times(0)).setStatus(AttemptStatus.TIMEOUT);
        verify(mockAttempt3, times(0)).setStatus(AttemptStatus.TIMEOUT);
        verify(mockAttempt4, times(1)).setStatus(AttemptStatus.TIMEOUT);

        verify(mockSession1, times(1)).setStatus(SessionStatus.FAILURE);
        verify(mockSession2, times(1)).setStatus(SessionStatus.FAILURE);
    }
}
