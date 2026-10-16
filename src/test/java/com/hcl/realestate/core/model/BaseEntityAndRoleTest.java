package com.hcl.realestate.core.model;

import com.hcl.realestate.core.util.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Task 5: BaseEntity and Role Hierarchy Tests")
class BaseEntityAndRoleTest {

    @Test
    @DisplayName("BaseEntity should initialize createdAt and updatedAt timestamps automatically")
    void testBaseEntityTimestamps() {
        Property property = new Property();
        assertNotNull(property.getCreatedAt(), "createdAt must not be null");
        assertNotNull(property.getUpdatedAt(), "updatedAt must not be null");
        assertNull(property.getId(), "Default id should be null");

        LocalDateTime initialUpdated = property.getUpdatedAt();
        property.setId(100L);
        assertEquals(100L, property.getId());
        assertTrue(property.getUpdatedAt().isAfter(initialUpdated) || property.getUpdatedAt().isEqual(initialUpdated),
                "Setting ID should refresh updatedAt timestamp");
    }

    @Test
    @DisplayName("Property, User, and Lease entities should properly inherit from BaseEntity")
    void testEntitiesInheritance() {
        Property property = new Property(1L, 10L, "Luxury Villa", "Desc", "VILLA", "Chennai", "OMR", 4, 85000.0, 400000.0, Constants.STATUS_APPROVED);
        User user = new User(2L, "Kavitha", "Raman", "kavitha@example.com", "9876543210", Constants.ROLE_OWNER);
        Lease lease = new Lease(3L, 1L, 2L, 10L, LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1), 85000.0, 400000.0, Constants.LEASE_ACTIVE);

        assertInstanceOf(BaseEntity.class, property, "Property must be an instance of BaseEntity");
        assertInstanceOf(BaseEntity.class, user, "User must be an instance of BaseEntity");
        assertInstanceOf(BaseEntity.class, lease, "Lease must be an instance of BaseEntity");

        assertEquals(1L, property.getId());
        assertEquals(2L, user.getId());
        assertEquals(3L, lease.getId());
    }

    @Test
    @DisplayName("Role enum authority levels should follow ADMIN > OWNER > TENANT > SEEKER hierarchy")
    void testRoleAuthorityLevels() {
        assertTrue(Role.ROLE_ADMIN.getAuthorityLevel() > Role.ROLE_OWNER.getAuthorityLevel());
        assertTrue(Role.ROLE_OWNER.getAuthorityLevel() > Role.ROLE_TENANT.getAuthorityLevel());
        assertTrue(Role.ROLE_TENANT.getAuthorityLevel() > Role.ROLE_SEEKER.getAuthorityLevel());

        assertTrue(Role.ROLE_ADMIN.hasHigherOrEqualAuthority(Role.ROLE_OWNER));
        assertTrue(Role.ROLE_OWNER.hasHigherOrEqualAuthority(Role.ROLE_SEEKER));
        assertFalse(Role.ROLE_SEEKER.hasHigherOrEqualAuthority(Role.ROLE_ADMIN));
    }

    @Test
    @DisplayName("RoleHierarchy should resolve implied reachable roles correctly")
    void testRoleHierarchyReachability() {
        Set<Role> adminReachable = RoleHierarchy.getReachableRoles(Role.ROLE_ADMIN);
        assertTrue(adminReachable.containsAll(Set.of(Role.ROLE_ADMIN, Role.ROLE_OWNER, Role.ROLE_TENANT, Role.ROLE_SEEKER)),
                "ADMIN must have access to all roles");

        Set<Role> ownerReachable = RoleHierarchy.getReachableRoles(Role.ROLE_OWNER);
        assertTrue(ownerReachable.contains(Role.ROLE_OWNER));
        assertTrue(ownerReachable.contains(Role.ROLE_SEEKER));
        assertFalse(ownerReachable.contains(Role.ROLE_ADMIN));

        Set<Role> seekerReachable = RoleHierarchy.getReachableRoles(Role.ROLE_SEEKER);
        assertEquals(Set.of(Role.ROLE_SEEKER), seekerReachable);
    }

    @Test
    @DisplayName("User with multiple roles should inherit combined reachability via RoleHierarchy")
    void testUserRoleHierarchyIntegration() {
        User adminUser = new User(1L, "System", "Admin", "admin@realestate.com", "9999999999", Role.ROLE_ADMIN.name());
        assertTrue(adminUser.hasAccess(Role.ROLE_ADMIN));
        assertTrue(adminUser.hasAccess(Role.ROLE_OWNER));
        assertTrue(adminUser.hasAccess(Role.ROLE_SEEKER));

        User ownerUser = new User(2L, "Suresh", "Kumar", "suresh@example.com", "8888888888", Role.ROLE_OWNER.name());
        assertTrue(ownerUser.hasAccess(Role.ROLE_OWNER));
        assertTrue(ownerUser.hasAccess(Role.ROLE_SEEKER));
        assertFalse(ownerUser.hasAccess(Role.ROLE_ADMIN));

        User seekerUser = new User(3L, "Ananya", "Sharma", "ananya@example.com", "7777777777", Role.ROLE_SEEKER.name());
        assertTrue(seekerUser.hasAccess(Role.ROLE_SEEKER));
        assertFalse(seekerUser.hasAccess(Role.ROLE_OWNER));
        assertFalse(seekerUser.hasAccess(Role.ROLE_ADMIN));
    }
}
