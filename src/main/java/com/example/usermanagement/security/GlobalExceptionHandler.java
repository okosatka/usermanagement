package com.example.usermanagement.security;

import com.example.usermanagement.api.AlreadyExistsException;
import com.example.usermanagement.api.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", e.getMessage());
        body.put("status", HttpStatus.FORBIDDEN);

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    // Handle NotFoundException
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(NotFoundException e, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", e.getMessage());
        body.put("status", HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // Handle AlreadyExistsException
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExistsException(AlreadyExistsException e, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", e.getMessage());
        body.put("status", HttpStatus.CONFLICT);

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    // Handle generic exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception e, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "An unexpected error occurred");
        body.put("details", e.getMessage());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
