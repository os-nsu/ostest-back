package ru.nsu.ostest.domain.exception.validation;

public class DuplicateGroupNameException extends DuplicateNameException {
    private DuplicateGroupNameException(String message) {
        super(message);
    }

    public static DuplicateGroupNameException of(String name) {
        return new DuplicateGroupNameException(String.format("Group with name '%s' already exists", name));
    }
}
