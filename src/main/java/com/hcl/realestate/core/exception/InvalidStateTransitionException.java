package com.hcl.realestate.core.exception;

/**
 * Custom Unchecked Exception demonstrating A1-EX-02.
 * Thrown when an entity lifecycle transition violates domain finite state machine rules
 * (e.g. attempting to lease a property that is not APPROVED, or approving an already RENTED property).
 */
public class InvalidStateTransitionException extends RuntimeException {

    private final String fromState;
    private final String toState;

    public InvalidStateTransitionException(String fromState, String toState, String reason) {
        super("Invalid state transition from [" + fromState + "] to [" + toState + "]: " + reason);
        this.fromState = fromState;
        this.toState = toState;
    }

    public String getFromState() {
        return fromState;
    }

    public String getToState() {
        return toState;
    }
}
