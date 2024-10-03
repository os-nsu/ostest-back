package ru.nsu.ostest.laboratory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryDto;
import ru.nsu.ostest.domain.repository.LaboratoryRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class LaboratoryTestSetup {

    private static final String PATH = "/api/laboratory";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LaboratoryRepository laboratoryRepository;

    @Autowired
    private MockMvc mockMvc;

    public LaboratoryDto createLaboratory(LaboratoryCreationRequestDto creationRequestDto) throws Exception {
        var result = mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationRequestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        var laboratory = objectMapper.readValue(result.getResponse().getContentAsString(), LaboratoryDto.class);

        assertTrue(laboratoryRepository.findById(laboratory.id()).isPresent());

        return laboratory;
    }

    public void createLaboratoryBad(LaboratoryCreationRequestDto creationRequestDto) throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals(1, laboratoryRepository.findAll().size());
    }

    public LaboratoryDto getLaboratoryDto(String path) throws IOException {
        return objectMapper.readValue(
                Resources.toString(Resources.getResource(path), StandardCharsets.UTF_8), LaboratoryDto.class
        );
    }

}
