package ru.nsu.ostest.adapter.in.rest.model.filter;

import java.util.List;

public record SearchRequestDto(List<Filter> filters,
                               Pagination pagination) {
    public SearchRequestDto {
        if (pagination == null) {
            pagination = Pagination.getDefaultInstance();
        }
    }
}

