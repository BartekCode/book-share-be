package com.example.db.exceptions;

public class DbException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private String businessDesc;

    public String getBusinessDesc() {
        return businessDesc;
    }

    public DbException(String message, String businessDesc) {
        super(message);
        this.businessDesc = businessDesc;
    }

    public DbException(String message, String businessDesc, Throwable cause) {
        super(message, cause);
        this.businessDesc = businessDesc;
    }

    public DbException(Throwable cause) {
        super(cause);
    }
}
