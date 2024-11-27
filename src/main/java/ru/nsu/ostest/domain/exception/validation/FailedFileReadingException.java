package ru.nsu.ostest.domain.exception.validation;

public class FailedFileReadingException extends ValidationException {
    public FailedFileReadingException() {
        super("Failed to read file.");
    }
}
