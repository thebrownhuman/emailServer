package com.shivansh.emailservice.exception;

public class InvalidEmailTypeException extends RuntimeException {

    public InvalidEmailTypeException(String message) {
        super(message);
    }
}
