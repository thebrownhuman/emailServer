package com.shivansh.emailservice.model;

public class HealthResponse {

    private String status;
    private String smtp;
    private String uptime;
    private String version;

    public HealthResponse() {
    }

    public HealthResponse(String status, String smtp, String uptime, String version) {
        this.status = status;
        this.smtp = smtp;
        this.uptime = uptime;
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public String getSmtp() {
        return smtp;
    }

    public String getUptime() {
        return uptime;
    }

    public String getVersion() {
        return version;
    }
}
