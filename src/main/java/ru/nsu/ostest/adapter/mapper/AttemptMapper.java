package ru.nsu.ostest.adapter.mapper;

import org.mapstruct.*;
import ru.nsu.ostest.adapter.in.rest.model.session.AttemptDto;
import ru.nsu.ostest.adapter.in.rest.model.session.AttemptShortDto;
import ru.nsu.ostest.adapter.in.rest.model.session.AvailableTaskResponse;
import ru.nsu.ostest.adapter.in.rest.model.session.MakeAttemptDto;
import ru.nsu.ostest.adapter.in.rest.model.test.AttemptResultSetRequest;
import ru.nsu.ostest.adapter.out.persistence.entity.session.Attempt;
import ru.nsu.ostest.adapter.out.persistence.entity.session.AttemptResults;
import ru.nsu.ostest.adapter.out.persistence.entity.test.TestLaboratoryLink;

import java.util.List;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = TestMapper.class
)
public interface AttemptMapper {

    @Mapping(source = "session.laboratory.number", target = "laboratoryNumber")
    @Mapping(target = "connectedTests",
            expression = "java(filterAndMapTests(attempt.getSession().getLaboratory().getTestsLinks()))")
    @Mapping(target = "status", expression = "java(AvailabilityStatus.AVAILABLE)")
    AvailableTaskResponse toAvailableTaskResponse(Attempt attempt);

    @Named("filterTests")
    default List<String> filterAndMapTests(List<TestLaboratoryLink> testsLinks) {
        return testsLinks.stream()
                .filter(TestLaboratoryLink::getIsSwitchedOn)
                .map(it -> it.getTest().getCode())
                .toList();
    }

    Attempt makeAttemptDtoToAttempt(MakeAttemptDto makeAttemptDto);

    @Mapping(target = "attemptResult", source = "attempt.attemptResults")
    AttemptDto attemptToAttemptDto(Attempt attempt);

    AttemptShortDto attemptToAttemptShortDto(Attempt attempt);

    @Mapping(target = "duration", source = "request.duration")
    @Mapping(target = "errorDetails", source = "request.errorDetails")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "attempt", source = "attempt")
    @Mapping(target = "testResults", source = "request.testResults")
    AttemptResults attemptResultSetRequestToAttemptResults(AttemptResultSetRequest request, Attempt attempt);
}
