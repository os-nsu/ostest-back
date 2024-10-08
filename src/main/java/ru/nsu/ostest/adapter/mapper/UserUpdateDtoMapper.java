package ru.nsu.ostest.adapter.mapper;

import org.mapstruct.*;
import ru.nsu.ostest.adapter.in.rest.model.user.UserUpdateRequestDto;
import ru.nsu.ostest.adapter.out.persistence.entity.group.Group;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;
import ru.nsu.ostest.domain.service.GroupService;

@Mapper(
        uses = {JsonNullableMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserUpdateDtoMapper {
    public abstract void update(UserUpdateRequestDto updateUser, @MappingTarget User target);

    public void mapGroup(UserUpdateRequestDto updateUser, @MappingTarget User target, GroupService groupService) {
        if (updateUser.getGroupNumber() != null && updateUser.getGroupNumber().isPresent()) {
            String groupNumber = updateUser.getGroupNumber().get();
            Group group = groupService.findGroupByName(groupNumber);
            target.setGroup(group);
        }
    }

}