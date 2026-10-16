package com.hcl.realestate.core.exception;

/**
 * Custom Unchecked Exception demonstrating A1-EX-02.
 * Thrown when an actor attempts an action without adequate ownership
 * or RBAC privilege (e.g. Insecure Direct Object Reference / IDOR prevention).
 */
public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
