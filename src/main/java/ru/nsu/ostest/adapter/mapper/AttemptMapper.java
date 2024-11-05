package ru.nsu.ostest.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.nsu.ostest.adapter.in.rest.model.session.AttemptDto;
import ru.nsu.ostest.adapter.in.rest.model.session.AttemptShortDto;
import ru.nsu.ostest.adapter.out.persistence.entity.session.Attempt;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface AttemptMapper {

    AttemptDto attemptToAttemptDto(Attempt attempt);

    AttemptShortDto attemptToAttemptShortDto(Attempt attempt);
}
