package ru.nsu.ostest.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.control.DeepClone;
import ru.nsu.ostest.adapter.in.rest.model.session.AttemptDto;
import ru.nsu.ostest.adapter.out.persistence.entity.session.Attempt;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface AttemptMapper {

    AttemptDto attemptToAttemptDto(Attempt attempt);
}
