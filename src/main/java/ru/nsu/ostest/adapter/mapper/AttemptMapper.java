package ru.nsu.ostest.adapter.mapper;

import org.mapstruct.*;
import ru.nsu.ostest.adapter.in.rest.model.session.AttemptDto;
import ru.nsu.ostest.adapter.in.rest.model.session.AttemptShortDto;
import ru.nsu.ostest.adapter.in.rest.model.session.AvailableTaskResponse;
import ru.nsu.ostest.adapter.in.rest.model.session.MakeAttemptDto;
import ru.nsu.ostest.adapter.out.persistence.entity.session.Attempt;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = TestMapper.class
)
public interface AttemptMapper {

    @Mapping(source = "session.laboratory.number", target = "laboratoryNumber")
    @Mapping(source = "session.laboratory.testsLinks", target = "connectedTests")
    @Mapping(target = "status", expression = "java(AvailabilityStatus.AVAILABLE)")
    AvailableTaskResponse toAvailableTaskResponse(Attempt attempt);

    Attempt makeAttemptDtoToAttempt(MakeAttemptDto makeAttemptDto);

    AttemptDto attemptToAttemptDto(Attempt attempt);

    AttemptShortDto attemptToAttemptShortDto(Attempt attempt);
}
