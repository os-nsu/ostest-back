package ru.nsu.ostest.laboratory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryDto;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryEditionRequestDto;
import ru.nsu.ostest.domain.repository.LaboratoryRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import({LaboratoryTestSetup.class})
@AutoConfigureMockMvc(addFilters = false)
public class LaboratoryControllerIntegrationTest {

    private static final String LAB_NAME = "Test Laboratory";
    private static final String LAB_DESCRIPTION = "Test Description";
    private static final boolean IS_HIDDEN = false;
    private static final int SEMESTER_NUMBER = 1;
    private static final LocalDateTime DEADLINE = LocalDateTime.parse("2024-10-07T07:02:27");

    @Autowired
    private LaboratoryTestSetup laboratoryTestSetup;

    @Autowired
    private LaboratoryRepository laboratoryRepository;

    @BeforeEach
    void setUp() {
        laboratoryRepository.deleteAll();
    }

    @Test
    public void createLaboratory_ShouldReturnCreated_WhenValidRequest() throws Exception {
        var laboratoryDto = laboratoryTestSetup.createLaboratory(
                new LaboratoryCreationRequestDto(LAB_NAME, LAB_DESCRIPTION, SEMESTER_NUMBER, DEADLINE, IS_HIDDEN)
        );

        checkLaboratory(laboratoryDto, laboratoryTestSetup.getLaboratoryDto("laboratory/laboratory_created.json"));
    }

    @Test
    public void createLaboratory_ShouldReturnConflict_WhenDuplicateName() throws Exception {
        laboratoryTestSetup.createLaboratory(
                new LaboratoryCreationRequestDto(LAB_NAME, LAB_DESCRIPTION, SEMESTER_NUMBER, DEADLINE, IS_HIDDEN)
        );

        laboratoryTestSetup.createLaboratoryBad(
                new LaboratoryCreationRequestDto(
                        LAB_NAME,
                        "Second Laboratory Description",
                        2,
                        LocalDateTime.now().plusDays(10),
                        true
                ));
    }

    @Test
    public void deleteLaboratory_ShouldReturnStatusOk_WhenLaboratoryExists() throws Exception {

        LaboratoryDto laboratoryDto = laboratoryTestSetup.createLaboratory(
                new LaboratoryCreationRequestDto(LAB_NAME, LAB_DESCRIPTION, SEMESTER_NUMBER, DEADLINE, IS_HIDDEN)
        );

        laboratoryTestSetup.deleteLaboratory(laboratoryDto.id());
    }

    @Test
    public void editLaboratory_ShouldReturnCreated_WhenValidRequest() throws Exception {
        LaboratoryDto createdLaboratoryDto = laboratoryTestSetup.createLaboratory(
                new LaboratoryCreationRequestDto(LAB_NAME, LAB_DESCRIPTION, SEMESTER_NUMBER, DEADLINE, IS_HIDDEN)
        );

        LaboratoryDto editedLaboratoryDto = laboratoryTestSetup.editLaboratory(
                new LaboratoryEditionRequestDto(
                        createdLaboratoryDto.id(),
                        "New Laboratory Name",
                        "New Laboratory Description",
                        createdLaboratoryDto.semesterNumber(),
                        createdLaboratoryDto.deadline(),
                        !IS_HIDDEN));

        checkLaboratory(editedLaboratoryDto,
                laboratoryTestSetup.getLaboratoryDto("laboratory/laboratory_edited.json"));
    }

    @Test
    public void editLaboratory_ShouldReturnConflict_WhenDuplicateName() throws Exception {
        laboratoryTestSetup.createLaboratory(
                new LaboratoryCreationRequestDto(LAB_NAME, LAB_DESCRIPTION, SEMESTER_NUMBER, DEADLINE, IS_HIDDEN)
        );

        LaboratoryDto createdLaboratoryDto = laboratoryTestSetup.createLaboratory(
                new LaboratoryCreationRequestDto("Second Lab Name", LAB_DESCRIPTION, SEMESTER_NUMBER, DEADLINE, IS_HIDDEN)
        );

        laboratoryTestSetup.editLaboratoryBad(
                new LaboratoryEditionRequestDto(
                        createdLaboratoryDto.id(),
                        LAB_NAME,
                        "New Laboratory Description",
                        createdLaboratoryDto.semesterNumber(),
                        createdLaboratoryDto.deadline(),
                        !IS_HIDDEN));
    }

    private void checkLaboratory(LaboratoryDto actual, LaboratoryDto expected) {
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }
}