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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.nsu.ostest.adapter.in.rest.exception.model.Error;
import ru.nsu.ostest.domain.exception.DuplicateLaboratoryNameException;
import ru.nsu.ostest.security.exceptions.AuthException;
import ru.nsu.ostest.security.exceptions.NotFoundException;
import ru.nsu.ostest.security.impl.AuthConstants;

@RestControllerAdvice
@CrossOrigin(maxAge = 1440)
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<Object> handleException(EntityNotFoundException e) {
        return new ResponseEntity<>(Error.builder().code(404).message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({DuplicateLaboratoryNameException.class})
    public ResponseEntity<Object> handleException(DuplicateLaboratoryNameException e) {
        return new ResponseEntity<>(Error.builder().code(404).message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NotFoundException.class})
    protected ResponseEntity<Object> handleNotFound(Exception e) {
        return new ResponseEntity<>(Error.builder().code(404).message(e.getMessage()).build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ClassCastException.class})
    protected ResponseEntity<Object> handleClassCastException(Exception e) {
        return new ResponseEntity<>(Error.builder().code(404).message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({PropertyValueException.class})
    protected ResponseEntity<Object> handlePropertyValueException(Exception e) {
        return new ResponseEntity<>(Error.builder().code(404).message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({BadRequestException.class})
    protected ResponseEntity<Object> handleBadRequest(Exception e) {
        return new ResponseEntity<>(Error.builder().code(400).message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({AuthException.class})
    protected ResponseEntity<Object> handleAuthException(Exception e) {
        String message = e.getMessage();
        return new ResponseEntity<>(Error.builder().code(401).message("Auth failed: " + message).build(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({ExpiredJwtException.class, MalformedJwtException.class, UnsupportedJwtException.class})
    protected ResponseEntity<Object> handleJwtException(RuntimeException e) {
        String message = e.getMessage();
        logger.error(AuthConstants.INVALID_TOKEN_MESSAGE + "{}", message, e);
        return new ResponseEntity<>(
                Error.builder().code(400).message(AuthConstants.INVALID_TOKEN_MESSAGE + message).build(),
                HttpStatus.UNAUTHORIZED);
    }

}