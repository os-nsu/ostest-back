package ru.nsu.ostest.adapter.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.nsu.ostest.adapter.in.rest.model.user.GroupMemberUserDto;
import ru.nsu.ostest.adapter.in.rest.model.user.UserCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.user.UserDto;
import ru.nsu.ostest.adapter.out.persistence.entity.group.Group;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {GroupMapper.class, RoleMapper.class}
)
public interface UserMapper {
    User userCreationRequestDtoToUser(UserCreationRequestDto userRegistrationDto);

    @Mapping(target = "group", source = "groups", qualifiedByName = "mapToGroup")
    UserDto userToUserDto(User user);

    @Named("mapToGroup")
    default Group mapToGroup(Set<Group> groups) {
        return Optional.ofNullable(groups)
                .stream().flatMap(Collection::stream)
                .findFirst()
                .orElse(null);
    }

    List<GroupMemberUserDto> mapToGroupMembers(Set<User> users);
}