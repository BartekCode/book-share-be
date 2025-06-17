package com.example.core.model.role;

public enum RoleName {
    USER("User"),
    ADMIN("Admin");

    private final String name;

    RoleName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}


