package ru.nsu.ostest.domain.exception;

public class DuplicateLaboratoryNameException extends RuntimeException {

    private DuplicateLaboratoryNameException(String message) {
        super(message);
    }

    public static DuplicateLaboratoryNameException of(String name) {
        return new DuplicateLaboratoryNameException(String.format("Laboratory with name '%s' already exists", name));
    }
}
