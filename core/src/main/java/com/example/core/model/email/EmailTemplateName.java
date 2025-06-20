package com.example.core.model.email;

public enum EmailTemplateName {
    // sprign bedzie szukac takiego html template w naszym templates
    ACTIVATE_ACCOUNT("activate_account");

    private final String templateName;

    EmailTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateName() {
        return templateName;
    }
}
