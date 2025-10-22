package com.sportradar.exception;

import java.io.Serial;

public class GameNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public GameNotFoundException(String message) {
        super(message);
    }
}