package com.hcl.realestate.core.repository;

import com.hcl.realestate.core.model.Property;

import java.util.List;

/**
 * Domain repository interface for Property listings.
 * Extends generic CrudRepository and adds specialized query methods.
 */
public interface PropertyRepository extends CrudRepository<Property, Long> {

    /**
     * Finds properties located in the given city (case-insensitive).
     *
     * @param city the target city name
     * @return list of matching properties
     */
    List<Property> findByCity(String city);

    /**
     * Finds properties belonging to the specified owner ID.
     *
     * @param ownerId the owner's unique identifier
     * @return list of properties owned by the owner
     */
    List<Property> findByOwnerId(Long ownerId);

    /**
     * Finds properties matching a specific status (e.g. APPROVED, PENDING_REVIEW, RENTED).
     *
     * @param status the listing status
     * @return list of properties matching the status
     */
    List<Property> findByStatus(String status);

    /**
     * Finds properties with monthly rent falling within [minRent, maxRent].
     *
     * @param minRent minimum monthly rent
     * @param maxRent maximum monthly rent
     * @return list of properties within the budget range
     */
    List<Property> findByPriceRange(double minRent, double maxRent);
}
