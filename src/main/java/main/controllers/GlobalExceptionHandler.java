package main.controllers;

import main.dtos.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>>handleException(RuntimeException ex) {
        return ResponseEntity.badRequest().body(
                new ApiResponse<>(
                        ex.getMessage(),
                        null
                )
        );
    }
}
