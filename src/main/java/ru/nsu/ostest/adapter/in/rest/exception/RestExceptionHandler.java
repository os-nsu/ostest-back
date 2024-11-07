package ru.nsu.ostest.adapter.in.rest.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.nsu.ostest.adapter.in.rest.exception.model.Error;

import java.text.MessageFormat;

import ru.nsu.ostest.domain.exception.validation.ValidationException;
import ru.nsu.ostest.security.exception.AuthorizationException;
import ru.nsu.ostest.security.impl.AuthConstants;


@RestControllerAdvice
@CrossOrigin(maxAge = 1440)
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);
    public static final String INVALID_TOKEN_MESSAGE = "Invalid token: {}";
    public static final String AUTH_FAILED_MESSAGE = "Auth failed: {}";

    @ExceptionHandler({ValidationException.class})
    public ResponseEntity<Object> handleBadRequestException(Exception e) {
        logger.error(e.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler({NoRightsException.class})
    protected ResponseEntity<Object> handleAccessDeniedException(Exception e) {
        logger.error(e.getMessage());
        return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler({AuthorizationException.class})
    protected ResponseEntity<Object> handleAuthException(Exception e) {
        logger.error(e.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Auth failed: " + e.getMessage());
    }

    @ExceptionHandler({ExpiredJwtException.class, MalformedJwtException.class, UnsupportedJwtException.class})
    protected ResponseEntity<Object> handleJwtException(RuntimeException e) {
        String message = e.getMessage();
        logger.error(INVALID_TOKEN_MESSAGE, message);
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, INVALID_TOKEN_MESSAGE + message);
    }

    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String message) {
        return new ResponseEntity<>(Error.builder()
                .message(message).build(), status);
    }
}