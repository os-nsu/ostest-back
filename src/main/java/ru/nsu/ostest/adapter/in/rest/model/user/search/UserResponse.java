package ru.nsu.ostest.adapter.in.rest.model.user.search;

import ru.nsu.ostest.adapter.in.rest.model.filter.FieldDescriptor;
import ru.nsu.ostest.adapter.in.rest.model.filter.FilterDescriptor;
import ru.nsu.ostest.adapter.in.rest.model.filter.Pagination;
import ru.nsu.ostest.adapter.in.rest.model.user.userData.UserDto;

import java.util.List;

public record UserResponse(
        Pagination pagination,
        String tableName,
        List<FieldDescriptor> fieldsDescriptors,
        List<FilterDescriptor> filters,
        List<UserDto> users
) {
}