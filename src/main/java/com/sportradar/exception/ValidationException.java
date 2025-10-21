package com.sportradar.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public class ValidationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
    private final ErrorCode errorCode;
    private final String fieldName;

    public ValidationException(String message, ErrorCode errorCode, String fieldName) {
        super(message);
        this.errorCode = errorCode;
        this.fieldName = fieldName;
    }

    public ValidationException(String message, ErrorCode errorCode, String fieldName, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.fieldName = fieldName;
    }

    @Override
    public String toString() {
        return "ValidationException{" +
                "message='" + getMessage() + '\'' +
                ", errorCode=" + errorCode +
                ", fieldName='" + fieldName + '\'' +
                '}';
    }
}