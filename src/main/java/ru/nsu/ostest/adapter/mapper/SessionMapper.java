package ru.nsu.ostest.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.control.DeepClone;
import ru.nsu.ostest.adapter.in.rest.model.session.SessionDto;
import ru.nsu.ostest.adapter.in.rest.model.session.SessionShortDto;
import ru.nsu.ostest.adapter.out.persistence.entity.session.Session;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        mappingControl = DeepClone.class,
        uses = {LaboratoryMapper.class, UserMapper.class, AttemptMapper.class}
)
public interface SessionMapper {

    SessionDto sessionToSessionDto(Session session);

    @Mapping(target = "attemptsNumber", expression = "java(session.getAttempts().size())")
    @Mapping(source = "session.laboratory.name", target = "laboratoryName")
    SessionShortDto sessionToSessionShortDto(Session session);
}
