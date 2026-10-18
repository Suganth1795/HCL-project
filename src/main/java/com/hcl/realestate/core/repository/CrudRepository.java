package com.hcl.realestate.core.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic CRUD repository interface defining persistent storage operations for entity T with identifier ID.
 * Demonstrates:
 * - Generics with type parameters &lt;T, ID&gt; (A1-GEN-01)
 * - Standard Repository Design Pattern
 * - Java Optional for null-safe lookups (A1-GEN-02)
 *
 * @param <T>  the domain entity type
 * @param <ID> the entity identifier type
 */
public interface CrudRepository<T, ID> {

    /**
     * Saves a given entity. If the entity is new (id is null or non-positive),
     * a unique identifier is generated and assigned before saving.
     *
     * @param entity the entity to save, must not be null
     * @return the saved entity instance with identifier populated
     */
    T save(T entity);

    /**
     * Retrieves an entity by its unique identifier.
     *
     * @param id the entity identifier
     * @return an Optional containing the matching entity, or Optional.empty() if not found
     */
    Optional<T> findById(ID id);

    /**
     * Returns all instances of the entity stored in the repository.
     *
     * @return an unmodifiable list of all stored entities
     */
    List<T> findAll();

    /**
     * Deletes the entity with the specified identifier.
     *
     * @param id the entity identifier
     * @return true if an entity was found and removed, false otherwise
     */
    boolean deleteById(ID id);

    /**
     * Returns whether an entity with the given identifier exists.
     *
     * @param id the entity identifier
     * @return true if an entity with the given identifier exists, false otherwise
     */
    boolean existsById(ID id);

    /**
     * Returns the total number of entities managed by this repository.
     *
     * @return the entity count
     */
    long count();

    /**
     * Deletes all entities managed by this repository.
     */
    void deleteAll();
}
