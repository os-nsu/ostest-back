package ru.nsu.ostest.laboratory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryDto;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryEditionRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratorySearchRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestLaboratoryLinkDto;
import ru.nsu.ostest.adapter.mapper.LaboratoryMapper;
import ru.nsu.ostest.domain.common.enums.TestCategory;
import ru.nsu.ostest.domain.repository.LaboratoryRepository;
import ru.nsu.ostest.domain.repository.TestRepository;
import ru.nsu.ostest.security.impl.JwtAuthentication;
import ru.nsu.ostest.test.TestTestSetup;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@AutoConfigureMockMvc(addFilters = false)
@Import({LaboratoryTestSetup.class, TestTestSetup.class})
@SpringBootTest
public class LaboratoryControllerIntegrationTest {

    private static final String LAB_NAME = "Test Laboratory";
    private static final String LAB_DESCRIPTION = "Test Description";
    private static final boolean IS_HIDDEN = false;
    private static final int SEMESTER_NUMBER = 1;
    private static final OffsetDateTime DEADLINE = OffsetDateTime.parse("2024-10-07T07:02:27Z");
    private static final String LABORATORY1_DTO = "laboratory/laboratory1.json";
    private static final String LABORATORY2_DTO = "laboratory/laboratory2.json";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:latest"
    );

    @Autowired
    private LaboratoryTestSetup laboratoryTestSetup;

    @Autowired
    private TestTestSetup testTestSetup;

    @Autowired
    private LaboratoryRepository laboratoryRepository;

    @Autowired
    private LaboratoryMapper laboratoryMapper;

    @Autowired
    private TestRepository testRepository;

    @BeforeEach
    void setUp() {
        laboratoryRepository.deleteAll();
        testRepository.deleteAll();

        String adminAuthority = "ADMIN";
        Authentication authentication = new JwtAuthentication(true, "password", List.of(adminAuthority));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void createLaboratory_ShouldReturnCreated_WhenValidRequest() throws Exception {
        var test1 = createTest(1);
        var test2 = createTest(2);

        var laboratoryDto = laboratoryTestSetup.createLaboratory(
                new LaboratoryCreationRequestDto(LAB_NAME, 1, LAB_DESCRIPTION, SEMESTER_NUMBER, DEADLINE,
                        IS_HIDDEN, List.of(new TestLaboratoryLinkDto(test1.id(), true),
                        new TestLaboratoryLinkDto(test2.id(), false)))
        );

        checkLaboratory(laboratoryDto, laboratoryTestSetup.getLaboratoryDto("laboratory/laboratory_created.json"));
    }

    @Test
    public void createLaboratory_ShouldReturnConflict_WhenDuplicateName() throws Exception {
        laboratoryTestSetup.createLaboratory(
                new LaboratoryCreationRequestDto(LAB_NAME, 1, LAB_DESCRIPTION, SEMESTER_NUMBER, DEADLINE,
                        IS_HIDDEN, List.of())
        );

        laboratoryTestSetup.createLaboratoryBad(
                new LaboratoryCreationRequestDto(
                        LAB_NAME,
                        2,
                        "Second Laboratory Description",
                        2,
                        DEADLINE,
                        true,
                        List.of()
                ));
    }

    @Test
    public void deleteLaboratory_ShouldReturnStatusOk_WhenLaboratoryExists() throws Exception {

        LaboratoryDto laboratoryDto = laboratoryTestSetup.createLaboratory(
                new LaboratoryCreationRequestDto(LAB_NAME, 1, LAB_DESCRIPTION, SEMESTER_NUMBER, DEADLINE,
                        IS_HIDDEN, List.of())
        );

        laboratoryTestSetup.deleteLaboratory(laboratoryDto.id());
    }

    @Test
    public void editLaboratory_ShouldReturnCreated_WhenValidRequest() throws Exception {
        var test1 = createTest(1);
        var test2 = createTest(2);
        var test3 = createTest(3);

        LaboratoryDto createdLaboratoryDto = laboratoryTestSetup.createLaboratory(
                new LaboratoryCreationRequestDto(LAB_NAME, 1, LAB_DESCRIPTION, SEMESTER_NUMBER, DEADLINE,
                        IS_HIDDEN, List.of(new TestLaboratoryLinkDto(test1.id(), true),
                        new TestLaboratoryLinkDto(test2.id(), false)))
        );

        LaboratoryDto editedLaboratoryDto = laboratoryTestSetup.editLaboratory(
                new LaboratoryEditionRequestDto(
                        createdLaboratoryDto.id(),
                        "New Laboratory Name",
                        2,
                        "New Laboratory Description",
                        createdLaboratoryDto.semesterNumber(),
                        createdLaboratoryDto.deadline(),
                        !IS_HIDDEN,
                        List.of(new TestLaboratoryLinkDto(test3.id(), false)),
                        List.of(new TestLaboratoryLinkDto(test1.id(), false)),
                        List.of(new TestLaboratoryLinkDto(test2.id(), false))));

        checkLaboratory(editedLaboratoryDto,
                laboratoryTestSetup.getLaboratoryDto("laboratory/laboratory_edited.json"));
    }

    @Test
    public void editLaboratory_ShouldReturnConflict_WhenDuplicateName() throws Exception {
        laboratoryTestSetup.createLaboratory(
                new LaboratoryCreationRequestDto(LAB_NAME, 1, LAB_DESCRIPTION, SEMESTER_NUMBER, DEADLINE,
                        IS_HIDDEN, List.of())
        );

        LaboratoryDto createdLaboratoryDto = laboratoryTestSetup.createLaboratory(
                new LaboratoryCreationRequestDto("Second Lab Name", 2, LAB_DESCRIPTION, SEMESTER_NUMBER,
                        DEADLINE, IS_HIDDEN, List.of())
        );

        laboratoryTestSetup.editLaboratoryBad(
                new LaboratoryEditionRequestDto(
                        createdLaboratoryDto.id(),
                        LAB_NAME,
                        2,
                        "New Laboratory Description",
                        createdLaboratoryDto.semesterNumber(),
                        createdLaboratoryDto.deadline(),
                        !IS_HIDDEN,
                        List.of(),
                        List.of(),
                        List.of()));
    }

    @Test
    public void getLaboratoryById_ShouldReturnOk_WhenValidRequest() throws Exception {
        var laboratoryDto1 = laboratoryTestSetup.createLaboratory(
                new LaboratoryCreationRequestDto(LAB_NAME + '1', 1, LAB_DESCRIPTION, SEMESTER_NUMBER,
                        DEADLINE, IS_HIDDEN, List.of()));
        var laboratoryDto2 = laboratoryTestSetup.createLaboratory(
                new LaboratoryCreationRequestDto(LAB_NAME + '2', 2, LAB_DESCRIPTION, 2,
                        DEADLINE, true, List.of()));

        checkLaboratory(laboratoryDto1, laboratoryTestSetup.getLaboratoryDto(LABORATORY1_DTO));
        checkLaboratory(laboratoryDto2, laboratoryTestSetup.getLaboratoryDto(LABORATORY2_DTO));

        Long laboratory1Id = laboratoryDto1.id();
        Long laboratory2Id = laboratoryDto2.id();

        laboratoryDto1 = laboratoryTestSetup.getLaboratoryById(laboratory1Id);
        laboratoryDto2 = laboratoryTestSetup.getLaboratoryById(laboratory2Id);

        assertEquals(laboratory1Id, laboratoryDto1.id());
        assertEquals(laboratory2Id, laboratoryDto2.id());

        checkLaboratory(laboratoryDto1, laboratoryTestSetup.getLaboratoryDto(LABORATORY1_DTO));
        checkLaboratory(laboratoryDto2, laboratoryTestSetup.getLaboratoryDto(LABORATORY2_DTO));
    }

    @Test
    public void searchLaboratories_ShouldReturnOk_WhenValidRequest() throws Exception {
        var laboratoryDto1 = laboratoryTestSetup.createLaboratory(
                new LaboratoryCreationRequestDto(LAB_NAME + '1', 1, LAB_DESCRIPTION, SEMESTER_NUMBER,
                        DEADLINE, IS_HIDDEN, List.of()));
        var laboratoryDto2 = laboratoryTestSetup.createLaboratory(
                new LaboratoryCreationRequestDto(LAB_NAME + '2', 2, LAB_DESCRIPTION, 2,
                        DEADLINE, !IS_HIDDEN, List.of()));
        var laboratoryDto3 = laboratoryTestSetup.createLaboratory(
                new LaboratoryCreationRequestDto(LAB_NAME + '3', 3, LAB_DESCRIPTION, SEMESTER_NUMBER,
                        DEADLINE, !IS_HIDDEN, List.of()));
        var laboratoryDto4 = laboratoryTestSetup.createLaboratory(
                new LaboratoryCreationRequestDto(LAB_NAME + '4', 4, LAB_DESCRIPTION, 2,
                        DEADLINE, IS_HIDDEN, List.of()));

        var laboratoryShortDto1 = laboratoryMapper.laboratoryDtoToLaboratoryShortDto(laboratoryDto1);
        var laboratoryShortDto2 = laboratoryMapper.laboratoryDtoToLaboratoryShortDto(laboratoryDto2);
        var laboratoryShortDto3 = laboratoryMapper.laboratoryDtoToLaboratoryShortDto(laboratoryDto3);
        var laboratoryShortDto4 = laboratoryMapper.laboratoryDtoToLaboratoryShortDto(laboratoryDto4);

        checkLaboratory(laboratoryDto1, laboratoryTestSetup.getLaboratoryDto(LABORATORY1_DTO));
        checkLaboratory(laboratoryDto2, laboratoryTestSetup.getLaboratoryDto(LABORATORY2_DTO));
        checkLaboratory(laboratoryDto3, laboratoryTestSetup.getLaboratoryDto("laboratory/laboratory3.json"));
        checkLaboratory(laboratoryDto4, laboratoryTestSetup.getLaboratoryDto("laboratory/laboratory4.json"));

        var searchedLaboratories =
                laboratoryTestSetup.searchLaboratories(new LaboratorySearchRequestDto(false, 1));

        assertEquals(1, searchedLaboratories.size());
        assertTrue(searchedLaboratories.contains(laboratoryShortDto1));

        searchedLaboratories =
                laboratoryTestSetup.searchLaboratories(new LaboratorySearchRequestDto(true, 2));

        assertEquals(1, searchedLaboratories.size());
        assertTrue(searchedLaboratories.contains(laboratoryShortDto2));

        searchedLaboratories =
                laboratoryTestSetup.searchLaboratories(
                        new LaboratorySearchRequestDto(false, null));

        assertEquals(2, searchedLaboratories.size());
        assertTrue(searchedLaboratories.contains(laboratoryShortDto1));
        assertTrue(searchedLaboratories.contains(laboratoryShortDto4));

        searchedLaboratories =
                laboratoryTestSetup.searchLaboratories(
                        new LaboratorySearchRequestDto(null, 1));
        assertEquals(2, searchedLaboratories.size());
        assertTrue(searchedLaboratories.contains(laboratoryShortDto1));
        assertTrue(searchedLaboratories.contains(laboratoryShortDto3));
    }

    private void checkLaboratory(LaboratoryDto actual, LaboratoryDto expected) {
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .ignoringFieldsMatchingRegexes(".*\\.id")
                .isEqualTo(expected);
    }

    private TestDto createTest(int orderNumber) throws Exception {
        TestCreationRequestDto testCreationRequestDto =
                new TestCreationRequestDto("Test Test" + orderNumber, "Test Description",
                        "Test Code" + orderNumber, TestCategory.DEFAULT);
        return testTestSetup.createTest(testCreationRequestDto, createMultipartFile());
    }

    private MockMultipartFile createMultipartFile() {
        return new MockMultipartFile(
                "file",
                "testfile.txt",
                "text/plain",
                "test content".getBytes()
        );
    }
}