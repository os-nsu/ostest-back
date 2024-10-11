package ru.nsu.ostest.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MultipartFile;
import ru.nsu.ostest.adapter.in.rest.model.test.TestCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestDto;
import ru.nsu.ostest.adapter.out.persistence.entity.test.Test;
import ru.nsu.ostest.domain.repository.TestRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.mock.web.MockMultipartFile;

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

    public TestDto createTest(TestCreationRequestDto creationRequestDto, MockMultipartFile file) throws Exception {

        MockMultipartFile data = new MockMultipartFile(
                "data",
                "data.json",
                MediaType.APPLICATION_JSON_VALUE,
                new ObjectMapper().writeValueAsBytes(creationRequestDto)
        );

        var result = mockMvc.perform(multipart("/api/test")
                        .file(file)
                        .file(data)
                )
                .andExpect(status().isOk())
                .andReturn();


        var testDto = objectMapper.readValue(result.getResponse().getContentAsString(), TestDto.class);
        assertTrue(testRepository.findById(testDto.id()).isPresent());

        return testDto;
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

