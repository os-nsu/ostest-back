package ru.nsu.ostest.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.nsu.ostest.adapter.in.rest.model.session.AttemptResultDto;
import ru.nsu.ostest.adapter.in.rest.model.session.AvailableTaskResponse;
import ru.nsu.ostest.adapter.in.rest.model.test.AttemptResultSetRequest;
import ru.nsu.ostest.adapter.in.rest.model.test.AttemptResultSetResponse;
import ru.nsu.ostest.domain.common.enums.AvailabilityStatus;
import ru.nsu.ostest.domain.repository.AttemptRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class TaskTestSetup {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AttemptRepository attemptRepository;

    public AvailableTaskResponse getAvailableTask() throws Exception {
        var result = mockMvc.perform(get("/api/task/available"))
                .andExpect(status().isOk())
                .andReturn();

        var availableTask =
                objectMapper.readValue(result.getResponse().getContentAsString(), AvailableTaskResponse.class);

        if (availableTask.status() == AvailabilityStatus.AVAILABLE) {
            assertTrue(attemptRepository.findById(availableTask.id()).isPresent());
        }

        return availableTask;
    }

    public AttemptResultSetResponse saveResult(AttemptResultSetRequest request) throws Exception {
        var result = mockMvc.perform(post("/api/task/result")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), AttemptResultSetResponse.class);
    }

    public void saveResultInvalidRequest(AttemptResultSetRequest invalidRequest) throws Exception {
        mockMvc.perform(post("/api/task/result")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    public AvailableTaskResponse getAvailableTaskResponse(String path) throws IOException {
        return objectMapper.readValue(
                Resources.toString(Resources.getResource(path), StandardCharsets.UTF_8), AvailableTaskResponse.class
        );
    }

    public AttemptResultDto getAttemptResultDto(String path) throws IOException {
        return objectMapper.readValue(
                Resources.toString(Resources.getResource(path), StandardCharsets.UTF_8), AttemptResultDto.class
        );
    }
}
