package ru.nsu.ostest.adapter.in.rest.model.user.search;

import ru.nsu.ostest.adapter.in.rest.model.filter.Filter;
import ru.nsu.ostest.adapter.in.rest.model.filter.Pagination;

import java.util.List;

public record UserSearchRequestDto(List<Filter> filters,
                                   Pagination pagination) {
    public UserSearchRequestDto {
        if (pagination == null) {
            pagination = new Pagination(1, 20, null, null);
        }
    }
}
