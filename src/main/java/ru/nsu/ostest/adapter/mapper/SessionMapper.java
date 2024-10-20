package ru.nsu.ostest.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.control.DeepClone;
import ru.nsu.ostest.adapter.in.rest.model.session.SessionDto;
import ru.nsu.ostest.adapter.out.persistence.entity.session.Session;

import java.util.List;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        mappingControl = DeepClone.class,
        uses = {LaboratoryMapper.class, UserMapper.class, AttemptMapper.class}
)
public interface SessionMapper {

    List<SessionDto> sessionToSessionDto(List<Session> session);

    SessionDto sessionToSessionDto(Session session);
}
