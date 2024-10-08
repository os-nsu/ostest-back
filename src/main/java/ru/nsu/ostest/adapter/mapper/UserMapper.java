package ru.nsu.ostest.adapter.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.nsu.ostest.adapter.in.rest.model.user.UserCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.user.UserDto;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface UserMapper {
    User userCreationRequestDtoToUser(UserCreationRequestDto userRegistrationDto);

    @Mapping(target = "groupName", source = "group.name")
    UserDto userToUserResponseDto(User user);

}