package ru.nsu.ostest.adapter.in.rest.model.group;

import ru.nsu.ostest.adapter.in.rest.model.filter.FieldDescriptor;
import ru.nsu.ostest.adapter.in.rest.model.filter.FilterDescriptor;
import ru.nsu.ostest.adapter.in.rest.model.filter.Pagination;

import java.util.List;

public record GroupResponse(
        Pagination pagination,
        String tableName,
        List<FieldDescriptor> fieldsDescriptors,
        List<FilterDescriptor> filters,
        List<GroupDto> groups
) {
}