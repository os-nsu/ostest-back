package ru.nsu.ostest.domain.service;

import jakarta.persistence.criteria.Path;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import ru.nsu.ostest.adapter.in.rest.model.filter.Filter;

@AllArgsConstructor
public class IntegerFilterStrategy<T> implements FilterStrategy<T> {
    private final PathResolver<T> pathResolver;

    @Override
    public Specification<T> toSpecification(Filter filter) {
        Integer intValue = Integer.valueOf(filter.value());

        return (root, query, criteriaBuilder) -> {
            Path<Integer> path = (Path<Integer>) pathResolver.resolve(root, filter.fieldName());
            return criteriaBuilder.equal(path, intValue);
        };
    }
}
