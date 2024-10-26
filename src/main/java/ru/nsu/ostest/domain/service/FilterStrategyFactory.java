package ru.nsu.ostest.domain.service;

import java.util.HashMap;
import java.util.Map;

public class FilterStrategyFactory {
    private final Map<String, FilterStrategy> strategies = new HashMap<>();

    public FilterStrategyFactory() {
        strategies.put("string", new StringFilterStrategy());
        strategies.put("integer", new IntegerFilterStrategy());
    }

    public FilterStrategy getStrategy(String type) {
        return strategies.get(type);
    }
}