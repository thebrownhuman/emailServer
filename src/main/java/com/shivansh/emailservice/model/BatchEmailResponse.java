package com.shivansh.emailservice.model;

import java.util.List;

public class BatchEmailResponse {

    private boolean success;
    private int total;
    private int sent;
    private int failed;
    private List<BatchEmailResult> results;

    public BatchEmailResponse() {
    }

    public BatchEmailResponse(boolean success, int total, int sent, int failed,
            List<BatchEmailResult> results) {
        this.success = success;
        this.total = total;
        this.sent = sent;
        this.failed = failed;
        this.results = results;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getTotal() {
        return total;
    }

    public int getSent() {
        return sent;
    }

    public int getFailed() {
        return failed;
    }

    public List<BatchEmailResult> getResults() {
        return results;
    }
}
