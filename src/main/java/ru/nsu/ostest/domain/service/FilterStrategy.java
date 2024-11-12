package ru.nsu.ostest.domain.service;

import org.springframework.data.jpa.domain.Specification;
import ru.nsu.ostest.adapter.in.rest.model.filter.Filter;

public interface FilterStrategy<T> {
    Specification<T> toSpecification(Filter filter);
}
