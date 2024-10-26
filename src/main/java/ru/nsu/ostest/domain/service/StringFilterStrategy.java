package ru.nsu.ostest.domain.service;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import ru.nsu.ostest.adapter.in.rest.model.filter.Filter;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;

public class StringFilterStrategy implements FilterStrategy {


    @Override
    public Specification<User> toSpecification(Filter filter) {
        return (root, query, criteriaBuilder) -> {
            String fieldName = filter.fieldName();
            String value = filter.value();

            Path<?> path = resolvePath(root, fieldName);

            if (filter.exactSearch()) {
                return criteriaBuilder.equal(path, value);
            } else {
                return criteriaBuilder.like(path.as(String.class), "%" + value + "%");
            }

        };
    }

    private Path<?> resolvePath(Root<User> root, String fieldName) {
        if (fieldName.equals("groups")) {
            return root.join("groups").get("name");
        } else if (fieldName.equals("roles")) {
            return root.join("roles").get("role").get("name");
        } else {
            return root.get(fieldName);
        }
    }
}
