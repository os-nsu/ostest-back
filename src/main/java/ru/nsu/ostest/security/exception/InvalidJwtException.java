package ru.nsu.ostest.security.exception;

public class InvalidJwtException extends AuthorizationException {
    public InvalidJwtException() {
        super("Invalid JWT");
    }
}
