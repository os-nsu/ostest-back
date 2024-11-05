package ru.nsu.ostest.domain.exception.validation;

import java.util.UUID;

public class AttemptNotFoundException extends ValidationException {
    public AttemptNotFoundException(String message) {
        super(message);
    }

    public static AttemptNotFoundException notFoundAttemptWithId(UUID id) {
        return new AttemptNotFoundException(String.format("Attempt with id '%s' was not found", id));
    }
}
