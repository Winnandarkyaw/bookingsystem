package com.example.bookingsys.exception;

public class ClassFullException extends RuntimeException {

    public ClassFullException(String message) {
        super(message);
    }

    public ClassFullException(String message, Throwable cause) {
        super(message, cause);
    }
}
