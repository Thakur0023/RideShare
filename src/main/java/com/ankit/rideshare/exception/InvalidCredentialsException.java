package com.ankit.rideshare.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Invalid phone or password");
    }
}