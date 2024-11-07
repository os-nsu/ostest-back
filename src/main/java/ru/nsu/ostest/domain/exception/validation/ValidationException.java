package ru.nsu.ostest.domain.exception.validation;

import ru.nsu.ostest.domain.exception.DomainException;

public class ValidationException extends DomainException {
    public ValidationException(String message) {
        super(message);
    }
}
