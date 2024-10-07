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
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryEditionRequestDto;
import ru.nsu.ostest.domain.repository.LaboratoryRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
                .andExpect(status().isBadRequest());

        assertEquals(1, laboratoryRepository.findAll().size());
    }

    public void deleteLaboratory(Long labToDeleteId) throws Exception {
        mockMvc.perform(delete("/api/laboratory/{id}", labToDeleteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(labToDeleteId)))
                .andExpect(status().isOk());

        assertFalse(laboratoryRepository.findById(labToDeleteId).isPresent());
    }

    public LaboratoryDto editLaboratory(LaboratoryEditionRequestDto laboratoryEditionRequestDto) throws Exception {
        var result = mockMvc.perform(put(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(laboratoryEditionRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        var laboratory = objectMapper.readValue(result.getResponse().getContentAsString(), LaboratoryDto.class);

        assertTrue(laboratoryRepository.findById(laboratory.id()).isPresent());

        return laboratory;
    }

    public void editLaboratoryBad(LaboratoryEditionRequestDto laboratoryEditionRequestDto) throws Exception {
        mockMvc.perform(put(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(laboratoryEditionRequestDto)))
                .andExpect(status().isBadRequest());

        assertEquals(2, laboratoryRepository.findAll().size());
    }

    public LaboratoryDto getLaboratoryDto(String path) throws IOException {
        return objectMapper.readValue(
                Resources.toString(Resources.getResource(path), StandardCharsets.UTF_8), LaboratoryDto.class
        );
    }

}
