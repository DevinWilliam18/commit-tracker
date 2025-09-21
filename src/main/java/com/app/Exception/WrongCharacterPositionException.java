package com.app.Exception;

public class WrongCharacterPositionException extends RuntimeException{
    public WrongCharacterPositionException() {
    }

    public WrongCharacterPositionException(String message) {
        super(message);
    }

    public WrongCharacterPositionException(String message, Throwable cause) {
        super(message, cause);
    }
}
