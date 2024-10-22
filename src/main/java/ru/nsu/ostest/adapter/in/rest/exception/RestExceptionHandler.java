package ru.nsu.ostest.adapter.in.rest.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.hibernate.PropertyValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.nsu.ostest.adapter.in.rest.exception.model.Error;
import ru.nsu.ostest.security.impl.AuthConstants;


@RestControllerAdvice
@CrossOrigin(maxAge = 1440)
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler({EntityNotFoundException.class,
            DuplicateKeyException.class,
            ClassCastException.class,
            PropertyValueException.class,
            BadRequestException.class})
    public ResponseEntity<Object> handleBadRequestException(Exception e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler({AccessDeniedException.class})
    protected ResponseEntity<Object> handleAccessDeniedException(Exception e) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler({JwtException.class, BadCredentialsException.class})
    protected ResponseEntity<Object> handleAuthException(Exception e) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Auth failed: " + e.getMessage());
    }

    @ExceptionHandler({ExpiredJwtException.class, MalformedJwtException.class, UnsupportedJwtException.class})
    protected ResponseEntity<Object> handleJwtException(RuntimeException e) {
        String message = e.getMessage();
        logger.error(AuthConstants.INVALID_TOKEN_MESSAGE + "{}", message, e);
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, AuthConstants.INVALID_TOKEN_MESSAGE + message);
    }

    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String message) {
        return new ResponseEntity<>(Error.builder()
                .code(status.value())
                .message(message).build(), status);
    }
}