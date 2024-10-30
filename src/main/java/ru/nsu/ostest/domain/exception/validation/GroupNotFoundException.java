package ru.nsu.ostest.domain.exception.validation;

public class GroupNotFoundException extends NotFoundException {

    private GroupNotFoundException(String message) {
        super(message);
    }

    public static GroupNotFoundException notFoundGroupWithId(Long id) {
        return new GroupNotFoundException(String.format("Group with id '%s' was not found", id));
    }
}
