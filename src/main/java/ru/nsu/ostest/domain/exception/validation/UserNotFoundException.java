package ru.nsu.ostest.domain.exception.validation;

public class UserNotFoundException extends ValidationException {

    private UserNotFoundException(String message) {
        super(message);
    }

    public static UserNotFoundException notFoundUserWithUsername(String userName) {
        return new UserNotFoundException(String.format("User with name '%s' was not found", userName));
    }

    public static UserNotFoundException notFoundUserWithId(Long id) {
        return new UserNotFoundException(String.format("User with id '%s' was not found", id));
    }
}
