package ru.nsu.ostest.domain.service;

import ru.nsu.ostest.adapter.in.rest.model.annotation.AnnotationProcessor;
import ru.nsu.ostest.adapter.in.rest.model.filter.FieldDescriptor;
import ru.nsu.ostest.adapter.in.rest.model.filter.FilterDescriptor;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;

import java.util.ArrayList;
import java.util.List;

public class UserMetaProvider implements MetaProvider<User> {
    private final List<FieldDescriptor> fieldDescriptors;
    private final List<FilterDescriptor> filterDescriptors;

    public UserMetaProvider() {
        this.fieldDescriptors = new ArrayList<>();
        this.filterDescriptors = new ArrayList<>();
        AnnotationProcessor.processClassFields(User.class, fieldDescriptors);
        AnnotationProcessor.processClassFilters(User.class, filterDescriptors);
    }

    @Override
    public List<FieldDescriptor> getFieldDescriptors() {
        return fieldDescriptors;
    }

    @Override
    public List<FilterDescriptor> getFilterDescriptors() {
        return filterDescriptors;
    }
}
