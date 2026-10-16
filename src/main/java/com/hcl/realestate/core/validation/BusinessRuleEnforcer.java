package com.hcl.realestate.core.validation;

import com.hcl.realestate.core.exception.InvalidStateTransitionException;
import com.hcl.realestate.core.exception.ListingValidationException;
import com.hcl.realestate.core.exception.UnauthorizedAccessException;
import com.hcl.realestate.core.model.Property;
import com.hcl.realestate.core.model.Role;
import com.hcl.realestate.core.model.User;
import com.hcl.realestate.core.util.Constants;

/**
 * Service enforcing core domain business rules and invariants.
 * Demonstrates:
 * - Business Rule 1: Financial and listing validity invariants (throws checked ListingValidationException)
 * - Business Rule 2: Ownership authorization and valid state transitions (throws unchecked UnauthorizedAccessException
 *   and InvalidStateTransitionException)
 */
public final class BusinessRuleEnforcer {

    private BusinessRuleEnforcer() {
        // Utility / domain service class
    }

    /**
     * Enforces Business Rule 1: Validates property attributes and financial invariants.
     * Rules:
     * - Title and City must not be blank.
     * - Monthly rent must fall strictly within [MIN_MONTHLY_RENT, MAX_MONTHLY_RENT].
     * - Deposit amount must be at least DEFAULT_SECURITY_DEPOSIT_MULTIPLIER (2x) monthly rent.
     *
     * @param property the property listing to validate
     * @throws ListingValidationException (Checked Exception) if any rule is violated
     */
    public static void validateListingInvariants(Property property) throws ListingValidationException {
        if (property == null) {
            throw new ListingValidationException("Property payload cannot be null.");
        }

        if (property.getTitle() == null || property.getTitle().trim().isBlank()) {
            throw new ListingValidationException("title", "Property title is mandatory and cannot be blank.");
        }

        if (property.getCity() == null || property.getCity().trim().isBlank()) {
            throw new ListingValidationException("city", "Property city is mandatory and cannot be blank.");
        }

        double rent = property.getMonthlyRent();
        if (rent < Constants.MIN_MONTHLY_RENT || rent > Constants.MAX_MONTHLY_RENT) {
            throw new ListingValidationException("monthlyRent",
                    String.format("Monthly rent (INR %,.2f) must be between INR %,.2f and INR %,.2f.",
                            rent, Constants.MIN_MONTHLY_RENT, Constants.MAX_MONTHLY_RENT));
        }

        double minimumDeposit = rent * Constants.DEFAULT_SECURITY_DEPOSIT_MULTIPLIER;
        if (property.getDepositAmount() < minimumDeposit) {
            throw new ListingValidationException("depositAmount",
                    String.format("Security deposit (INR %,.2f) must be at least %dx monthly rent (minimum INR %,.2f).",
                            property.getDepositAmount(), (int) Constants.DEFAULT_SECURITY_DEPOSIT_MULTIPLIER, minimumDeposit));
        }
    }

    /**
     * Enforces Business Rule 2A: Verifies ownership authorization (IDOR protection).
     * Only the registered owner or a platform administrator can modify or deactivate a property.
     *
     * @param property the property to be modified
     * @param actor    the user initiating the modification
     * @throws UnauthorizedAccessException (Unchecked Exception) if actor lacks ownership or admin rights
     */
    public static void verifyOwnershipAuthorization(Property property, User actor) {
        if (property == null) {
            throw new IllegalArgumentException("Target property cannot be null.");
        }
        if (actor == null) {
            throw new UnauthorizedAccessException("Authentication required: Actor cannot be null.");
        }

        // Platform administrator has overriding privilege
        if (actor.hasAccess(Role.ROLE_ADMIN)) {
            return;
        }

        // Owner check
        if (property.getOwnerId() == null || !property.getOwnerId().equals(actor.getId())) {
            throw new UnauthorizedAccessException(
                    String.format("Access Denied: User [%s] (ID: %d) is not authorized to modify property (ID: %d, Owner ID: %d).",
                            actor.getFullName(), actor.getId(), property.getId(), property.getOwnerId()));
        }
    }

    /**
     * Enforces Business Rule 2B: Verifies valid lifecycle state transition for lease creation.
     * A property can only be leased when in APPROVED status. Transitioning from DRAFT, RENTED,
     * or DEACTIVATED is illegal.
     *
     * @param property the property to lease
     * @throws InvalidStateTransitionException (Unchecked Exception) if state transition is invalid
     */
    public static void verifyCanCreateLease(Property property) {
        if (property == null) {
            throw new IllegalArgumentException("Property cannot be null.");
        }

        String currentStatus = property.getStatus();
        if (!Constants.STATUS_APPROVED.equalsIgnoreCase(currentStatus)) {
            throw new InvalidStateTransitionException(
                    currentStatus,
                    Constants.STATUS_RENTED,
                    String.format("Cannot execute lease: Property ID %d is in [%s] status. Only [%s] properties can be leased.",
                            property.getId(), currentStatus, Constants.STATUS_APPROVED));
        }
    }
}
