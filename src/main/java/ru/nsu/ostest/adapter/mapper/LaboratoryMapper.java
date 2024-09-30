package ru.nsu.ostest.adapter.mapper;

import org.mapstruct.*;
import org.mapstruct.control.DeepClone;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryDto;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryShortDto;
import ru.nsu.ostest.adapter.out.persistence.entity.laboratory.Laboratory;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        mappingControl = DeepClone.class,
        uses = TestMapper.class
)
public interface LaboratoryMapper {
    Laboratory laboratoryCreationRequestDtoToLaboratory(LaboratoryCreationRequestDto laboratoryCreationRequestDto);

    @Mapping(target = "tests", source = "testsLinks")
    LaboratoryDto laboratoryToLaboratoryDto(Laboratory laboratory);

    LaboratoryShortDto laboratoryToLaboratoryShortDto(Laboratory laboratory);
}
