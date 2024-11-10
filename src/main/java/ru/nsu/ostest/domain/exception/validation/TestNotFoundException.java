package ru.nsu.ostest.domain.exception.validation;

public class TestNotFoundException extends ValidationException {

    private TestNotFoundException(String message) {
        super(message);
    }

    public static TestNotFoundException notFoundTestWithId(Long id) {
        return new TestNotFoundException(String.format("Test with id '%s' was not found", id));
    }
}
