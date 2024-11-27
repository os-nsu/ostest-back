package ru.nsu.ostest.domain.exception;

import ru.nsu.ostest.security.exception.AuthorizationException;

public class NoRightsException extends AuthorizationException {
    public NoRightsException() {
        super("No rights.");
    }
}
