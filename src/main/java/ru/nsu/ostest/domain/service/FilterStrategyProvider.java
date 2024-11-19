package ru.nsu.ostest.domain.service;

import java.util.HashMap;
import java.util.Map;

public class FilterStrategyProvider<T> {
    private final Map<String, FilterStrategy<T>> strategies = new HashMap<>();

    public void registerStrategy(String type, FilterStrategy<T> strategy) {
        strategies.put(type, strategy);
    }

    public FilterStrategy<T> getStrategy(String type) {
        return strategies.get(type);
    }
}