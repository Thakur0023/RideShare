package com.ankit.rideshare.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String phone) {
        super("User with Phone " + phone + " already exists");
    }
}
