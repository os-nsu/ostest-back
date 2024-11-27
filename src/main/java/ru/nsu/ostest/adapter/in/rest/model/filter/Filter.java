package ru.nsu.ostest.adapter.in.rest.model.filter;

public record Filter(String fieldName, boolean exactSearch, String type,
                     String value) {
}

