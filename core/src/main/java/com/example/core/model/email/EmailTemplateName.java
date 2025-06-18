package com.example.core.model.email;

public enum EmailTemplateName {
    // sprign bedzie szukac takiego html template w naszym templates
    ACTIVATE_ACCOUNT("activate_account");

    private final String name;

    EmailTemplateName(String name) {
        this.name = name;
    }
}
