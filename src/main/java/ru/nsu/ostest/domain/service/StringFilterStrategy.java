package ru.nsu.ostest.domain.service;

import jakarta.persistence.criteria.Path;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import ru.nsu.ostest.adapter.in.rest.model.filter.Filter;

@AllArgsConstructor
public class StringFilterStrategy<T> implements FilterStrategy<T> {
    private final PathResolver<T> pathResolver;

    @Override
    public Specification<T> toSpecification(Filter filter) {
        return (root, query, criteriaBuilder) -> {
            String value = filter.value();
            Path<String> path = (Path<String>) pathResolver.resolve(root, filter.fieldName());

            if (filter.exactSearch()) {
                return criteriaBuilder.equal(path, value);
            } else {
                return criteriaBuilder.like(path, "%" + value + "%");
            }
        };
    }
}