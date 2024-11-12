package ru.nsu.ostest.domain.service;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

@FunctionalInterface
public interface PathResolver<T> {
    Path<?> resolve(Root<T> root, String fieldName);
}
