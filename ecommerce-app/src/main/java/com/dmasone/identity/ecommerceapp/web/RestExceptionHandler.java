package com.dmasone.identity.ecommerceapp.web;

import com.dmasone.identity.sharedkernel.domain.DomainException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Central translation layer from application/domain failures to HTTP status
 * codes. Domain modules expose stable error codes without depending on web
 * framework types.
 */
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiError> handleDomainException(DomainException exception) {
        return ResponseEntity
                .status(statusFor(exception))
                .body(new ApiError(exception.code(), exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .orElse(null);
        String message = fieldError == null
                ? "Request validation failed"
                : fieldError.getField() + " " + fieldError.getDefaultMessage();
        return ResponseEntity
                .badRequest()
                .body(new ApiError("VALIDATION_ERROR", message));
    }

    @ExceptionHandler({
            ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiError> handleBadRequest(Exception exception) {
        return ResponseEntity
                .badRequest()
                .body(new ApiError("BAD_REQUEST", exception.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableRequestBody(HttpMessageNotReadableException exception) {
        return ResponseEntity
                .badRequest()
                .body(new ApiError("BAD_REQUEST", "Request body is malformed or missing"));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException exception) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(exception.getSupportedMediaTypes());
        return new ResponseEntity<>(
                new ApiError("UNSUPPORTED_MEDIA_TYPE", "Content type is not supported"),
                headers,
                HttpStatus.UNSUPPORTED_MEDIA_TYPE
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotAllowed(HttpRequestMethodNotSupportedException exception) {
        HttpHeaders headers = new HttpHeaders();
        if (exception.getSupportedHttpMethods() != null) {
            headers.setAllow(exception.getSupportedHttpMethods());
        }
        return new ResponseEntity<>(
                new ApiError("METHOD_NOT_ALLOWED", "HTTP method is not supported for this endpoint"),
                headers,
                HttpStatus.METHOD_NOT_ALLOWED
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NoResourceFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiError("NOT_FOUND", exception.getMessage()));
    }

    private HttpStatus statusFor(DomainException exception) {
        return switch (exception.code()) {
            case "PRODUCT_NOT_FOUND", "ORDER_NOT_FOUND", "PAYMENT_NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "INSUFFICIENT_STOCK", "IDEMPOTENCY_KEY_CONFLICT" -> HttpStatus.CONFLICT;
            case "INVALID_ORDER", "INVALID_QUANTITY" -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}
