package ru.nsu.ostest.security.exception;

import ru.nsu.ostest.security.impl.AuthConstants;

public class InvalidJwtException extends AuthorizationException {
    public InvalidJwtException() {
        super(AuthConstants.INVALID_JWT_MESSAGE);
    }
}
