package ru.nsu.ostest.domain.service;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.nsu.ostest.adapter.in.rest.model.filter.Filter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FilterServiceImpl<T> implements FilterService<T> {
    private final FilterStrategyFactory<T> filterStrategyFactory;

    @Override
    public Specification<T> createSpecification(List<Filter> filters) {
        return Optional.ofNullable(filters)
                .orElse(Collections.emptyList())
                .stream()
                .map(filter -> filterStrategyFactory.getStrategy(filter.type()).toSpecification(filter))
                .reduce(Specification::and)
                .orElse(null);
    }
}

