package ru.nsu.ostest.domain.exception.validation;

public class DuplicateUserNameException extends ValidationException {
    private DuplicateUserNameException(String message) {
        super(message);
    }

    public static DuplicateUserNameException of(String username) {
        return new DuplicateUserNameException(String.format("Login '%s' is already used", username));
    }
}
