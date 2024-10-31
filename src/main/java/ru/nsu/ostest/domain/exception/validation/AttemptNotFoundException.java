package ru.nsu.ostest.domain.exception.validation;

public class AttemptNotFoundException extends ValidationException {
    public AttemptNotFoundException(String message) {
        super(message);
    }

    public static AttemptNotFoundException notFoundAttemptWithId(Long id) {
        return new AttemptNotFoundException(String.format("Attempt with id '%s' was not found", id));
    }
}
