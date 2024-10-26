package ru.nsu.ostest.adapter.in.rest.model.filter;

public record Pagination(Integer index, Integer pageSize, Integer totalRecords,
                         Integer totalPages) {
    public Pagination {
        if (index == null) index = 1;
        if (pageSize == null) pageSize = 20;
    }
}