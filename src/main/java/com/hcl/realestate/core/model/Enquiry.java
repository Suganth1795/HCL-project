package com.hcl.realestate.core.model;

import java.time.LocalDateTime;

/**
 * Domain record representing an inquiry sent by a prospective seeker to a property owner.
 * Demonstrates:
 * - Modern Java 21 Record (A1-MOD-05)
 * - FR5 Seeker Inquiries
 */
public record Enquiry(
        Long id,
        Long propertyId,
        Long seekerId,
        String message,
        String ownerResponse,
        LocalDateTime createdAt,
        LocalDateTime respondedAt
) {
    /**
     * Factory method to create a new unresponded inquiry.
     */
    public static Enquiry create(Long id, Long propertyId, Long seekerId, String message) {
        return new Enquiry(id, propertyId, seekerId, message, null, LocalDateTime.now(), null);
    }

    /**
     * Creates an updated inquiry containing the owner's response.
     */
    public Enquiry withResponse(String response) {
        return new Enquiry(id, propertyId, seekerId, message, response, createdAt, LocalDateTime.now());
    }
}
