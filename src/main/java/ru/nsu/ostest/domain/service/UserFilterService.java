package ru.nsu.ostest.domain.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.nsu.ostest.adapter.in.rest.model.filter.Filter;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;

import java.util.List;
import java.util.Set;

@Service
public class UserFilterService implements FilterService {

    private static final Set<String> ALLOWED_FIELDS = Set.of(
            "username", "firstName", "secondName", "groups", "roles"
    );

    private final FilterStrategyFactory filterStrategyFactory = new FilterStrategyFactory();

    @Override
    public Specification<User> createSpecification(List<Filter> filters) {
        Specification<User> specification = Specification.where(null);

        if (filters != null) {
            for (Filter filter : filters) {
                FilterStrategy strategy = filterStrategyFactory.getStrategy(filter.type());
                if (strategy != null) {
                    if (!ALLOWED_FIELDS.contains(filter.fieldName())) {
                        throw new IllegalArgumentException("Field " + filter.fieldName() + " does not exist in User entity.");
                    }
                    specification = specification.and(strategy.toSpecification(filter));
                } else {
                    throw new IllegalArgumentException("Unsupported filter type: " + filter.type());
                }
            }
        }

        return specification;
    }
}
