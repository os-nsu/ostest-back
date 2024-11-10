package ru.nsu.ostest.security.exception;

public class WrongPasswordException extends AuthorizationException {
    public WrongPasswordException() {
        super("Wrong password.");
    }
}
