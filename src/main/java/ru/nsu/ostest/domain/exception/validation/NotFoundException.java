package ru.nsu.ostest.domain.exception.validation;

public class NotFoundException extends ValidationException {

    public NotFoundException(String message) {
        super(message);
    }
}
