package ru.nsu.ostest.domain.service;

import org.springframework.data.jpa.domain.Specification;
import ru.nsu.ostest.adapter.in.rest.model.filter.Filter;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;

public class IntegerFilterStrategy implements FilterStrategy {
    @Override
    public Specification<User> toSpecification(Filter filter) {
        Integer intValue = Integer.valueOf(filter.value());
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(filter.fieldName()), intValue);
    }
}
