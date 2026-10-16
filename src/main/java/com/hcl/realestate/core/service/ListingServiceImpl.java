package com.hcl.realestate.core.service;

import com.hcl.realestate.core.dto.SearchCriteria;
import com.hcl.realestate.core.exception.ListingValidationException;
import com.hcl.realestate.core.exception.PropertyNotFoundException;
import com.hcl.realestate.core.model.Property;
import com.hcl.realestate.core.model.User;
import com.hcl.realestate.core.strategy.CitySearchStrategy;
import com.hcl.realestate.core.strategy.PriceRangeSearchStrategy;
import com.hcl.realestate.core.strategy.SearchStrategy;
import com.hcl.realestate.core.util.Constants;
import com.hcl.realestate.core.validation.BusinessRuleEnforcer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Concrete implementation of ListingService managing FR1, FR2, and FR3.
 * Demonstrates:
 * - List (ArrayList) for property catalog and filtered search results
 * - Set (LinkedHashSet) for unique seeker favourites and available cities
 * - Queue (LinkedBlockingQueue via ApprovalQueueService) for FIFO admin moderation
 */
public class ListingServiceImpl implements ListingService {

    private final List<Property> catalog = new ArrayList<>();
    private final Set<Property> favourites = new LinkedHashSet<>();
    private final ApprovalQueueService approvalQueueService;
    private final SearchStrategy citySearchStrategy = new CitySearchStrategy();
    private final SearchStrategy priceRangeSearchStrategy = new PriceRangeSearchStrategy();
    private final AtomicLong idGenerator = new AtomicLong(100L);

    public ListingServiceImpl() {
        this.approvalQueueService = new ApprovalQueueService();
    }

    public ListingServiceImpl(ApprovalQueueService approvalQueueService) {
        this.approvalQueueService = (approvalQueueService != null) ? approvalQueueService : new ApprovalQueueService();
    }

    // --- FR1: Owner Listing Management ---

    @Override
    public Property createListing(Property property) throws ListingValidationException {
        if (property == null) {
            throw new ListingValidationException("Property to create cannot be null.");
        }

        // Assign unique identifier if unassigned
        if (property.getId() == null || property.getId() <= 0) {
            property.setId(idGenerator.incrementAndGet());
        }

        // Enforce Business Rule 1 invariants
        BusinessRuleEnforcer.validateListingInvariants(property);

        // Submissions enter pending review status and are queued for admin moderation
        property.setStatus(Constants.STATUS_PENDING_REVIEW);
        catalog.add(property);
        approvalQueueService.enqueue(property);

        return property;
    }

    @Override
    public List<Property> getListingsByOwner(Long ownerId) {
        if (ownerId == null) {
            return Collections.emptyList();
        }
        List<Property> ownerListings = new ArrayList<>();
        for (Property p : catalog) {
            if (p.getOwnerId() != null && p.getOwnerId().equals(ownerId)) {
                ownerListings.add(p);
            }
        }
        return ownerListings;
    }

    @Override
    public Property deactivateListing(Long propertyId, User actor) {
        Property property = findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException(propertyId));

        // Enforce Business Rule 2A: Ownership Authorization
        BusinessRuleEnforcer.verifyOwnershipAuthorization(property, actor);

        property.setStatus(Constants.STATUS_DEACTIVATED);
        return property;
    }

    // --- FR2: Admin Listing Moderation (Queue) ---

    @Override
    public Property approveListing(Long propertyId) {
        Property property = findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException(propertyId));

        // Remove from head of moderation queue if present
        Optional<Property> head = approvalQueueService.peekNext();
        if (head.isPresent() && head.get().getId().equals(propertyId)) {
            approvalQueueService.pollNext();
        }

        property.setStatus(Constants.STATUS_APPROVED);
        return property;
    }

    @Override
    public Property rejectListing(Long propertyId, String reason) {
        Property property = findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException(propertyId));

        Optional<Property> head = approvalQueueService.peekNext();
        if (head.isPresent() && head.get().getId().equals(propertyId)) {
            approvalQueueService.pollNext();
        }

        property.setStatus(Constants.STATUS_REJECTED);
        return property;
    }

    @Override
    public Optional<Property> getNextPendingForModeration() {
        return approvalQueueService.peekNext();
    }

    @Override
    public int getPendingQueueSize() {
        return approvalQueueService.getQueueSize();
    }

    // --- FR3: Seeker Dynamic Property Search & Favourites ---

    @Override
    public List<Property> search(SearchCriteria criteria) {
        List<Property> results = new ArrayList<>();
        for (Property p : catalog) {
            // Only approved properties are publicly discoverable
            if (Constants.STATUS_APPROVED.equalsIgnoreCase(p.getStatus())) {
                if (citySearchStrategy.matches(p, criteria) && priceRangeSearchStrategy.matches(p, criteria)) {
                    results.add(p);
                }
            }
        }
        return results;
    }

    @Override
    public boolean addToFavourites(Property property) {
        if (property == null) {
            return false;
        }
        // Set automatically enforces deduplication
        return favourites.add(property);
    }

    @Override
    public boolean removeFromFavourites(Long propertyId) {
        if (propertyId == null) {
            return false;
        }
        return favourites.removeIf(p -> p.getId() != null && p.getId().equals(propertyId));
    }

    @Override
    public Set<Property> getFavourites() {
        return Collections.unmodifiableSet(favourites);
    }

    @Override
    public Set<String> getAvailableCities() {
        Set<String> cities = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Property p : catalog) {
            if (Constants.STATUS_APPROVED.equalsIgnoreCase(p.getStatus()) && p.getCity() != null) {
                cities.add(p.getCity().trim());
            }
        }
        return cities;
    }

    @Override
    public Optional<Property> findById(Long id) {
        if (id == null) return Optional.empty();
        for (Property p : catalog) {
            if (p.getId() != null && p.getId().equals(id)) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Property> getAllListings() {
        return Collections.unmodifiableList(catalog);
    }

    /**
     * Seeds sample verified properties directly into catalog (marked APPROVED) for search demonstrations.
     */
    public void seedApprovedProperty(Property property) {
        if (property != null) {
            if (property.getId() == null) {
                property.setId(idGenerator.incrementAndGet());
            }
            property.setStatus(Constants.STATUS_APPROVED);
            catalog.add(property);
        }
    }
}
