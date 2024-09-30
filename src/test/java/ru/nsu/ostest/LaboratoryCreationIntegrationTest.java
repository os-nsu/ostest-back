package ru.nsu.ostest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryCreationRequestDto;
import ru.nsu.ostest.domain.repository.LaboratoryRepository;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LaboratoryCreationIntegrationTest {

    private static final String URL = "/api/laboratory";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LaboratoryRepository laboratoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        laboratoryRepository.deleteAll();
    }

    @Test
    public void createLaboratory_ShouldReturnCreated_WhenValidRequest() throws Exception {
        String name = "Test Laboratory";
        String description = "Test Description";
        Integer semesterNumber = 1;
        LocalDateTime dateTime = LocalDateTime.now().plusDays(7);
        Boolean isHidden = false;
        LaboratoryCreationRequestDto request =
                new LaboratoryCreationRequestDto(name, description, semesterNumber, dateTime, isHidden);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Laboratory"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.semesterNumber").value("1"));
    }

    @Test
    public void createLaboratory_ShouldReturnConflict_WhenDuplicateName() throws Exception {
        String name = "Duplicate Laboratory";
        String description = "First Laboratory Description";
        int semesterNumber = 1;
        LocalDateTime dateTime = LocalDateTime.now().plusDays(7);
        boolean isHidden = false;
        LaboratoryCreationRequestDto request1 =
                new LaboratoryCreationRequestDto(name, description, semesterNumber, dateTime, isHidden);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        description = "Second Laboratory Description";
        semesterNumber = 2;
        dateTime = LocalDateTime.now().plusDays(10);
        isHidden = true;
        LaboratoryCreationRequestDto request2 =
                new LaboratoryCreationRequestDto(name, description, semesterNumber, dateTime, isHidden);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest());
    }
}