package com.ndc.be.util.error;

public class InternalServerException extends RuntimeException {
    public InternalServerException(String message) {
        super(message);
    }
} 