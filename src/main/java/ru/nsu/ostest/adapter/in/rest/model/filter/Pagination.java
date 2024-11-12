package ru.nsu.ostest.adapter.in.rest.model.filter;

import org.springframework.data.domain.Page;

public record Pagination(Integer index, Integer pageSize, Integer totalRecords,
                         Integer totalPages) {
    public Pagination {
        if (index == null) index = 1;
        if (pageSize == null) pageSize = 20;
    }

    public static <T> Pagination createPagination(Page<T> page) {
        return new Pagination(
                page.getNumber() + 1,
                page.getSize(),
                (int) page.getTotalElements(),
                page.getTotalPages()
        );
    }
}