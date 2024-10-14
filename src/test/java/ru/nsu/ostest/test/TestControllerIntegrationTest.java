package ru.nsu.ostest.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.nsu.ostest.adapter.in.rest.model.test.TestCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestEditionRequestDto;
import ru.nsu.ostest.domain.common.enums.TestCategory;

import static org.assertj.core.api.Assertions.assertThat;


@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, statements = "delete from test")
@Testcontainers
@AutoConfigureMockMvc(addFilters = false)
@Import({TestTestSetup.class})
@SpringBootTest(properties = {
//        "logging.level.sql=trace"
})
public class TestControllerIntegrationTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:latest"
    );

    private static final String TEST_NAME = "Test Test";
    private static final String TEST_DESCRIPTION = "Test Description";

    @Autowired
    private TestTestSetup testTestSetup;


    @Test
    public void createTest_ShouldReturnCreated_WhenValidRequest() throws Exception {
        TestCreationRequestDto request = new TestCreationRequestDto(TEST_NAME, TEST_DESCRIPTION, TestCategory.DEFAULT);
        MockMultipartFile file = createMultipartFile("test content");

        var testDto = testTestSetup.createTest(request, file);

        checkTest(testDto, testTestSetup.getTestDto("test/test_created.json"));
    }

    @Test
    public void getTestScript_ShouldReturnSavedScript_WhenValidRequestAndScriptSaved() throws Exception {
        String content = "test content";

        TestCreationRequestDto request = new TestCreationRequestDto(TEST_NAME, TEST_DESCRIPTION, TestCategory.DEFAULT);
        MockMultipartFile file = createMultipartFile(content);
        var testDto = testTestSetup.createTest(request, file);

        Long id = testDto.id();

        Assertions.assertArrayEquals(content.getBytes(), testTestSetup.getScript(id));
    }

    @Test
    public void createTest_ShouldReturnConflict_WhenDuplicateName() throws Exception {
        TestCreationRequestDto request = new TestCreationRequestDto(TEST_NAME, TEST_DESCRIPTION, TestCategory.DEFAULT);
        MockMultipartFile file = createMultipartFile("test content");

        testTestSetup.createTestBad(request, file);
    }

    @Test
    public void deleteTest_ShouldReturnStatusOk_WhenTestExists() throws Exception {
        TestCreationRequestDto request = new TestCreationRequestDto("test for deleting", TEST_DESCRIPTION, TestCategory.DEFAULT);
        MockMultipartFile file = createMultipartFile("test content");
        var testDto = testTestSetup.createTest(request, file);

        testTestSetup.deleteTest(testDto.id());
    }

    @Test
    public void editTest_ShouldReturnCreated_WhenValidRequest() throws Exception {
        TestCreationRequestDto request = new TestCreationRequestDto("test name", "some description", TestCategory.DEFAULT);
        MockMultipartFile file = createMultipartFile("test content");
        TestDto createdTestDto = testTestSetup.createTest(request, file);


        MockMultipartFile editedFile = createMultipartFile("new test content");
        TestEditionRequestDto testEditionRequestDto = new TestEditionRequestDto(createdTestDto.id(), "new name", "new description", TestCategory.DEFAULT);
        TestDto editedTestDto = testTestSetup.editTest(testEditionRequestDto, editedFile);


        checkTest(editedTestDto, testTestSetup.getTestDto("test/test_edited.json"));
    }

    @Test
    public void editTest_ShouldReturnConflict_WhenDuplicateName() throws Exception {
        final String NAME1 = "name1";
        final String NAME2 = "name2";

        TestCreationRequestDto request1 = new TestCreationRequestDto(NAME1, TEST_DESCRIPTION, TestCategory.DEFAULT);
        MockMultipartFile file1 = createMultipartFile("test content 1");
        TestDto createdTestDto1 = testTestSetup.createTest(request1, file1);

        TestCreationRequestDto request2 = new TestCreationRequestDto(NAME2, TEST_DESCRIPTION, TestCategory.DEFAULT);
        MockMultipartFile file2 = createMultipartFile("test content 2");
        testTestSetup.createTest(request2, file2);

        TestEditionRequestDto request = new TestEditionRequestDto(createdTestDto1.id(), NAME2, TEST_DESCRIPTION, TestCategory.DEFAULT);
        MockMultipartFile editedFile = createMultipartFile("test content edited");
        testTestSetup.editTestBad(request, editedFile);
    }


    private MockMultipartFile createMultipartFile(String content) {
        return new MockMultipartFile(
                "file",
                "testfile.txt",
                "text/plain",
                content.getBytes()
        );
    }

    private void checkTest(TestDto actual, TestDto expected) {
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }
}