package ru.nsu.ostest.adapter.in.rest.model.filter;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor
public class Pagination {
    private Integer index;
    private Integer pageSize;
    private Integer totalRecords;
    private Integer totalPages;

    public Pagination(Integer index, Integer pageSize, Integer totalRecords, Integer totalPages) {
        this.index = (index == null) ? 1 : index;
        this.pageSize = (pageSize == null) ? 20 : pageSize;
        this.totalRecords = totalRecords;
        this.totalPages = totalPages;
    }

    public static Pagination getDefaultInstance() {
        return new Pagination(1, 20, null, null);
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
