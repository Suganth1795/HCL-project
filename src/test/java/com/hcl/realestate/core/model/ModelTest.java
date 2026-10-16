package com.hcl.realestate.core.model;

import com.hcl.realestate.core.util.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Task 4: Domain Models (Property, User, Lease) Unit Tests")
class ModelTest {

    @Test
    @DisplayName("Property model supports encapsulation and full lifecycle initialization")
    void testPropertyModelEncapsulation() {
        Property p = new Property(1L, 101L, "Prestige High-Rise 3BHK", "Spacious luxury flat",
                Constants.TYPE_APARTMENT, "Bangalore", "Bellandur", 3, 45000.0, 150000.0, Constants.STATUS_APPROVED);

        assertEquals(1L, p.getId());
        assertEquals(101L, p.getOwnerId());
        assertEquals("Prestige High-Rise 3BHK", p.getTitle());
        assertEquals("Spacious luxury flat", p.getDescription());
        assertEquals(Constants.TYPE_APARTMENT, p.getPropertyType());
        assertEquals("Bangalore", p.getCity());
        assertEquals("Bellandur", p.getLocality());
        assertEquals(3, p.getBhk());
        assertEquals(45000.0, p.getMonthlyRent());
        assertEquals(150000.0, p.getDepositAmount());
        assertEquals(Constants.STATUS_APPROVED, p.getStatus());

        // Test setters
        p.setMonthlyRent(48000.0);
        assertEquals(48000.0, p.getMonthlyRent());
        p.setStatus(Constants.STATUS_RENTED);
        assertEquals(Constants.STATUS_RENTED, p.getStatus());
    }

    @Test
    @DisplayName("Property natural ordering sorts by monthly rent ascending (Comparable)")
    void testPropertyComparableNaturalOrdering() {
        Property cheap = new Property(1L, 10L, "Studio", "Compact", Constants.TYPE_STUDIO, "Chennai", "Velachery", 1, 15000.0, 50000.0, Constants.STATUS_APPROVED);
        Property medium = new Property(2L, 10L, "2BHK", "Gated", Constants.TYPE_APARTMENT, "Chennai", "Adyar", 2, 35000.0, 100000.0, Constants.STATUS_APPROVED);
        Property luxury = new Property(3L, 10L, "Villa", "Beachfront", Constants.TYPE_VILLA, "Chennai", "ECR", 4, 85000.0, 300000.0, Constants.STATUS_APPROVED);

        List<Property> list = new ArrayList<>(List.of(luxury, cheap, medium));
        Collections.sort(list);

        assertEquals(15000.0, list.get(0).getMonthlyRent(), "Lowest rent should be first");
        assertEquals(35000.0, list.get(1).getMonthlyRent(), "Medium rent should be second");
        assertEquals(85000.0, list.get(2).getMonthlyRent(), "Highest rent should be last");
    }

    @Test
    @DisplayName("User model encapsulates profile and generates full name correctly")
    void testUserModelEncapsulationAndFullName() {
        User user = new User(101L, "Suresh", "Kumar", "owner.suresh@gmail.com", "+919840123456", Constants.ROLE_OWNER);

        assertEquals(101L, user.getId());
        assertEquals("Suresh", user.getFirstName());
        assertEquals("Kumar", user.getLastName());
        assertEquals("Suresh Kumar", user.getFullName());
        assertEquals("owner.suresh@gmail.com", user.getEmail());
        assertEquals(Constants.ROLE_OWNER, user.getRole());

        // Default constructor sets default role
        User newUser = new User();
        assertEquals(Constants.ROLE_SEEKER, newUser.getRole());
    }

    @Test
    @DisplayName("Lease model calculates tenancy duration in months and tracks active status")
    void testLeaseModelDurationAndStatus() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2027, 1, 1); // 1 full year

        Lease lease = new Lease(501L, 1L, 201L, 101L, start, end, 35000.0, 100000.0, Constants.LEASE_ACTIVE);

        assertEquals(501L, lease.getId());
        assertEquals(1L, lease.getPropertyId());
        assertEquals(201L, lease.getTenantId());
        assertEquals(101L, lease.getOwnerId());
        assertEquals(12, lease.getDurationInMonths(), "1 year tenancy should equal exactly 12 months");
        assertTrue(lease.isActive());

        lease.setStatus(Constants.LEASE_TERMINATED);
        assertFalse(lease.isActive());
    }

    @Test
    @DisplayName("Object identity: equals and hashCode contracts function correctly")
    void testObjectIdentityAndEquality() {
        Property p1 = new Property(1L, 10L, "Apt A", "Desc", Constants.TYPE_APARTMENT, "Pune", "Hinjewadi", 2, 25000.0, 60000.0, Constants.STATUS_APPROVED);
        Property p2 = new Property(1L, 10L, "Apt B", "Desc", Constants.TYPE_APARTMENT, "Pune", "Kothrud", 3, 30000.0, 80000.0, Constants.STATUS_APPROVED);
        Property p3 = new Property(2L, 10L, "Apt A", "Desc", Constants.TYPE_APARTMENT, "Pune", "Hinjewadi", 2, 25000.0, 60000.0, Constants.STATUS_APPROVED);

        assertEquals(p1, p2, "Properties with matching IDs should be equal");
        assertNotEquals(p1, p3, "Properties with different IDs should not be equal");
        assertEquals(p1.hashCode(), p2.hashCode());
    }
}