package ru.nsu.ostest.domain.exception.validation;

public class DuplicateTestNameException extends ValidationException {
    private DuplicateTestNameException(String message) {
        super(message);
    }

    public static DuplicateTestNameException of(String name) {
        return new DuplicateTestNameException(String.format("Test with name '%s' already exists", name));
    }
}
