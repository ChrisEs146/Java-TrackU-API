package com.tracku.chris.tracku.Utils.ExceptionHandlers;
import com.tracku.chris.tracku.Utils.CustomExceptions.UserAlreadyExistsException;
import com.tracku.chris.tracku.Utils.CustomExceptions.UserNotFoundException;
import com.tracku.chris.tracku.Utils.CustomExceptions.UserUnauthorizedException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class UserExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = {UserAlreadyExistsException.class})
    protected ResponseEntity<Object> handleExistingUser(RuntimeException ex, WebRequest request) {
        Map<String, Object> errorObject = createErrorObject(HttpStatus.CONFLICT, HttpStatus.CONFLICT.value(), ex.getMessage());
        return handleExceptionInternal(ex, errorObject, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(value={UserNotFoundException.class})
    protected ResponseEntity<Object> handleUserNotFound(EntityNotFoundException ex, WebRequest request) {
        Map<String, Object> errorObject = createErrorObject(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return handleExceptionInternal(ex, errorObject, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value={UserUnauthorizedException.class})
    protected ResponseEntity<Object> handleUserUnauthorized(RuntimeException ex, WebRequest request) {
        Map<String, Object> errorObject = createErrorObject(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
        return handleExceptionInternal(ex, errorObject, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(value = {AuthenticationException.class})
    protected ResponseEntity<Object> handleAuthenticationError(AuthenticationException ex) {
        Map<String, Object> errorObject = createErrorObject(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorObject);
    }

    private Map<String, Object> createErrorObject(HttpStatus status, int statusCode, String errorMsg) {
        Map<String, Object> errorObject = new HashMap<>();
        errorObject.put("Timestamp", LocalDateTime.now());
        errorObject.put("Status", status);
        errorObject.put("StatusCode", statusCode);
        errorObject.put("Message", errorMsg);
        return errorObject;
    }
}
