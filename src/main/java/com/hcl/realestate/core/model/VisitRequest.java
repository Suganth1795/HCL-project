package com.hcl.realestate.core.model;

import com.hcl.realestate.core.util.Constants;

import java.time.LocalDateTime;

/**
 * Domain record representing an on-site physical visit scheduled by a seeker.
 * Demonstrates:
 * - Modern Java 21 Record (A1-MOD-05)
 * - FR5 Site Visit scheduling and lifecycle
 */
public record VisitRequest(
        Long id,
        Long propertyId,
        Long seekerId,
        LocalDateTime preferredDateTime,
        String status,
        String ownerNotes
) {
    /**
     * Factory method to schedule a new visit request in REQUESTED status.
     */
    public static VisitRequest create(Long id, Long propertyId, Long seekerId, LocalDateTime preferredDateTime) {
        return new VisitRequest(id, propertyId, seekerId, preferredDateTime, Constants.VISIT_REQUESTED, null);
    }

    /**
     * Confirms the visit request with optional owner notes.
     */
    public VisitRequest confirm(String notes) {
        return new VisitRequest(id, propertyId, seekerId, preferredDateTime, Constants.VISIT_CONFIRMED, notes);
    }

    /**
     * Cancels the visit request with a cancellation note.
     */
    public VisitRequest cancel(String notes) {
        return new VisitRequest(id, propertyId, seekerId, preferredDateTime, Constants.VISIT_CANCELLED, notes);
    }
}
