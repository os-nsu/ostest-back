package ru.nsu.ostest.domain.exception.validation;

public class JsonSerializationException extends ValidationException {

    public JsonSerializationException() {
        super("Failed to serialize test results");

    }
}
