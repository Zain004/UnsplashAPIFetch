package com.example.unsplashapi;

import com.example.unsplashapi.ErrorFormat.ErrorInfo;
import com.example.unsplashapi.Response.APIResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private HttpHeaders createSecurityHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Content-Type-Options", "nosniff");
        headers.add("X-Frame-Options", "DENY");
        headers.add("X-XSS-Protection", "1; mode=block");
        return headers;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<APIResponse<List<String>>> handleException(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        logger.warn("Validation failed: {}, Message: {}", ex, ex.getMessage());
        APIResponse<List<String>> response = APIResponse.<List<String>>builder()
                .success(false)
                .message("Validation failed")
                .data(errors)
                .status(HttpStatus.BAD_REQUEST)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .headers(createSecurityHeaders())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<APIResponse<List<String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.toList());
        logger.warn("Validation failed: {}, Message: {}", ex, ex.getMessage());
        APIResponse<List<String>> response = APIResponse.<List<String>>builder()
                .success(false)
                .message("Invalid request data")
                .data(errors)
                .status(HttpStatus.BAD_REQUEST)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .headers(createSecurityHeaders())
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<APIResponse<ErrorInfo>> handleGeneralException(Exception ex) {
        logger.error("An unexpected error occured: {}, Message: {}", ex, ex.getMessage(), ex);
        ErrorInfo errorInfo = new ErrorInfo("Unexpected Error",
                "An unexpected error occurred on the server. Please contact support if the problem persists.");
        APIResponse<ErrorInfo> response = APIResponse.<ErrorInfo>builder()
                .success(false)
                .message("Invalid request data")
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .data(errorInfo)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .headers(createSecurityHeaders())
                .body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<APIResponse<ErrorInfo>> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.warn("Illegal argument: {}, Message: {}", e, e.getMessage());
        ErrorInfo errorInfo = new ErrorInfo("Invalid Argument", e.getMessage());
        APIResponse<ErrorInfo> response = APIResponse.<ErrorInfo>builder()
                .success(false)
                .message("Invalid request data")
                .status(HttpStatus.BAD_REQUEST)
                .data(errorInfo)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .headers(createSecurityHeaders())
                .body(response);
    }

    @ExceptionHandler(DataAccessException.class)
    protected ResponseEntity<APIResponse<ErrorInfo>> handleDataAccessException(DataAccessException ex) {
        logger.error("Error accessing data from the database: {}, Message: {}", ex, ex.getMessage());
        ErrorInfo errorInfo = new ErrorInfo("Database error",
                "An error occurred while accessing the database. Please try again later.");
        APIResponse<ErrorInfo> response = APIResponse.<ErrorInfo>builder()
                .success(false)
                .message("Invalid request data")
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .data(errorInfo)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .headers(createSecurityHeaders())
                .body(response);
    }

    @ExceptionHandler(ResponseStatusException.class)
    protected ResponseEntity<APIResponse<ErrorInfo>> handleResponseStatusException(ResponseStatusException ex) {
        logger.warn("ResponseStatusException: {}, Status: {}, Message: {}", ex, ex.getStatusCode(), ex.getMessage());
        ErrorInfo errorInfo = new ErrorInfo(ex.getStatusCode().toString(), ex.getMessage());
        APIResponse<ErrorInfo> response = APIResponse.<ErrorInfo>builder()
                .success(false)
                .message("Invalid request data")
                .status((HttpStatus) ex.getStatusCode())
                .data(errorInfo)
                .build();
        return ResponseEntity.status(ex.getStatusCode())
                .headers(createSecurityHeaders())
                .body(response);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<APIResponse<ErrorInfo>> handleJsonProcessingException(JsonProcessingException ex) {
        logger.warn("JsonProcessingException: {}, Message: {}", ex, ex.getMessage());
        ErrorInfo errorInfo = new ErrorInfo("400", "Error processing JSON data: " + ex.getMessage());
        APIResponse<ErrorInfo> response = APIResponse.<ErrorInfo>builder()
                .success(false)
                .message("Invalid request data")
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .data(errorInfo)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .headers(createSecurityHeaders())
                .body(response);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<APIResponse<ErrorInfo>> handleWebClientResponseException(WebClientResponseException ex) {
        logger.warn("WebClientResponseException: {}, Status: {}, Message: {}", ex, ex.getStatusCode(), ex.getMessage());
        ErrorInfo errorInfo = new ErrorInfo(ex.getStatusCode().toString(), ex.getMessage());
        APIResponse<ErrorInfo> response = APIResponse.<ErrorInfo>builder()
                .success(false)
                .message("Invalid request data")
                .status((HttpStatus) ex.getStatusCode())
                .data(errorInfo)
                .build();
        return ResponseEntity.status(ex.getStatusCode())
                .headers(createSecurityHeaders())
                .body(response);
    }
}
