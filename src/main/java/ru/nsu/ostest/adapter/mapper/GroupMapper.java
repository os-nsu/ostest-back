package ru.nsu.ostest.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupDto;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupEditionRequestDto;
import ru.nsu.ostest.adapter.out.persistence.entity.group.Group;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import java.util.List;


@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface GroupMapper {
    Group groupCreationRequestDtoToGroup(GroupCreationRequestDto groupCreationRequestDto);

    void groupEditionRequestDtoToGroup(@MappingTarget Group group, GroupEditionRequestDto groupEditionRequestDto);

    GroupDto groupToGroupDto(Group group);

    List<GroupDto> groupsToGroupDtoList(List<Group> groups);

}