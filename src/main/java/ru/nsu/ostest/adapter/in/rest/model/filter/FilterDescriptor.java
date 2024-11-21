package ru.nsu.ostest.adapter.in.rest.model.filter;

public record FilterDescriptor(String type, String fieldName,
                               boolean exactSearch) {
}
