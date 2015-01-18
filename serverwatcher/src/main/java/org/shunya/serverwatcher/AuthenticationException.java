package org.shunya.serverwatcher;

public class AuthenticationException extends Exception {
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(Exception e) {
        super(e);
    }
}
