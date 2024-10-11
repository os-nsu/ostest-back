package ru.nsu.ostest.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.nsu.ostest.adapter.in.rest.model.test.TestCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestEditionRequestDto;
import ru.nsu.ostest.domain.repository.TestRepository;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.mock.web.MockMultipartFile;

@SpringBootTest
@Import({TestTestSetup.class})
@AutoConfigureMockMvc(addFilters = false)
public class TestControllerIntegrationTest {

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

        TestCreationRequestDto request = new TestCreationRequestDto(TEST_NAME, TEST_DESCRIPTION, null);
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "testfile.txt",
                "text/plain",
                "test content".getBytes()
        );


        var testDto = testTestSetup.createTest(request, file);

        checkTest(testDto, testTestSetup.getTestDto("test/test_created.json"));
    }

    @Test
    public void createTest_ShouldReturnConflict_WhenDuplicateName() throws Exception {

        TestCreationRequestDto request = new TestCreationRequestDto(TEST_NAME, TEST_DESCRIPTION, null);
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "testfile.txt",
                "text/plain",
                "test content".getBytes()
        );

        testTestSetup.createTestBad(request, file);
    }

    @Test
    public void deleteTest_ShouldReturnStatusOk_WhenTestExists() throws Exception {

        TestCreationRequestDto request = new TestCreationRequestDto("test for deleting", TEST_DESCRIPTION, null);
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "testfile.txt",
                "text/plain",
                "test content".getBytes()
        );
        var testDto = testTestSetup.createTest(request, file);

        testTestSetup.deleteTest(testDto.id());
    }

    @Test
    public void editTest_ShouldReturnCreated_WhenValidRequest() throws Exception {

        TestCreationRequestDto request = new TestCreationRequestDto("test name", "some description", null);
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "testfile.txt",
                "text/plain",
                "test content".getBytes()
        );
        TestDto createdTestDto = testTestSetup.createTest(request, file);


        MockMultipartFile editedFile = new MockMultipartFile(
                "file",
                "testfile.txt",
                "text/plain",
                "new test content".getBytes()
        );
        TestEditionRequestDto testEditionRequestDto = new TestEditionRequestDto(createdTestDto.id(), "new name", "new description", null);
        TestDto editedTestDto = testTestSetup.editTest(testEditionRequestDto, editedFile);


        checkTest(editedTestDto, testTestSetup.getTestDto("test/test_edited.json"));
    }

    @Test
    public void editTest_ShouldReturnConflict_WhenDuplicateName() throws Exception {
        final String NAME1 = "name1";
        final String NAME2 = "name2";


        TestCreationRequestDto request1 = new TestCreationRequestDto(NAME1, TEST_DESCRIPTION, null);
        MockMultipartFile file1 = new MockMultipartFile(
                "file",
                "testfile.txt",
                "text/plain",
                "test content".getBytes()
        );
        TestDto createdTestDto1 = testTestSetup.createTest(request1, file1);


        TestCreationRequestDto request2 = new TestCreationRequestDto(NAME2, TEST_DESCRIPTION, null);
        MockMultipartFile file2 = new MockMultipartFile(
                "file",
                "testfile.txt",
                "text/plain",
                "test content".getBytes()
        );
        testTestSetup.createTest(request2, file2);


        TestEditionRequestDto request = new TestEditionRequestDto(createdTestDto1.id(), NAME2, TEST_DESCRIPTION, null);
        MockMultipartFile editedFile = new MockMultipartFile(
                "file",
                "testfile.txt",
                "text/plain",
                "new test content".getBytes()
        );
        testTestSetup.editTestBad(request, editedFile);
    }

    private void checkTest(TestDto actual, TestDto expected) {
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id", "testCategory")
                .isEqualTo(expected);
    }

}