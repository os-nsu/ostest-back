package ru.nsu.ostest.adapter.mapper;

import org.mapstruct.*;
import org.mapstruct.control.DeepClone;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryDto;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryEditionRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryShortDto;
import ru.nsu.ostest.adapter.out.persistence.entity.laboratory.Laboratory;

import java.util.List;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        mappingControl = DeepClone.class,
        uses = TestMapper.class
)
public interface LaboratoryMapper {

    @Mapping(target = "id", ignore = true)
    void updateLaboratoryFromEditionRequestDto(LaboratoryEditionRequestDto dto, @MappingTarget Laboratory laboratory);

    @Mapping(ignore = true, target = "testsLinks")
    Laboratory laboratoryCreationRequestDtoToLaboratory(LaboratoryCreationRequestDto laboratoryCreationRequestDto);

    @Mapping(target = "tests", source = "testsLinks")
    LaboratoryDto laboratoryToLaboratoryDto(Laboratory laboratory);

    LaboratoryShortDto laboratoryDtoToLaboratoryShortDto(LaboratoryDto laboratoryDto);

    List<LaboratoryShortDto> laboratoriesToLaboratoryShortDtoList(List<Laboratory> laboratories);
}
