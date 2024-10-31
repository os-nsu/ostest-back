package ru.nsu.ostest.domain.exception.validation;

public class SessionNotFoundException extends NotFoundException {

    private SessionNotFoundException(String message) {
        super(message);
    }

    public static SessionNotFoundException notFoundSessionWithId(Long id) {
        return new SessionNotFoundException(String.format("Session with id '%s' was not found", id));
    }
}
