package ru.nsu.ostest.session;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.nsu.ostest.adapter.in.rest.model.session.GetLabSessionFroStudentRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.session.SessionDto;
import ru.nsu.ostest.adapter.in.rest.model.session.StartSessionRequestDto;
import ru.nsu.ostest.domain.repository.SessionRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
    private MockMvc mockMvc;

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

    public SessionDto getLabSessionForStudent(GetLabSessionFroStudentRequestDto getLabSessionFroStudentRequestDto) throws Exception {
        var result = mockMvc.perform(get(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getLabSessionFroStudentRequestDto)))
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

}
