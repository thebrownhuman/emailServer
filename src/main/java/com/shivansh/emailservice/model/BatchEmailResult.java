package com.shivansh.emailservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchEmailResult {

    private String to;
    private boolean success;
    private String messageId;
    private String error;

    public BatchEmailResult() {
    }

    public BatchEmailResult(String to, boolean success, String messageId, String error) {
        this.to = to;
        this.success = success;
        this.messageId = messageId;
        this.error = error;
    }

    public String getTo() {
        return to;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getError() {
        return error;
    }
}
