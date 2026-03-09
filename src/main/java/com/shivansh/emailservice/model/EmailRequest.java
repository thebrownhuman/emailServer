package com.shivansh.emailservice.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public class EmailRequest {

    @NotBlank(message = "Recipient email is required")
    @Email(message = "Invalid email format")
    private String to;

    @NotNull(message = "Email type is required")
    private EmailType type;

    @NotBlank(message = "Caller app identifier is required")
    private String callerApp;

    private String subject;

    @NotNull(message = "Template data is required")
    private Map<String, Object> templateData;

    public EmailRequest() {
    }

    public EmailRequest(String to, EmailType type, String callerApp,
            String subject, Map<String, Object> templateData) {
        this.to = to;
        this.type = type;
        this.callerApp = callerApp;
        this.subject = subject;
        this.templateData = templateData;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public EmailType getType() {
        return type;
    }

    public void setType(EmailType type) {
        this.type = type;
    }

    public String getCallerApp() {
        return callerApp;
    }

    public void setCallerApp(String callerApp) {
        this.callerApp = callerApp;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Map<String, Object> getTemplateData() {
        return templateData;
    }

    public void setTemplateData(Map<String, Object> templateData) {
        this.templateData = templateData;
    }
}
