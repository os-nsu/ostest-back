package ru.nsu.ostest.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.nsu.ostest.adapter.in.rest.model.session.SessionDto;
import ru.nsu.ostest.adapter.in.rest.model.session.StartSessionRequestDto;
import ru.nsu.ostest.domain.repository.SessionRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;
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
