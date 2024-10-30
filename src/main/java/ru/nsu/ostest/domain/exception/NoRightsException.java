package ru.nsu.ostest.domain.exception;

public class NoRightsException extends RuntimeException {
    public NoRightsException() {
        super("No rights.");
    }
}
