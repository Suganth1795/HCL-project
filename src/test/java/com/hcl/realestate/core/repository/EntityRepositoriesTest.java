package com.hcl.realestate.core.repository;

import com.hcl.realestate.core.model.Property;
import com.hcl.realestate.core.model.Role;
import com.hcl.realestate.core.model.User;
import com.hcl.realestate.core.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Entity-Specific Repository Tests")
class EntityRepositoriesTest {

    private InMemoryPropertyRepository propertyRepo;
    private InMemoryUserRepository userRepo;

    @BeforeEach
    void setUp() {
        propertyRepo = new InMemoryPropertyRepository();
        userRepo = new InMemoryUserRepository();
    }

    @Test
    @DisplayName("InMemoryPropertyRepository correctly filters by city case-insensitively")
    void testFindByCity() {
        propertyRepo.save(new Property(1L, 10L, "Bangalore Flat 1", "desc", "APARTMENT", "Bangalore", "Koramangala", 2, 35000.0, 70000.0, Constants.STATUS_APPROVED));
        propertyRepo.save(new Property(2L, 10L, "Bangalore Flat 2", "desc", "APARTMENT", "BANGALORE", "Indiranagar", 3, 55000.0, 110000.0, Constants.STATUS_APPROVED));
        propertyRepo.save(new Property(3L, 20L, "Chennai Flat 1", "desc", "APARTMENT", "Chennai", "Adyar", 2, 30000.0, 60000.0, Constants.STATUS_APPROVED));

        List<Property> bglProps = propertyRepo.findByCity("bangalore");
        assertEquals(2, bglProps.size());

        List<Property> chnProps = propertyRepo.findByCity("CHENNAI");
        assertEquals(1, chnProps.size());

        assertTrue(propertyRepo.findByCity("Mumbai").isEmpty());
        assertTrue(propertyRepo.findByCity(null).isEmpty());
    }

    @Test
    @DisplayName("InMemoryPropertyRepository filters by ownerId, status, and price range")
    void testPropertyCustomQueries() {
        propertyRepo.save(new Property(1L, 101L, "P1", "d", "APARTMENT", "Bangalore", "HSR", 2, 25000.0, 50000.0, Constants.STATUS_APPROVED));
        propertyRepo.save(new Property(2L, 101L, "P2", "d", "APARTMENT", "Bangalore", "BTM", 1, 15000.0, 30000.0, Constants.STATUS_PENDING_REVIEW));
        propertyRepo.save(new Property(3L, 102L, "P3", "d", "APARTMENT", "Chennai", "OMR", 3, 45000.0, 90000.0, Constants.STATUS_APPROVED));

        List<Property> owner101 = propertyRepo.findByOwnerId(101L);
        assertEquals(2, owner101.size());

        List<Property> pending = propertyRepo.findByStatus(Constants.STATUS_PENDING_REVIEW);
        assertEquals(1, pending.size());
        assertEquals("P2", pending.get(0).getTitle());

        List<Property> budgetProps = propertyRepo.findByPriceRange(20000.0, 50000.0);
        assertEquals(2, budgetProps.size());
    }

    @Test
    @DisplayName("InMemoryUserRepository correctly finds by email and role")
    void testUserRepositoryQueries() {
        User u1 = new User(1L, "Rajesh", "Sharma", "rajesh@example.com", "9876543210", Constants.ROLE_OWNER);
        User u2 = new User(2L, "Priya", "Nair", "priya@example.com", "9876543211", Constants.ROLE_SEEKER);
        User u3 = new User(3L, "Amit", "Patel", "amit@example.com", "9876543212", Constants.ROLE_SEEKER);

        userRepo.save(u1);
        userRepo.save(u2);
        userRepo.save(u3);

        Optional<User> foundEmail = userRepo.findByEmail("PRIYA@EXAMPLE.COM");
        assertTrue(foundEmail.isPresent());
        assertEquals("Priya", foundEmail.get().getFirstName());

        assertTrue(userRepo.findByEmail("unknown@example.com").isEmpty());
        assertTrue(userRepo.findByEmail(null).isEmpty());

        List<User> seekers = userRepo.findByRole(Constants.ROLE_SEEKER);
        assertEquals(2, seekers.size());

        List<User> owners = userRepo.findByRole(Constants.ROLE_OWNER);
        assertEquals(1, owners.size());
    }
}
