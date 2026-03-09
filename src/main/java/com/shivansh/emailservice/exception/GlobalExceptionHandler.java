package com.shivansh.emailservice.exception;

import com.shivansh.emailservice.model.EmailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<EmailResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest()
                .body(EmailResponse.error("VALIDATION_ERROR", message));
    }

    @ExceptionHandler(InvalidEmailTypeException.class)
    public ResponseEntity<EmailResponse> handleInvalidType(InvalidEmailTypeException ex) {
        return ResponseEntity.badRequest()
                .body(EmailResponse.error("INVALID_EMAIL_TYPE", ex.getMessage()));
    }

    @ExceptionHandler(InvalidCallerException.class)
    public ResponseEntity<EmailResponse> handleInvalidCaller(InvalidCallerException ex) {
        return ResponseEntity.status(401)
                .body(EmailResponse.error("INVALID_CALLER", ex.getMessage()));
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<EmailResponse> handleRateLimit(RateLimitExceededException ex) {
        return ResponseEntity.status(429)
                .body(EmailResponse.rateLimited((int) ex.getRetryAfterSeconds()));
    }

    @ExceptionHandler(MailException.class)
    public ResponseEntity<EmailResponse> handleMailError(MailException ex) {
        log.error("SMTP error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(500)
                .body(EmailResponse.error("SMTP_ERROR",
                        "Failed to send email. Will retry automatically."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<EmailResponse> handleGeneral(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(500)
                .body(EmailResponse.error("INTERNAL_ERROR", ex.getMessage()));
    }
}
