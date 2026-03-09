package com.shivansh.emailservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailResponse {

    private boolean success;
    private String messageId;
    private String timestamp;
    private String error;
    private String message;
    private Integer retryAfter;

    private EmailResponse() {
    }

    public static EmailResponse success(String messageId) {
        EmailResponse response = new EmailResponse();
        response.success = true;
        response.messageId = messageId;
        response.timestamp = Instant.now().toString();
        return response;
    }

    public static EmailResponse error(String errorCode, String message) {
        EmailResponse response = new EmailResponse();
        response.success = false;
        response.error = errorCode;
        response.message = message;
        response.timestamp = Instant.now().toString();
        return response;
    }

    public static EmailResponse rateLimited(int retryAfterSeconds) {
        EmailResponse response = new EmailResponse();
        response.success = false;
        response.error = "RATE_LIMITED";
        response.message = "Too many requests. Try again in " + retryAfterSeconds + " seconds.";
        response.retryAfter = retryAfterSeconds;
        response.timestamp = Instant.now().toString();
        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public Integer getRetryAfter() {
        return retryAfter;
    }
}
