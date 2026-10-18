package com.hcl.realestate.core.repository;

import com.hcl.realestate.core.model.Lease;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for Rental Lease contracts.
 * Extends CrudRepository with property and tenancy queries.
 */
public interface LeaseRepository extends CrudRepository<Lease, Long> {

    /**
     * Finds the active lease agreement associated with a given property ID.
     *
     * @param propertyId the property identifier
     * @return Optional containing the lease if present, or empty Optional
     */
    Optional<Lease> findByPropertyId(Long propertyId);

    /**
     * Finds all leases contracted by a specific tenant ID.
     *
     * @param tenantId the tenant user identifier
     * @return list of leases for the tenant
     */
    List<Lease> findByTenantId(Long tenantId);

    /**
     * Finds all leases contracted under a specific owner ID.
     *
     * @param ownerId the owner user identifier
     * @return list of leases for the owner
     */
    List<Lease> findByOwnerId(Long ownerId);
}
