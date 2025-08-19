package com.rbouaro.aimentor.exceptions;

import com.rbouaro.aimentor.dto.global.AppErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;
import java.util.Map;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    public static final String VALIDATION_FAILED = "Validation failed";

    // 401 - Unauthorized (custom)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<AppErrorResponse<Void>> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), "UNAUTHORIZED", null);
    }

    // 403 - Forbidden (custom)
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<AppErrorResponse<Void>> handleForbidden(ForbiddenException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), "FORBIDDEN", null);
    }

    // 404 - Not Found (custom)
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<AppErrorResponse<Void>> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), "NOT_FOUND", null);
    }

    // 409 - Conflict (custom)
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<AppErrorResponse<Void>> handleConflict(ConflictException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), "CONFLICT", null);
    }

    // 429 - Too Many Requests / Limit Exceeded (custom)
    @ExceptionHandler(LimitExceededException.class)
    public ResponseEntity<AppErrorResponse<Void>> handleLimitExceeded(LimitExceededException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage(), "LIMIT_EXCEEDED", null);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<AppErrorResponse<String>> handleDuplicateKey(DuplicateKeyException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "Resource already exists", "CONFLICT",
                ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage());
    }


    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<AppErrorResponse<Void>> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), "BAD_REQUEST", null);
    }


    // 400 - Body validation errors (@Valid on @RequestBody)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AppErrorResponse<List<Map<String, String>>>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<Map<String, String>> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> Map.of(
                        "field", fe.getField(),
                        "message", String.valueOf(fe.getDefaultMessage())))
                .toList();

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                VALIDATION_FAILED,
                "VALIDATION_ERROR",
                details
        );
    }

    // 400 - Binding/validation errors (e.g., form-data, query params)
    @ExceptionHandler(BindException.class)
    public ResponseEntity<AppErrorResponse<List<Map<String, String>>>> handleBindException(
            BindException ex, HttpServletRequest request) {

        List<Map<String, String>> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> Map.of(
                        "field", fe.getField(),
                        "message", String.valueOf(fe.getDefaultMessage())))
                .toList();

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                VALIDATION_FAILED,
                "VALIDATION_ERROR",
                details
        );
    }

    // 400 - Constraint violations (@Validated on parameters, path variables)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<AppErrorResponse<List<Map<String, String>>>> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {

        List<Map<String, String>> details = ex.getConstraintViolations()
                .stream()
                .map(v -> Map.of(
                        "property", v.getPropertyPath().toString(),
                        "message", v.getMessage()))
                .toList();

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                VALIDATION_FAILED,
                "VALIDATION_ERROR",
                details
        );
    }

    // 400 - Malformed JSON request body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<AppErrorResponse<Void>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Malformed request body: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Malformed request body", "MALFORMED_REQUEST", null);
    }

    // 400 - Missing request parameter
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<AppErrorResponse<Map<String, String>>> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Missing required parameter",
                "MISSING_PARAMETER",
                Map.of("parameter", ex.getParameterName())
        );
    }

    // 400 - Argument type mismatch
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<AppErrorResponse<Map<String, String>>> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid parameter value",
                "TYPE_MISMATCH",
                Map.of("name", ex.getName(), "value", ex.getValue() != null ? ex.getValue().toString() : "null")
        );
    }

    // 401 - Authentication failures
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<AppErrorResponse<String>> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Authentication failed", "AUTHENTICATION_FAILED", ex.getMessage());
    }

    // 403 - Access denied
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<AppErrorResponse<Void>> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "Access is denied", "ACCESS_DENIED", null);
    }

    // 404 - No handler found
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<AppErrorResponse<Void>> handleNoHandlerFound(
            NoHandlerFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Resource not found", "NOT_FOUND", null);
    }

    // 405 - Method not supported
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<AppErrorResponse<Map<String, Object>>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return buildResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Method not allowed",
                "METHOD_NOT_ALLOWED",
                Map.of(
                        "method", ex.getMethod(),
                        "supported", ex.getSupportedHttpMethods() != null ? ex.getSupportedHttpMethods().toString() : "[]"
                )
        );
    }

    // 500 - Runtime exceptions (keep message format used by callers)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<AppErrorResponse<Void>> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {
        log.error("Unhandled runtime exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        String msg = "An error occurred while processing your message: " + ex.getMessage();
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, msg, "INTERNAL_SERVER_ERROR", null);
    }

    // 500 - Fallback for any other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<AppErrorResponse<Void>> handleException(
            Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", "INTERNAL_SERVER_ERROR", null);
    }

    private <T> ResponseEntity<AppErrorResponse<T>> buildResponse(HttpStatus status, String message, String code, T details) {
        return ResponseEntity.status(status).body(new AppErrorResponse<>(message, code, details));
    }
}