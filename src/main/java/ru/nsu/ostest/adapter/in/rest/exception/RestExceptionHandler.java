package ru.nsu.ostest.adapter.in.rest.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.hibernate.PropertyValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.nsu.ostest.adapter.in.rest.exception.model.Error;
import ru.nsu.ostest.domain.exception.DuplicateLaboratoryNameException;
import ru.nsu.ostest.domain.exception.DuplicateTestNameException;
import ru.nsu.ostest.domain.exception.UserNotFoundException;
import ru.nsu.ostest.domain.exception.validation.ValidationException;
import ru.nsu.ostest.security.exceptions.AuthException;
import ru.nsu.ostest.security.exceptions.NotFoundException;

import java.text.MessageFormat;

@RestControllerAdvice
@CrossOrigin(maxAge = 1440)
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);
    public static final String INVALID_TOKEN_MESSAGE = "Invalid token: {}";
    public static final String AUTH_FAILED_MESSAGE = "Auth failed: {}";

    @ExceptionHandler({EntityNotFoundException.class, ValidationException.class})
    public ResponseEntity<Object> handleException(EntityNotFoundException e) {
        return new ResponseEntity<>(Error.builder().code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({DuplicateTestNameException.class})
    public ResponseEntity<Object> handleException(DuplicateTestNameException e) {
        return new ResponseEntity<>(Error.builder().code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({DuplicateLaboratoryNameException.class})
    public ResponseEntity<Object> handleException(DuplicateLaboratoryNameException e) {
        return new ResponseEntity<>(Error.builder().code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NotFoundException.class, UserNotFoundException.class})
    protected ResponseEntity<Object> handleNotFound(Exception e) {
        return new ResponseEntity<>(Error.builder().code(HttpStatus.NOT_FOUND.value())
                .message(e.getMessage()).build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ClassCastException.class})
    protected ResponseEntity<Object> handleClassCastException(Exception e) {
        return new ResponseEntity<>(Error.builder().code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({PropertyValueException.class})
    protected ResponseEntity<Object> handlePropertyValueException(Exception e) {
        return new ResponseEntity<>(Error.builder().code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({BadRequestException.class})
    protected ResponseEntity<Object> handleBadRequest(Exception e) {
        return new ResponseEntity<>(Error.builder().code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({AuthException.class})
    protected ResponseEntity<Object> handleAuthException(Exception e) {
        String message = e.getMessage();
        logger.error(AUTH_FAILED_MESSAGE, message);
        return new ResponseEntity<>(Error.builder().code(HttpStatus.UNAUTHORIZED.value())
                .message(MessageFormat.format(AUTH_FAILED_MESSAGE, message)).build(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({ExpiredJwtException.class, MalformedJwtException.class, UnsupportedJwtException.class})
    protected ResponseEntity<Object> handleJwtException(RuntimeException e) {
        String message = e.getMessage();
        logger.error(INVALID_TOKEN_MESSAGE, message);
        return new ResponseEntity<>(
                Error.builder().code(HttpStatus.UNAUTHORIZED.value())
                        .message(MessageFormat.format(INVALID_TOKEN_MESSAGE, message)).build(), HttpStatus.UNAUTHORIZED);
    }

}