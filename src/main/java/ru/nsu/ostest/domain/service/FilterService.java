package ru.nsu.ostest.domain.service;

import org.springframework.data.jpa.domain.Specification;
import ru.nsu.ostest.adapter.in.rest.model.filter.Filter;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;

import java.util.List;

public interface FilterService {
    Specification<User> createSpecification(List<Filter> filters);
}
