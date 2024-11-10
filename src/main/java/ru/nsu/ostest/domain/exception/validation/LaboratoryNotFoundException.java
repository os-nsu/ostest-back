package ru.nsu.ostest.domain.exception.validation;

public class LaboratoryNotFoundException extends ValidationException {

    private LaboratoryNotFoundException(String message) {
        super(message);
    }

    public static LaboratoryNotFoundException notFoundLaboratoryWithId(Long id) {
        return new LaboratoryNotFoundException(String.format("Laboratory with id '%s' was not found", id));
    }
}
