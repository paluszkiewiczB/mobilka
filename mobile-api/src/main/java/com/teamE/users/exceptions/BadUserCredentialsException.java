package com.teamE.users.exceptions;

public class BadUserCredentialsException extends UserException {
    public BadUserCredentialsException() {
    }

    public BadUserCredentialsException(String message) {
        super(message);
    }
}
