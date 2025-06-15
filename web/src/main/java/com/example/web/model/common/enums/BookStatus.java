package com.example.web.model.common.enums;

public enum BookStatus {
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    DONE("Done"),
    RETURNED("Returned"),
    REJECTED("Rejected");

    private final String status;

    BookStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
