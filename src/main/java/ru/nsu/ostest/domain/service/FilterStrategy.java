package ru.nsu.ostest.domain.service;

import org.springframework.data.jpa.domain.Specification;
import ru.nsu.ostest.adapter.in.rest.model.filter.Filter;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;

public interface FilterStrategy {
    Specification<User> toSpecification(Filter filter);
}

