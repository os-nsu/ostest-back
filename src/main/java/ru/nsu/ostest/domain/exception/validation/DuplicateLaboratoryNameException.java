package ru.nsu.ostest.domain.exception.validation;

public class DuplicateLaboratoryNameException extends DuplicateNameException {
    private DuplicateLaboratoryNameException(String message) {
        super(message);
    }

    public static DuplicateLaboratoryNameException of(String name) {
        return new DuplicateLaboratoryNameException(String.format("Laboratory with name '%s' already exists", name));
    }
}
