package com.hcl.realestate.core.service;

import com.hcl.realestate.core.dto.SearchCriteria;
import com.hcl.realestate.core.exception.ListingValidationException;
import com.hcl.realestate.core.model.Enquiry;
import com.hcl.realestate.core.model.Lease;
import com.hcl.realestate.core.model.Property;
import com.hcl.realestate.core.model.User;
import com.hcl.realestate.core.model.VisitRequest;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Service interface defining operations for Functional Requirements FR1 through FR6.
 * Demonstrates:
 * - List (A1-COL-01) for catalog and search results
 * - Set (A1-COL-02) for deduplicated seeker favourites
 * - Map (A1-COL-03) for city grouping, user favourites, inquiries, visits, and active leases
 * - Queue (A1-COL-04) for FIFO admin approval workflow
 * - Comparator (A1-COL-06) for multi-criteria property sorting
 */
public interface ListingService {

    // --- FR1: Owner Listing Management ---

    Property createListing(Property property) throws ListingValidationException;

    List<Property> getListingsByOwner(Long ownerId);

    Property deactivateListing(Long propertyId, User actor);

    // --- FR2: Admin Listing Moderation (Queue) ---

    Property approveListing(Long propertyId);

    Property rejectListing(Long propertyId, String reason);

    Optional<Property> getNextPendingForModeration();

    int getPendingQueueSize();

    // --- FR3: Seeker Dynamic Property Search & Favourites ---

    List<Property> search(SearchCriteria criteria);

    boolean addToFavourites(Property property);

    boolean removeFromFavourites(Long propertyId);

    Set<Property> getFavourites();

    Set<String> getAvailableCities();

    Optional<Property> findById(Long id);

    List<Property> getAllListings();

    // --- FR4: Seeker Favourites & Shortlists (Map<Long, Set<Property>>) ---

    boolean addFavourite(Long userId, Property property);

    boolean removeFavourite(Long userId, Long propertyId);

    Set<Property> getFavourites(Long userId);

    Map<Long, Set<Property>> getAllFavouritesByUser();

    // --- FR5: Site Visits & Inquiries (Map<Long, List<Enquiry>> & Map<Long, List<VisitRequest>>) ---

    Enquiry sendEnquiry(Long propertyId, Long seekerId, String message);

    Enquiry respondToEnquiry(Long propertyId, Long enquiryId, String response);

    List<Enquiry> getEnquiriesForProperty(Long propertyId);

    VisitRequest requestSiteVisit(Long propertyId, Long seekerId, LocalDateTime preferredTime);

    VisitRequest confirmSiteVisit(Long propertyId, Long visitId, String ownerNotes);

    List<VisitRequest> getVisitRequestsForProperty(Long propertyId);

    // --- FR6: Lease Agreement Creation (Map<Long, Lease>) ---

    Lease createLease(Lease lease, User actor) throws ListingValidationException;

    Optional<Lease> getActiveLeaseForProperty(Long propertyId);

    List<Lease> getLeasesForTenant(Long tenantId);

    Map<Long, Lease> getActiveLeases();

    // --- Reports & Sorting (Map<String, List<Property>> & Comparator<Property>) ---

    Map<String, List<Property>> groupPropertiesByCity();

    String generateSortedCityReport(Comparator<Property> comparator);
}
