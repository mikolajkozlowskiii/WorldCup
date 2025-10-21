package com.sportradar.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public class GameValidationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
    private final String fieldName;

    public GameValidationException(String message, String fieldName) {
        super(message);
        this.fieldName = fieldName;
    }

    public GameValidationException(String message, String fieldName, Throwable cause) {
        super(message, cause);
        this.fieldName = fieldName;
    }

    @Override
    public String toString() {
        return "ValidationException{" +
                "message='" + getMessage() + '\'' +
                ", fieldName='" + fieldName + '\'' +
                '}';
    }
}