package ru.nsu.ostest.test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import ru.nsu.ostest.adapter.in.rest.model.test.TestCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestEditionRequestDto;
import ru.nsu.ostest.domain.repository.TestRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import({TestTestSetup.class})
@AutoConfigureMockMvc(addFilters = false)
public class TestControllerIntegrationTest {

    private static final String PATH = "/api/test";

    private static final String TEST_NAME = "Test Test";
    private static final String TEST_DESCRIPTION = "Test Description";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestTestSetup testTestSetup;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        testRepository.deleteAll();
    }

    @Test
    public void createTest_ShouldReturnCreated_WhenValidRequest() throws Exception {
        var testDto = testTestSetup.createTest(
                new TestCreationRequestDto(TEST_NAME, TEST_DESCRIPTION, null)
        );

        checkTest(testDto, testTestSetup.getTestDto("test/test_created.json"));
    }

    @Test
    public void createTest_ShouldReturnConflict_WhenDuplicateName() throws Exception {
        testTestSetup.createTest(
                new TestCreationRequestDto(TEST_NAME, TEST_DESCRIPTION, null)
        );

        testTestSetup.createTestBad(
                new TestCreationRequestDto(
                        TEST_NAME,
                        "Second Test Description",
                        null
                ));
    }

    @Test
    public void deleteTest_ShouldReturnStatusOk_WhenTestExists() throws Exception {
        String name = "Test Test to Delete";
        String description = "Test Description";
        TestCreationRequestDto request =
                new TestCreationRequestDto(name, description, null);

        MockHttpServletResponse mvcResponse = mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description))
                .andReturn()
                .getResponse();

        try (JsonParser jsonParser = objectMapper.createParser(mvcResponse.getContentAsByteArray())) {
            TestDto test = jsonParser.readValueAs(TestDto.class);

            Long testId = test.id();
            assertNotEquals(null, testRepository.findById(testId).orElse(null));//todo: тут throw

            mockMvc.perform(delete("/api/test/{id}", testId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            assertNull(testRepository.findById(testId).orElse(null));
        }
    }

    @Test
    public void editTest_ShouldReturnCreated_WhenValidRequest() throws Exception {
        String name = "Test Test to Edit";
        String description = "Test Description Not Edited";
        Integer semesterNumber = 1;
        LocalDateTime dateTime = LocalDateTime.now().plusDays(7);
        boolean isHidden = false;
        TestCreationRequestDto creationRequest =
                new TestCreationRequestDto(name, description, null);

        MockHttpServletResponse mvcResponse = mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.semesterNumber").value(semesterNumber))
                .andExpect(jsonPath("$.isHidden").value(isHidden))
                .andReturn()
                .getResponse();

        description = "Edited Test Description";
        isHidden = true;

        try (JsonParser jsonParser = objectMapper.createParser(mvcResponse.getContentAsByteArray())) {
            TestDto test = jsonParser.readValueAs(TestDto.class);

            Long testId = test.id();

            TestEditionRequestDto editionRequest =
                    new TestEditionRequestDto(testId, name, description, null);

            mockMvc.perform(put(PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editionRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(name))
                    .andExpect(jsonPath("$.description").value(description));
        }
    }

    @Test
    public void editTest_ShouldReturnConflict_WhenDuplicateName() throws Exception {
        String duplicatedName = "Duplicate Test to Edit";
        String description = "Test Description Not Edited";
        TestCreationRequestDto creationRequest =
                new TestCreationRequestDto(duplicatedName, description, null);

        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationRequest)))
                .andExpect(status().isCreated());

        String name = "Test to Edit";

        creationRequest =
                new TestCreationRequestDto(name, description, null);

        MockHttpServletResponse mvcResponse = mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        description = "Edited Test Description";

        try (JsonParser jsonParser = objectMapper.createParser(mvcResponse.getContentAsByteArray())) {
            TestDto test = jsonParser.readValueAs(TestDto.class);

            Long testId = test.id();

            TestEditionRequestDto editionRequest =
                    new TestEditionRequestDto(testId, duplicatedName, description, null);

            mockMvc.perform(put(PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editionRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    private void checkTest(TestDto actual, TestDto expected) {
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id", "category", "scriptBody", "laboratoriesLinks")
                .isEqualTo(expected);
    }

}