package ru.nsu.ostest.security.exception;

import ru.nsu.ostest.security.impl.AuthConstants;

public class WrongPasswordException extends AuthorizationException {
    public WrongPasswordException() {
        super(AuthConstants.WRONG_PASSWORD_MESSAGE);
    }
}
