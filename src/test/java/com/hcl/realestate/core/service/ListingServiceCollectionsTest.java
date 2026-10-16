package com.hcl.realestate.core.service;

import com.hcl.realestate.core.dto.SearchCriteria;
import com.hcl.realestate.core.exception.ListingValidationException;
import com.hcl.realestate.core.exception.PropertyNotFoundException;
import com.hcl.realestate.core.exception.UnauthorizedAccessException;
import com.hcl.realestate.core.model.Property;
import com.hcl.realestate.core.model.User;
import com.hcl.realestate.core.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Task 7: Collections (List, Set, Queue) and FR1-FR3 Tests")
class ListingServiceCollectionsTest {

    private ListingServiceImpl listingService;
    private ApprovalQueueService queueService;
    private User owner;
    private User intruder;

    @BeforeEach
    void setUp() {
        queueService = new ApprovalQueueService();
        listingService = new ListingServiceImpl(queueService);

        owner = new User(1L, "Vikram", "Seth", "vikram@example.com", "9876543210", Constants.ROLE_OWNER);
        intruder = new User(99L, "Bad", "Actor", "bad@example.com", "9000000000", Constants.ROLE_SEEKER);
    }

    @Test
    @DisplayName("FR1 & Queue: Creating a listing adds to List catalog and enqueues to FIFO Queue")
    void testFR1CreateListingEnqueuesForModeration() throws ListingValidationException {
        Property property = new Property(null, owner.getId(), "Skyline Penthouse", "360-degree city view",
                "PENTHOUSE", "Mumbai", "Bandra", 4, 150000.0, 500000.0, Constants.STATUS_DRAFT);

        Property created = listingService.createListing(property);

        assertNotNull(created.getId(), "Created property must have an assigned ID");
        assertEquals(Constants.STATUS_PENDING_REVIEW, created.getStatus());

        // Check catalog List contains the listing
        assertEquals(1, listingService.getAllListings().size());

        // Check FIFO Queue contains the submission
        assertEquals(1, queueService.getQueueSize());
        Optional<Property> head = queueService.peekNext();
        assertTrue(head.isPresent());
        assertEquals("Skyline Penthouse", head.get().getTitle());
    }

    @Test
    @DisplayName("FR1 & List: getListingsByOwner returns filtered List of owner properties")
    void testFR1GetListingsByOwner() throws ListingValidationException {
        Property p1 = new Property(null, 1L, "Flat A", "Desc", "APARTMENT", "Chennai", "Loc", 2, 25000.0, 50000.0, Constants.STATUS_DRAFT);
        Property p2 = new Property(null, 1L, "Flat B", "Desc", "APARTMENT", "Chennai", "Loc", 3, 35000.0, 70000.0, Constants.STATUS_DRAFT);
        Property p3 = new Property(null, 2L, "Flat C", "Desc", "APARTMENT", "Bangalore", "Loc", 1, 15000.0, 30000.0, Constants.STATUS_DRAFT);

        listingService.createListing(p1);
        listingService.createListing(p2);
        listingService.createListing(p3);

        List<Property> owner1Listings = listingService.getListingsByOwner(1L);
        assertEquals(2, owner1Listings.size());

        List<Property> owner2Listings = listingService.getListingsByOwner(2L);
        assertEquals(1, owner2Listings.size());
    }

    @Test
    @DisplayName("FR1: Deactivate listing verifies ownership authorization before mutation")
    void testFR1DeactivateListingAuthorization() throws ListingValidationException {
        Property p1 = new Property(null, owner.getId(), "Flat D", "Desc", "APARTMENT", "Pune", "Loc", 2, 20000.0, 40000.0, Constants.STATUS_DRAFT);
        Property created = listingService.createListing(p1);

        // Intruder attempt fails with UnauthorizedAccessException
        assertThrows(UnauthorizedAccessException.class,
                () -> listingService.deactivateListing(created.getId(), intruder));

        // Owner deactivates successfully
        Property deactivated = listingService.deactivateListing(created.getId(), owner);
        assertEquals(Constants.STATUS_DEACTIVATED, deactivated.getStatus());
    }

    @Test
    @DisplayName("FR2 & Queue: Admin moderation queue enforces strict FIFO processing")
    void testFR2QueueEnforcesFIFOOrdering() throws ListingValidationException {
        Property first = new Property(null, 1L, "First Submission", "Desc", "APARTMENT", "Chennai", "Loc", 2, 20000.0, 40000.0, Constants.STATUS_DRAFT);
        Property second = new Property(null, 1L, "Second Submission", "Desc", "APARTMENT", "Bangalore", "Loc", 3, 30000.0, 60000.0, Constants.STATUS_DRAFT);

        listingService.createListing(first);
        listingService.createListing(second);

        assertEquals(2, queueService.getQueueSize());

        // First in must be first out
        Optional<Property> polledFirst = queueService.pollNext();
        assertTrue(polledFirst.isPresent());
        assertEquals("First Submission", polledFirst.get().getTitle());

        Optional<Property> polledSecond = queueService.pollNext();
        assertTrue(polledSecond.isPresent());
        assertEquals("Second Submission", polledSecond.get().getTitle());

        assertTrue(queueService.isEmpty());
    }

    @Test
    @DisplayName("FR2: Approving a pending listing marks it APPROVED and searchable")
    void testFR2ApproveListing() throws ListingValidationException {
        Property property = new Property(null, 1L, "Boutique Apartment", "Desc", "APARTMENT", "Chennai", "Adyar", 2, 32000.0, 70000.0, Constants.STATUS_DRAFT);
        Property created = listingService.createListing(property);

        // Before approval, not discoverable in public search
        List<Property> preSearch = listingService.search(SearchCriteria.byCity("Chennai"));
        assertTrue(preSearch.isEmpty());

        // Admin approves listing
        Property approved = listingService.approveListing(created.getId());
        assertEquals(Constants.STATUS_APPROVED, approved.getStatus());

        // Now discoverable in public search
        List<Property> postSearch = listingService.search(SearchCriteria.byCity("Chennai"));
        assertEquals(1, postSearch.size());
        assertEquals(approved.getId(), postSearch.get(0).getId());
    }

    @Test
    @DisplayName("FR3 & List: Seeker dynamic search filters approved properties by city and budget")
    void testFR3SearchWithCriteria() {
        listingService.seedApprovedProperty(new Property(1L, 10L, "Chennai Beach Flat", "Desc", "APARTMENT", "Chennai", "Besant Nagar", 2, 28000.0, 60000.0, Constants.STATUS_APPROVED));
        listingService.seedApprovedProperty(new Property(2L, 10L, "Bangalore Luxury Villa", "Desc", "VILLA", "Bangalore", "Indiranagar", 4, 90000.0, 300000.0, Constants.STATUS_APPROVED));
        listingService.seedApprovedProperty(new Property(3L, 10L, "Bangalore Budget Studio", "Desc", "STUDIO", "Bangalore", "Whitefield", 1, 16000.0, 40000.0, Constants.STATUS_APPROVED));

        // Filter Bangalore under 50,000
        SearchCriteria criteria = SearchCriteria.byCityAndPrice("Bangalore", 10000.0, 50000.0);
        List<Property> results = listingService.search(criteria);

        assertEquals(1, results.size());
        assertEquals("Bangalore Budget Studio", results.get(0).getTitle());
    }

    @Test
    @DisplayName("FR3 & Set: addToFavourites enforces deduplication naturally")
    void testFR3FavouritesSetDeduplication() {
        Property property = new Property(100L, 10L, "Scenic Lake House", "Desc", "VILLA", "Udaipur", "Lake Pichola", 3, 60000.0, 150000.0, Constants.STATUS_APPROVED);

        // First addition succeeds
        boolean addedFirst = listingService.addToFavourites(property);
        assertTrue(addedFirst, "First addition to favourites should return true");
        assertEquals(1, listingService.getFavourites().size());

        // Duplicate addition is rejected by Set
        boolean addedDuplicate = listingService.addToFavourites(property);
        assertFalse(addedDuplicate, "Duplicate addition to favourites Set must return false");
        assertEquals(1, listingService.getFavourites().size(), "Set size must remain 1 after duplicate addition");

        // Removal succeeds
        boolean removed = listingService.removeFromFavourites(100L);
        assertTrue(removed);
        assertEquals(0, listingService.getFavourites().size());
    }

    @Test
    @DisplayName("FR3 & Set: getAvailableCities returns sorted unique city names")
    void testFR3AvailableCitiesSet() {
        listingService.seedApprovedProperty(new Property(1L, 10L, "P1", "Desc", "APARTMENT", "Chennai", "Loc", 2, 20000.0, 40000.0, Constants.STATUS_APPROVED));
        listingService.seedApprovedProperty(new Property(2L, 10L, "P2", "Desc", "APARTMENT", "Chennai", "Loc", 2, 25000.0, 50000.0, Constants.STATUS_APPROVED));
        listingService.seedApprovedProperty(new Property(3L, 10L, "P3", "Desc", "APARTMENT", "Hyderabad", "Loc", 2, 22000.0, 45000.0, Constants.STATUS_APPROVED));

        Set<String> cities = listingService.getAvailableCities();
        assertEquals(2, cities.size(), "Set should deduplicate Chennai");
        assertTrue(cities.contains("Chennai"));
        assertTrue(cities.contains("Hyderabad"));
    }
}
