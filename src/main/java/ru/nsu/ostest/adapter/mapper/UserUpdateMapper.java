package ru.nsu.ostest.adapter.mapper;

import org.mapstruct.*;
import ru.nsu.ostest.adapter.in.rest.model.user.UserEditionRequestDto;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;

@Mapper(
        uses = {JsonNullableMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserUpdateMapper {
    public abstract void update(UserEditionRequestDto updateUser, @MappingTarget User target);

}