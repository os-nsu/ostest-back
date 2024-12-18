package ru.nsu.ostest.domain.exception.validation;

public class InvalidOldPasswordException extends ValidationException {
    public InvalidOldPasswordException() {
        super("The old password provided is incorrect.");
    }
}