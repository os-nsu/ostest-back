package ru.nsu.ostest.adapter.in.rest.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.nsu.ostest.adapter.in.rest.controller.LaboratoryController;
import ru.nsu.ostest.adapter.in.rest.model.ErrorDto;
import ru.nsu.ostest.domain.exception.DuplicateLaboratoryNameException;

@RestControllerAdvice(basePackageClasses = LaboratoryController.class)
public class LaboratoryControllerAdvice {

    @ExceptionHandler(DuplicateLaboratoryNameException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleException(DuplicateLaboratoryNameException e) {
        return new ErrorDto(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleException(Exception e) {
        return new ErrorDto("Internal server error.");
    }
}
