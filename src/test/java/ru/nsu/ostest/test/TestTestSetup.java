package ru.nsu.ostest.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import ru.nsu.ostest.adapter.in.rest.model.test.TestCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestEditionRequestDto;
import ru.nsu.ostest.domain.repository.TestRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    public void createTestBad(TestCreationRequestDto creationRequestDto, MockMultipartFile file) throws Exception {
        MockMultipartFile data = new MockMultipartFile(
                "data",
                "data.json",
                MediaType.APPLICATION_JSON_VALUE,
                new ObjectMapper().writeValueAsBytes(creationRequestDto)
        );

        mockMvc.perform(multipart("/api/test")
                        .file(file)
                        .file(data)
                )
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(1, testRepository.findAll().size());
    }

    public void deleteTest(Long testToDeleteId) throws Exception {
        mockMvc.perform(delete("/api/test/{id}", testToDeleteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testToDeleteId)))
                .andExpect(status().isOk());

        assertFalse(testRepository.findById(testToDeleteId).isPresent());
    }

    public TestDto editTest(TestEditionRequestDto testEditionRequestDto, MultipartFile editedFile) throws Exception {

        MockMultipartFile data = new MockMultipartFile(
                "data",
                "data.json",
                MediaType.APPLICATION_JSON_VALUE,
                new ObjectMapper().writeValueAsBytes(testEditionRequestDto)
        );

        var result = mockMvc.perform(multipart("/api/test")
                        .file((MockMultipartFile) editedFile)
                        .file(data)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                )
                .andExpect(status().isOk())
                .andReturn();


        var editedTest = objectMapper.readValue(result.getResponse().getContentAsString(), TestDto.class);

        assertTrue(testRepository.findById(editedTest.id()).isPresent());

        return editedTest;
    }

    public void editTestBad(TestEditionRequestDto testEditionRequestDto, MultipartFile editedFile) throws Exception {

        MockMultipartFile data = new MockMultipartFile(
                "data",
                "data.json",
                MediaType.APPLICATION_JSON_VALUE,
                new ObjectMapper().writeValueAsBytes(testEditionRequestDto)
        );

        mockMvc.perform(multipart("/api/test")
                        .file((MockMultipartFile) editedFile)
                        .file(data)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                )
                .andExpect(status().isBadRequest());
    }

    public byte[] getScript(Long id) throws Exception {

        var result = mockMvc.perform(get("/api/test/{id}/script", id)
                )
                .andExpect(status().isOk())
                .andReturn();

        return result.getResponse().getContentAsByteArray();
    }


    public TestDto getTestDto(String path) throws IOException {
        return objectMapper.readValue(
                Resources.toString(Resources.getResource(path), StandardCharsets.UTF_8), TestDto.class
        );
    }

}

