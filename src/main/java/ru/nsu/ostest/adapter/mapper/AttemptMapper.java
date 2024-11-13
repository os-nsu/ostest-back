package ru.nsu.ostest.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.nsu.ostest.adapter.in.rest.model.session.AttemptDto;
import ru.nsu.ostest.adapter.in.rest.model.session.AttemptShortDto;
import ru.nsu.ostest.adapter.in.rest.model.session.AvailableTaskResponse;
import ru.nsu.ostest.adapter.in.rest.model.session.MakeAttemptDto;
import ru.nsu.ostest.adapter.in.rest.model.test.AttemptResultSetRequest;
import ru.nsu.ostest.adapter.out.persistence.entity.session.Attempt;
import ru.nsu.ostest.adapter.out.persistence.entity.session.AttemptResults;

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

    @Mapping(target = "duration", source = "request.duration")
    @Mapping(target = "errorDetails", source = "request.errorDetails")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "attempt", source = "attempt")
    @Mapping(target = "testResultsJson", source = "request.testResults")
    AttemptResults attemptResultSetRequestToAttemptResults(AttemptResultSetRequest request, Attempt attempt);


}
