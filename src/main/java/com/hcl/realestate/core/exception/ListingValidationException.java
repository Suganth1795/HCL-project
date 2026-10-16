package com.hcl.realestate.core.exception;

/**
 * Custom Checked Exception demonstrating A1-EX-01.
 * Thrown when a property listing fails business validation invariants
 * (e.g., rent below minimum, invalid security deposit, missing mandatory attributes).
 * Callers must explicitly handle or declare this exception.
 */
public class ListingValidationException extends Exception {

    private final String field;

    /**
     * Constructs a validation exception with a general error message.
     *
     * @param message human-readable validation failure reason
     */
    public ListingValidationException(String message) {
        super(message);
        this.field = null;
    }

    /**
     * Constructs a validation exception pinpointing a specific property field.
     *
     * @param field   the domain property attribute that violated business rules
     * @param message detailed validation failure explanation
     */
    public ListingValidationException(String field, String message) {
        super("Validation failed on field [" + field + "]: " + message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
