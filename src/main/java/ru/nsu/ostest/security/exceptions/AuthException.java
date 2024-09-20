package ru.nsu.ostest.security.exceptions;

public class AuthException extends RuntimeException {
    public AuthException(String string) {
        super(string);
    }
}