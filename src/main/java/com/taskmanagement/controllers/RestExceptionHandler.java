package com.taskmanagement.controllers;

import com.taskmanagement.exceptions.TaskNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<?> handleTaskNotFound(TaskNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "timestamp", Instant.now(),
                        "status", 404,
                        "error", "Not Found",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of(
                        "timestamp", Instant.now(),
                        "status", 400,
                        "error", "Bad Request",
                        "message", ex.getMessage()
                ));
    }

    // NEW: Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        // Collect all field errors into a single message
        String msg = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity.badRequest()
                .body(Map.of(
                        "timestamp", Instant.now(),
                        "status", 400,
                        "error", "Bad Request",
                        "message", msg
                ));
    }
}
