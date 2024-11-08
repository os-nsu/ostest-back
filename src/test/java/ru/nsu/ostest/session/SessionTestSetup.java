package ru.nsu.ostest.session;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.nsu.ostest.adapter.in.rest.model.session.*;
import ru.nsu.ostest.domain.repository.AttemptRepository;
import ru.nsu.ostest.domain.repository.SessionRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class SessionTestSetup {

    private static final String PATH = "/api/session";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private AttemptRepository attemptRepository;
    @Autowired
    private MockMvc mockMvc;

    public AttemptDto makeAttempt(MakeAttemptDto makeAttemptDto, Long sessionId) throws Exception {
        var result = mockMvc.perform(post(PATH + "/{sessionId}/attempt", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(makeAttemptDto)))
                .andExpect(status().isCreated())
                .andReturn();

        var attemptDto = objectMapper.readValue(result.getResponse().getContentAsString(), AttemptDto.class);

        assertTrue(attemptRepository.findById(attemptDto.id()).isPresent());

        return attemptDto;
    }

    public AttemptDto getAttemptById(UUID id) throws Exception {
        var result = mockMvc.perform(get(PATH + "/attempt/{attemptId}", id))
                .andExpect(status().isOk())
                .andReturn();

        var attempt = objectMapper.readValue(result.getResponse().getContentAsString(), AttemptDto.class);

        assertTrue(attemptRepository.findById(attempt.id()).isPresent());

        return attempt;
    }


    public SessionDto startSession(StartSessionRequestDto startSessionRequestDto) throws Exception {
        var result = mockMvc.perform(post(PATH + "/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startSessionRequestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        var session = objectMapper.readValue(result.getResponse().getContentAsString(), SessionDto.class);

        assertTrue(sessionRepository.findById(session.id()).isPresent());

        return session;
    }

    public SessionDto getLabSessionForStudent(GetLabSessionFromStudentRequestDto getLabSessionFromStudentRequestDto)
            throws Exception {
        var result = mockMvc.perform(post(PATH + "/lab-student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getLabSessionFromStudentRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        var session = objectMapper.readValue(result.getResponse().getContentAsString(), SessionDto.class);

        assertTrue(sessionRepository.findById(session.id()).isPresent());

        return session;
    }

    public List<SessionDto> getUserSessions(Long userId) throws Exception {
        var result = mockMvc.perform(get(PATH + "/user/{userId}", userId))
                .andExpect(status().isOk())
                .andReturn();

        var sessionList = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<SessionDto>>() {
                });

        for (SessionDto session : sessionList) {
            assertTrue(sessionRepository.findById(session.id()).isPresent());
        }

        return sessionList;
    }

    public SessionDto getSessionById(Long id) throws Exception {
        var result = mockMvc.perform(get(PATH + "/{id}", id))
                .andExpect(status().isOk())
                .andReturn();

        var session = objectMapper.readValue(result.getResponse().getContentAsString(), SessionDto.class);

        assertTrue(sessionRepository.findById(session.id()).isPresent());

        return session;
    }

    public SessionDto getSessionDto(String path) throws IOException {
        return objectMapper.readValue(
                Resources.toString(Resources.getResource(path), StandardCharsets.UTF_8), SessionDto.class
        );
    }

    public AttemptDto getAttemptDto(String path) throws IOException {
        return objectMapper.readValue(
                Resources.toString(Resources.getResource(path), StandardCharsets.UTF_8), AttemptDto.class
        );
    }
}
