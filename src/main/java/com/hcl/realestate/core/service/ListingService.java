package com.hcl.realestate.core.service;

import com.hcl.realestate.core.dto.SearchCriteria;
import com.hcl.realestate.core.exception.ListingValidationException;
import com.hcl.realestate.core.model.Property;
import com.hcl.realestate.core.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service interface defining operations for Functional Requirements FR1, FR2, and FR3.
 * Demonstrates:
 * - List (A1-COL-01) for catalog and search results
 * - Set (A1-COL-02) for deduplicated seeker favourites
 * - Queue (A1-COL-04) for FIFO admin approval workflow
 */
public interface ListingService {

    // --- FR1: Owner Listing Management ---

    /**
     * Creates and validates a new listing, storing it in the catalog and enqueueing for review.
     *
     * @param property property to create
     * @return created property with assigned ID
     * @throws ListingValidationException if validation invariants fail
     */
    Property createListing(Property property) throws ListingValidationException;

    /**
     * Retrieves all listings belonging to a specific owner.
     *
     * @param ownerId owner's unique identifier
     * @return List of matching properties
     */
    List<Property> getListingsByOwner(Long ownerId);

    /**
     * Deactivates a property listing after verifying ownership authorization.
     *
     * @param propertyId property ID to deactivate
     * @param actor      user requesting deactivation (must be owner or admin)
     * @return updated property
     */
    Property deactivateListing(Long propertyId, User actor);

    // --- FR2: Admin Listing Moderation (Queue) ---

    /**
     * Approves the property at the head of the pending queue, making it searchable.
     *
     * @param propertyId property ID to approve
     * @return approved property
     */
    Property approveListing(Long propertyId);

    /**
     * Rejects the property with an explanatory feedback message.
     *
     * @param propertyId property ID to reject
     * @param reason     rejection reason
     * @return rejected property
     */
    Property rejectListing(Long propertyId, String reason);

    /**
     * Inspects the next pending property in the FIFO approval queue.
     *
     * @return Optional containing the next property
     */
    Optional<Property> getNextPendingForModeration();

    /**
     * Gets the number of items currently waiting in the moderation queue.
     *
     * @return queue size
     */
    int getPendingQueueSize();

    // --- FR3: Seeker Dynamic Property Search & Favourites ---

    /**
     * Searches for approved properties matching the provided dynamic criteria.
     *
     * @param criteria search filter parameters
     * @return List of matching approved properties
     */
    List<Property> search(SearchCriteria criteria);

    /**
     * Adds a property to the seeker's favourites collection (Set ensures deduplication).
     *
     * @param property property to bookmark
     * @return true if added; false if already in favourites
     */
    boolean addToFavourites(Property property);

    /**
     * Removes a property from favourites.
     *
     * @param propertyId property ID to remove
     * @return true if removed; false if not found
     */
    boolean removeFromFavourites(Long propertyId);

    /**
     * Retrieves all currently favourited properties.
     *
     * @return Set of bookmarked properties
     */
    Set<Property> getFavourites();

    /**
     * Retrieves all distinct cities where approved properties exist.
     *
     * @return Set of unique city names
     */
    Set<String> getAvailableCities();

    /**
     * Finds a property by its ID.
     *
     * @param id property ID
     * @return Optional containing the property
     */
    Optional<Property> findById(Long id);

    /**
     * Returns all properties in the system catalog.
     *
     * @return complete catalog List
     */
    List<Property> getAllListings();
}
