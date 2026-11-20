package org.example.Lab4.access;

// Thrown by AccessControlProxy when the current role is not allowed to perform an action.
public class AccessDeniedException extends IllegalStateException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
