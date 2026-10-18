package com.hcl.realestate.core.repository;

import com.hcl.realestate.core.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for User accounts.
 * Extends CrudRepository with authentication and identity queries.
 */
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * Finds a user by email address (case-insensitive).
     *
     * @param email user email
     * @return Optional containing the user if found, or empty Optional
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds users assigned to a specific role.
     *
     * @param role role name (e.g. ROLE_OWNER, ROLE_SEEKER, ROLE_ADMIN)
     * @return list of matching users
     */
    List<User> findByRole(String role);
}
