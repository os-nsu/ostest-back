package ru.nsu.ostest.adapter.in.rest.model.filter;

import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class Pagination {
    private final Integer index;
    private final Integer pageSize;
    private final Integer totalRecords;
    private final Integer totalPages;

    public Pagination(Integer index, Integer pageSize, Integer totalRecords, Integer totalPages) {
        this.index = (index == null) ? 1 : index;
        this.pageSize = (pageSize == null) ? 20 : pageSize;
        this.totalRecords = totalRecords;
        this.totalPages = totalPages;
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
