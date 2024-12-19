package ru.nsu.ostest.laboratory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.*;
import ru.nsu.ostest.domain.repository.LaboratoryRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

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

    @Transactional
    public void deleteAllLabs() {
        laboratoryRepository.deleteAll();
    }

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
        mockMvc.perform(delete(PATH + "/{id}", labToDeleteId))
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

    public LaboratoryDto getLaboratoryById(Long id) throws Exception {
        var result = mockMvc.perform(get(PATH + "/{id}", id))
                .andExpect(status().isOk())
                .andReturn();

        var laboratory = objectMapper.readValue(result.getResponse().getContentAsString(), LaboratoryDto.class);

        assertTrue(laboratoryRepository.findById(laboratory.id()).isPresent());

        return laboratory;
    }

    public List<LaboratoryShortDto> searchLaboratories(LaboratorySearchRequestDto laboratorySearchRequestDto) throws Exception {
        var result = mockMvc.perform(post(PATH + "/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(laboratorySearchRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        var laboratories = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<LaboratoryShortDto>>() {
                });

        for (LaboratoryShortDto laboratory : laboratories) {
            assertTrue(laboratoryRepository.findById(laboratory.id()).isPresent());
        }

        return laboratories;
    }

    public void editLaboratoryBad(LaboratoryEditionRequestDto laboratoryEditionRequestDto) throws Exception {
        mockMvc.perform(put(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(laboratoryEditionRequestDto)))
                .andExpect(status().isBadRequest());
    }

    public LaboratoryDto getLaboratoryDto(String path) throws IOException {
        return objectMapper.readValue(
                Resources.toString(Resources.getResource(path), StandardCharsets.UTF_8), LaboratoryDto.class
        );
    }
}
