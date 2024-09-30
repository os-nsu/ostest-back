package ru.nsu.ostest.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.control.DeepClone;
import org.mapstruct.factory.Mappers;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryDto;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryShortDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestDto;
import ru.nsu.ostest.adapter.out.persistence.entity.laboratory.Laboratory;
import ru.nsu.ostest.adapter.out.persistence.entity.test.Test;
import ru.nsu.ostest.adapter.out.persistence.entity.test.TestLaboratoryLink;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        mappingControl = DeepClone.class
)
public interface LaboratoryMapper {
    Laboratory laboratoryCreationRequestDtoToLaboratory(LaboratoryCreationRequestDto laboratoryCreationRequestDto);

    default LaboratoryDto laboratoryToLaboratoryDto(Laboratory laboratory) {
        if (laboratory == null) {
            return null;
        }

        Long id = laboratory.getId();
        String name = laboratory.getName();
        String description = laboratory.getDescription();
        Integer semesterNumber = laboratory.getSemesterNumber();
        LocalDateTime deadline = laboratory.getDeadline();
        Boolean isHidden = laboratory.getIsHidden();

        List<Test> tests = laboratory.getTestsLinks().stream()
                .map(TestLaboratoryLink::getTest)
                .toList();
        List<TestDto> testsDto = tests.stream()
                .map((t) -> Mappers.getMapper(TestMapper.class).toTestDtoFromEntity(t))
                .toList();

        return new LaboratoryDto(id, name, description, semesterNumber, deadline, isHidden, testsDto);
    }

    LaboratoryShortDto laboratoryToLaboratoryShortDto(Laboratory laboratory);
}
