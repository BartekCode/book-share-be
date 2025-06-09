package com.example.web.model.book;

public enum BookRequestStatus {
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    DONE("Done"),
    RETURNED("Returned"),
    REJECTED("Rejected");

    private final String status;

    BookRequestStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
