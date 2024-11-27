package ru.nsu.ostest.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupDto;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupEditionRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupFullDto;
import ru.nsu.ostest.adapter.out.persistence.entity.group.Group;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;


@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE, uses = RoleMapper.class)
public interface GroupMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    Group groupCreationRequestDtoToGroup(GroupCreationRequestDto groupCreationRequestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "groupName", source = "groupEditionRequestDto.name")
    void groupEditionRequestDtoToGroup(@MappingTarget Group group, GroupEditionRequestDto groupEditionRequestDto);

    GroupDto groupToGroupDto(Group group);

    @Mapping(target = "users", source = "group.users")
    @Mapping(target = "name", source = "group.groupName")
    GroupFullDto mapToGroupFullDto(Group group);
}