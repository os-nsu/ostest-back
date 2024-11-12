package ru.nsu.ostest.adapter.in.rest.model.annotation;

import ru.nsu.ostest.adapter.in.rest.model.filter.FieldDescriptor;
import ru.nsu.ostest.adapter.in.rest.model.filter.FilterDescriptor;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AnnotationProcessor {
    public static <T> List<FieldDescriptor> getFieldDescriptors(Class<T> clazz) {
        List<FieldDescriptor> descriptors = new ArrayList<>();
        processClassFields(clazz, descriptors);
        return descriptors;
    }

    public static <T> List<FilterDescriptor> getFilterDescriptors(Class<T> clazz) {
        List<FilterDescriptor> filters = new ArrayList<>();
        processClassFilters(clazz, filters);

        return filters;
    }


    public static void processClassFields(Class<?> clazz, List<FieldDescriptor> descriptors) {
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            DescribableField annotation = field.getAnnotation(DescribableField.class);
            if (annotation != null && annotation.includeInDescriptor()) {
                Class<?> fieldType = getFieldType(field);
                if (!fieldType.isPrimitive() && !fieldType.equals(String.class)) {
                    processClassFields(fieldType, descriptors);
                } else {
                    descriptors.add(new FieldDescriptor(fieldType.getSimpleName(), field.getName()));
                }
            }
        }
    }

    public static void processClassFilters(Class<?> clazz, List<FilterDescriptor> filters) {
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            DescribableField annotation = field.getAnnotation(DescribableField.class);
            if (annotation != null && annotation.includeInDescriptor()) {
                String fieldName = field.getName();
                Class<?> fieldType = getFieldType(field);
                if (!fieldType.isPrimitive() && !fieldType.equals(String.class)) {
                    processClassFilters(fieldType, filters);
                } else {
                    filters.add(new FilterDescriptor(fieldType.getSimpleName(), fieldName, false));
                    if (fieldType.equals(String.class)) {
                        filters.add(new FilterDescriptor(fieldType.getSimpleName(), fieldName, true));
                    }
                }
            }
        }
    }

    private static Class<?> getFieldType(Field field) {
        if (field.getGenericType() instanceof ParameterizedType paramType) {
            Type actualType = paramType.getActualTypeArguments()[0];
            if (actualType instanceof Class<?>) {
                return (Class<?>) actualType;
            }
        }
        return field.getType();
    }
}
