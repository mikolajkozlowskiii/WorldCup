package com.sportradar.exception;

import java.io.Serial;

public class GameAlreadyExistsException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public GameAlreadyExistsException(String message) {
        super(message);
    }
}