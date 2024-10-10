package ru.nsu.ostest.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.nsu.ostest.adapter.in.rest.model.test.TestCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestDto;
import ru.nsu.ostest.domain.repository.TestRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class TestTestSetup {

    private static final String PATH = "/api/test";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private MockMvc mockMvc;

    public TestDto createTest(TestCreationRequestDto creationRequestDto) throws Exception {
        var result = mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationRequestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        var test = objectMapper.readValue(result.getResponse().getContentAsString(), TestDto.class);

        assertTrue(testRepository.findById(test.id()).isPresent());

        return test;
    }

    public void createTestBad(TestCreationRequestDto creationRequestDto) throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals(1, testRepository.findAll().size());
    }

    public TestDto getTestDto(String path) throws IOException {
        return objectMapper.readValue(
                Resources.toString(Resources.getResource(path), StandardCharsets.UTF_8), TestDto.class
        );
    }

}

