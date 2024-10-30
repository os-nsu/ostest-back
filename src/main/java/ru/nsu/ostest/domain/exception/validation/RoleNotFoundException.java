package ru.nsu.ostest.domain.exception.validation;

public class RoleNotFoundException extends NotFoundException {

    private RoleNotFoundException(String message) {
        super(message);
    }

    public static RoleNotFoundException notFoundRoleWithName(String name) {
        return new RoleNotFoundException(String.format("Role with name '%s' was not found", name));
    }
}
