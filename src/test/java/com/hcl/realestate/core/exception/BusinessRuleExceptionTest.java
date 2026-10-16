package com.hcl.realestate.core.exception;

import com.hcl.realestate.core.model.Property;
import com.hcl.realestate.core.model.Role;
import com.hcl.realestate.core.model.User;
import com.hcl.realestate.core.util.Constants;
import com.hcl.realestate.core.validation.BusinessRuleEnforcer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Task 6: Custom Exception & Business Rule Enforcement Tests")
class BusinessRuleExceptionTest {

    private Property validProperty;
    private User ownerUser;
    private User adminUser;
    private User seekerUser;

    @BeforeEach
    void setUp() {
        validProperty = new Property(1L, 10L, "Prestige Lakeside", "Premium 3BHK",
                "APARTMENT", "Bangalore", "Whitefield", 3, 35000.0, 70000.0, Constants.STATUS_APPROVED);

        ownerUser = new User(10L, "Suresh", "Rao", "suresh@example.com", "9876543210", Constants.ROLE_OWNER);
        adminUser = new User(1L, "Admin", "User", "admin@realestate.com", "9999999999", Role.ROLE_ADMIN.name());
        seekerUser = new User(20L, "Anand", "Verma", "anand@example.com", "8888888888", Constants.ROLE_SEEKER);
    }

    @Test
    @DisplayName("Business Rule 1: Valid property passes listing invariant validation")
    void testValidListingPasses() {
        assertDoesNotThrow(() -> BusinessRuleEnforcer.validateListingInvariants(validProperty));
    }

    @Test
    @DisplayName("Business Rule 1: Blank title or city throws ListingValidationException (Checked)")
    void testBlankTitleOrCityThrowsValidationException() {
        Property noTitle = new Property(2L, 10L, "   ", "Desc", "APARTMENT", "Bangalore", "Loc", 2, 20000.0, 40000.0, Constants.STATUS_DRAFT);
        ListingValidationException exTitle = assertThrows(ListingValidationException.class,
                () -> BusinessRuleEnforcer.validateListingInvariants(noTitle));
        assertEquals("title", exTitle.getField());

        Property noCity = new Property(3L, 10L, "Title", "Desc", "APARTMENT", "   ", "Loc", 2, 20000.0, 40000.0, Constants.STATUS_DRAFT);
        ListingValidationException exCity = assertThrows(ListingValidationException.class,
                () -> BusinessRuleEnforcer.validateListingInvariants(noCity));
        assertEquals("city", exCity.getField());
    }

    @Test
    @DisplayName("Business Rule 1: Monthly rent outside [1,000, 1,000,000] throws ListingValidationException")
    void testRentBoundsThrowValidationException() {
        // Rent below minimum (INR 500 < 1,000)
        Property lowRent = new Property(4L, 10L, "Micro Shed", "Desc", "ROOM", "Chennai", "Loc", 1, 500.0, 2000.0, Constants.STATUS_DRAFT);
        ListingValidationException exLow = assertThrows(ListingValidationException.class,
                () -> BusinessRuleEnforcer.validateListingInvariants(lowRent));
        assertEquals("monthlyRent", exLow.getField());

        // Rent above maximum (INR 1,500,000 > 1,000,000)
        Property highRent = new Property(5L, 10L, "Palace", "Desc", "MANSION", "Mumbai", "Loc", 10, 1500000.0, 5000000.0, Constants.STATUS_DRAFT);
        ListingValidationException exHigh = assertThrows(ListingValidationException.class,
                () -> BusinessRuleEnforcer.validateListingInvariants(highRent));
        assertEquals("monthlyRent", exHigh.getField());
    }

    @Test
    @DisplayName("Business Rule 1: Deposit less than 2x monthly rent throws ListingValidationException")
    void testInsufficientDepositThrowsValidationException() {
        // Rent = 30,000; Minimum Deposit required = 60,000. Provided = 40,000.
        Property lowDeposit = new Property(6L, 10L, "City Flat", "Desc", "APARTMENT", "Hyderabad", "Loc", 2, 30000.0, 40000.0, Constants.STATUS_DRAFT);
        ListingValidationException ex = assertThrows(ListingValidationException.class,
                () -> BusinessRuleEnforcer.validateListingInvariants(lowDeposit));
        assertEquals("depositAmount", ex.getField());
    }

    @Test
    @DisplayName("Business Rule 2A: Owner or Admin is authorized to modify property")
    void testOwnerAndAdminAuthorized() {
        assertDoesNotThrow(() -> BusinessRuleEnforcer.verifyOwnershipAuthorization(validProperty, ownerUser));
        assertDoesNotThrow(() -> BusinessRuleEnforcer.verifyOwnershipAuthorization(validProperty, adminUser));
    }

    @Test
    @DisplayName("Business Rule 2A: Unauthorized user throws UnauthorizedAccessException (Unchecked)")
    void testUnauthorizedUserThrowsUnauthorizedAccessException() {
        UnauthorizedAccessException ex = assertThrows(UnauthorizedAccessException.class,
                () -> BusinessRuleEnforcer.verifyOwnershipAuthorization(validProperty, seekerUser));
        assertTrue(ex.getMessage().contains("Access Denied"));
    }

    @Test
    @DisplayName("Business Rule 2B: Lease permitted only when property is in APPROVED status")
    void testLeasePermittedOnlyForApprovedStatus() {
        assertDoesNotThrow(() -> BusinessRuleEnforcer.verifyCanCreateLease(validProperty));

        Property draftProperty = new Property(7L, 10L, "Draft Home", "Desc", "APARTMENT", "Delhi", "Loc", 2, 25000.0, 50000.0, Constants.STATUS_DRAFT);
        InvalidStateTransitionException exDraft = assertThrows(InvalidStateTransitionException.class,
                () -> BusinessRuleEnforcer.verifyCanCreateLease(draftProperty));
        assertEquals(Constants.STATUS_DRAFT, exDraft.getFromState());
        assertEquals(Constants.STATUS_RENTED, exDraft.getToState());

        Property rentedProperty = new Property(8L, 10L, "Already Rented Home", "Desc", "APARTMENT", "Delhi", "Loc", 2, 25000.0, 50000.0, Constants.STATUS_RENTED);
        InvalidStateTransitionException exRented = assertThrows(InvalidStateTransitionException.class,
                () -> BusinessRuleEnforcer.verifyCanCreateLease(rentedProperty));
        assertEquals(Constants.STATUS_RENTED, exRented.getFromState());
    }

    @Test
    @DisplayName("A1-EX-03: Multi-catch correctly traps both domain runtime exceptions")
    void testMultiCatchDomainExceptions() {
        boolean caughtUnauthorized = false;
        try {
            BusinessRuleEnforcer.verifyOwnershipAuthorization(validProperty, seekerUser);
        } catch (UnauthorizedAccessException | InvalidStateTransitionException | PropertyNotFoundException e) {
            caughtUnauthorized = true;
        }
        assertTrue(caughtUnauthorized, "Multi-catch should catch UnauthorizedAccessException");

        boolean caughtStateTransition = false;
        try {
            validProperty.setStatus(Constants.STATUS_DEACTIVATED);
            BusinessRuleEnforcer.verifyCanCreateLease(validProperty);
        } catch (UnauthorizedAccessException | InvalidStateTransitionException | PropertyNotFoundException e) {
            caughtStateTransition = true;
        }
        assertTrue(caughtStateTransition, "Multi-catch should catch InvalidStateTransitionException");
    }
}
