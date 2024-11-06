package ru.nsu.ostest.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.nsu.ostest.adapter.in.rest.model.user.RoleDto;
import ru.nsu.ostest.adapter.out.persistence.entity.user.UserRole;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface RoleMapper {
    @Mapping(target = "roleName", source = "role.name")
    RoleDto mapToRole(UserRole userRole);
}
