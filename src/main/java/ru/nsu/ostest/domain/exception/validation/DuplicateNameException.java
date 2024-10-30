package ru.nsu.ostest.domain.exception.validation;

public class DuplicateNameException extends ValidationException {
    public DuplicateNameException(String message) {
        super(message);
    }
}
