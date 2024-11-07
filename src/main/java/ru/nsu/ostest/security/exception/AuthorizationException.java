package ru.nsu.ostest.security.exception;

import ru.nsu.ostest.domain.exception.DomainException;

public class AuthorizationException extends DomainException {
    public AuthorizationException(String message) {
        super(message);
    }
}
