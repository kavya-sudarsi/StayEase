package com.stayease.property_service.exception;

public class NoBedsAvailableException extends RuntimeException {
    public NoBedsAvailableException(String message) {
        super(message);
    }
}