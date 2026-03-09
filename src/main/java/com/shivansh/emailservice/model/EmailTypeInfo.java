package com.shivansh.emailservice.model;

import java.util.List;

public class EmailTypeInfo {

    private String type;
    private String description;
    private List<String> requiredFields;
    private List<String> optionalFields;

    private EmailTypeInfo() {
    }

    public static EmailTypeInfo from(EmailType emailType) {
        EmailTypeInfo info = new EmailTypeInfo();
        info.type = emailType.name();
        info.description = emailType.getDescription();
        info.requiredFields = emailType.getRequiredFields();
        info.optionalFields = emailType.getOptionalFields();
        return info;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getRequiredFields() {
        return requiredFields;
    }

    public List<String> getOptionalFields() {
        return optionalFields;
    }
}
