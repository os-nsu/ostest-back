package ru.nsu.ostest.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryDto;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryShortDto;
import ru.nsu.ostest.adapter.out.persistence.entity.laboratory.Laboratory;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface LaboratoryMapper {
    Laboratory laboratoryCreationRequestDtoToLaboratory(LaboratoryCreationRequestDto laboratoryCreationRequestDto);

    LaboratoryDto laboratoryToLaboratoryDto(Laboratory laboratory);

    LaboratoryShortDto laboratoryToLaboratoryShortDto(Laboratory laboratory);
}
