package ru.nsu.ostest.domain.service;

import org.springframework.data.jpa.domain.Specification;
import ru.nsu.ostest.adapter.in.rest.model.filter.Filter;

import java.util.List;

public interface FilterService<T> {
    Specification<T> createSpecification(List<Filter> filters);
}
