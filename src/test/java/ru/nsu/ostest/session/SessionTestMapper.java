package ru.nsu.ostest.session;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.control.DeepClone;
import ru.nsu.ostest.adapter.in.rest.model.session.SessionDto;
import ru.nsu.ostest.adapter.in.rest.model.session.SessionShortDto;
import ru.nsu.ostest.adapter.mapper.UserMapper;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        mappingControl = DeepClone.class,
        uses = {UserMapper.class}
)
public interface SessionTestMapper {

    @Mapping(target = "attemptsNumber", expression = "java(session.attempts().size())")
    @Mapping(source = "session.laboratory.name", target = "laboratoryName")
    SessionShortDto sessionDtoToSessionShortDto(SessionDto session);
}
