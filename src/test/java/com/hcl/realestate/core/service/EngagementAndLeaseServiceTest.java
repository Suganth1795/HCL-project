package com.hcl.realestate.core.service;

import com.hcl.realestate.core.exception.InvalidStateTransitionException;
import com.hcl.realestate.core.exception.ListingValidationException;
import com.hcl.realestate.core.exception.PropertyNotFoundException;
import com.hcl.realestate.core.exception.UnauthorizedAccessException;
import com.hcl.realestate.core.model.Enquiry;
import com.hcl.realestate.core.model.Lease;
import com.hcl.realestate.core.model.Property;
import com.hcl.realestate.core.model.User;
import com.hcl.realestate.core.model.VisitRequest;
import com.hcl.realestate.core.util.Constants;
import com.hcl.realestate.core.util.PropertyComparators;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Task 8: Maps, Sorted Report, and FR4-FR6 Service Tests")
class EngagementAndLeaseServiceTest {

    private ListingServiceImpl listingService;
    private Property p1;
    private Property p2;
    private User owner;
    private User seeker;
    private User intruder;

    @BeforeEach
    void setUp() {
        listingService = new ListingServiceImpl();

        owner = new User(1L, "Raman", "Nair", "raman@example.com", "9876543210", Constants.ROLE_OWNER);
        seeker = new User(10L, "Deepak", "Sharma", "deepak@example.com", "8888888888", Constants.ROLE_SEEKER);
        intruder = new User(99L, "Intruder", "User", "intruder@example.com", "9000000000", Constants.ROLE_SEEKER);

        p1 = new Property(101L, owner.getId(), "Skyline Luxury Flat", "Desc", "APARTMENT", "Chennai", "OMR", 3, 40000.0, 100000.0, Constants.STATUS_APPROVED);
        p2 = new Property(102L, owner.getId(), "Tech Studio", "Desc", "STUDIO", "Bangalore", "HSR", 1, 20000.0, 50000.0, Constants.STATUS_APPROVED);

        listingService.seedApprovedProperty(p1);
        listingService.seedApprovedProperty(p2);
    }

    @Test
    @DisplayName("FR4 & Map: addFavourite and getFavourites map properties per User ID")
    void testFR4UserFavouritesMap() {
        Long user1 = 10L;
        Long user2 = 20L;

        assertTrue(listingService.addFavourite(user1, p1));
        assertTrue(listingService.addFavourite(user1, p2));
        assertFalse(listingService.addFavourite(user1, p1), "Duplicate add should return false");

        assertTrue(listingService.addFavourite(user2, p2));

        Set<Property> user1Favs = listingService.getFavourites(user1);
        assertEquals(2, user1Favs.size());

        Set<Property> user2Favs = listingService.getFavourites(user2);
        assertEquals(1, user2Favs.size());

        Map<Long, Set<Property>> allFavs = listingService.getAllFavouritesByUser();
        assertEquals(2, allFavs.size());

        // Removal
        assertTrue(listingService.removeFavourite(user1, p1.getId()));
        assertEquals(1, listingService.getFavourites(user1).size());
    }

    @Test
    @DisplayName("FR5 & Map: sendEnquiry and respondToEnquiry mapped per Property ID")
    void testFR5EnquiryWorkflow() {
        Enquiry e1 = listingService.sendEnquiry(p1.getId(), seeker.getId(), "Is maintenance included?");
        assertNotNull(e1.id());
        assertEquals("Is maintenance included?", e1.message());
        assertNull(e1.ownerResponse());

        List<Enquiry> list = listingService.getEnquiriesForProperty(p1.getId());
        assertEquals(1, list.size());

        Enquiry answered = listingService.respondToEnquiry(p1.getId(), e1.id(), "Yes, maintenance is inclusive.");
        assertEquals("Yes, maintenance is inclusive.", answered.ownerResponse());
        assertNotNull(answered.respondedAt());
    }

    @Test
    @DisplayName("FR5 & Map: requestSiteVisit and confirmSiteVisit mapped per Property ID")
    void testFR5SiteVisitWorkflow() {
        LocalDateTime visitTime = LocalDateTime.now().plusDays(2);
        VisitRequest request = listingService.requestSiteVisit(p1.getId(), seeker.getId(), visitTime);

        assertNotNull(request.id());
        assertEquals(Constants.VISIT_REQUESTED, request.status());

        List<VisitRequest> visits = listingService.getVisitRequestsForProperty(p1.getId());
        assertEquals(1, visits.size());

        VisitRequest confirmed = listingService.confirmSiteVisit(p1.getId(), request.id(), "Confirmed. Call guard on arrival.");
        assertEquals(Constants.VISIT_CONFIRMED, confirmed.status());
        assertEquals("Confirmed. Call guard on arrival.", confirmed.ownerNotes());
    }

    @Test
    @DisplayName("FR6 & Map: createLease registers lease and transitions property to RENTED")
    void testFR6LeaseCreationSuccess() throws ListingValidationException {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusYears(1);
        Lease lease = new Lease(null, p1.getId(), seeker.getId(), p1.getOwnerId(),
                start, end, p1.getMonthlyRent(), p1.getDepositAmount(), Constants.LEASE_ACTIVE);

        Lease created = listingService.createLease(lease, owner);
        assertNotNull(created.getId());
        assertEquals(Constants.STATUS_RENTED, p1.getStatus());

        Optional<Lease> activeLease = listingService.getActiveLeaseForProperty(p1.getId());
        assertTrue(activeLease.isPresent());
        assertEquals(created.getId(), activeLease.get().getId());

        List<Lease> tenantLeases = listingService.getLeasesForTenant(seeker.getId());
        assertEquals(1, tenantLeases.size());
    }

    @Test
    @DisplayName("FR6: createLease rejects unauthorized user or duplicate lease on rented property")
    void testFR6LeaseCreationSecurityAndDuplicates() throws ListingValidationException {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusYears(1);
        Lease lease = new Lease(null, p1.getId(), seeker.getId(), p1.getOwnerId(),
                start, end, p1.getMonthlyRent(), p1.getDepositAmount(), Constants.LEASE_ACTIVE);

        // Intruder fails authorization check
        assertThrows(UnauthorizedAccessException.class,
                () -> listingService.createLease(lease, intruder));

        // Owner succeeds
        listingService.createLease(lease, owner);

        // Attempting duplicate lease on already rented property fails
        Lease secondLease = new Lease(null, p1.getId(), 20L, p1.getOwnerId(),
                start, end, p1.getMonthlyRent(), p1.getDepositAmount(), Constants.LEASE_ACTIVE);

        assertThrows(InvalidStateTransitionException.class,
                () -> listingService.createLease(secondLease, owner));
    }

    @Test
    @DisplayName("Map & Comparator: groupPropertiesByCity and generateSortedCityReport")
    void testGroupPropertiesByCityAndSortedReport() {
        Map<String, List<Property>> grouped = listingService.groupPropertiesByCity();
        assertEquals(2, grouped.size(), "Should have Chennai and Bangalore groups");
        assertTrue(grouped.containsKey("Chennai"));
        assertTrue(grouped.containsKey("Bangalore"));

        String report = listingService.generateSortedCityReport(PropertyComparators.BY_RENT_ASC);
        assertNotNull(report);
        assertTrue(report.contains("REAL ESTATE VERIFIED PROPERTIES REPORT"));
        assertTrue(report.contains("BANGALORE"));
        assertTrue(report.contains("CHENNAI"));
        assertTrue(report.contains("OVERALL PORTFOLIO"));
    }
}
