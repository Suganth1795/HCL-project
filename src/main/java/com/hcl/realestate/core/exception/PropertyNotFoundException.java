package com.hcl.realestate.core.exception;

/**
 * Custom Unchecked Exception demonstrating A1-EX-02.
 * Thrown when an inquiry, modification, or lease operation targets a non-existent property ID.
 */
public class PropertyNotFoundException extends RuntimeException {

    private final Long propertyId;

    public PropertyNotFoundException(Long propertyId) {
        super("Property not found with ID: " + propertyId);
        this.propertyId = propertyId;
    }

    public Long getPropertyId() {
        return propertyId;
    }
}
