package ru.nsu.ostest.domain.service;


import ru.nsu.ostest.adapter.in.rest.model.annotation.AnnotationProcessor;
import ru.nsu.ostest.adapter.in.rest.model.filter.FieldDescriptor;
import ru.nsu.ostest.adapter.in.rest.model.filter.FilterDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MetaProvider {
    private static final Map<Class<?>, List<FieldDescriptor>> fieldDescriptorsCache = new ConcurrentHashMap<>();
    private static final Map<Class<?>, List<FilterDescriptor>> filterDescriptorsCache = new ConcurrentHashMap<>();

    public static <T> List<FieldDescriptor> getFieldDescriptors(Class<T> clazz) {
        return fieldDescriptorsCache.computeIfAbsent(clazz, key -> {
            List<FieldDescriptor> descriptors = new ArrayList<>();
            AnnotationProcessor.processClassFields(key, descriptors);
            return descriptors;
        });
    }

    public static <T> List<FilterDescriptor> getFilterDescriptors(Class<T> clazz) {
        return filterDescriptorsCache.computeIfAbsent(clazz, key -> {
            List<FilterDescriptor> filters = new ArrayList<>();
            AnnotationProcessor.processClassFilters(key, filters);
            return filters;
        });
    }
}