package com.hcl.realestate.core.model;

import com.hcl.realestate.core.util.Constants;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a registered User on the Real Estate platform.
 * Demonstrates:
 * - Inheritance (extends BaseEntity)
 * - Encapsulation (private fields, controlled getters and setters)
 * - Role-Based Access Control and Role Hierarchy integration
 * - Object identity (equals and hashCode based on ID and email)
 */
public class User extends BaseEntity {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String role;
    private final Set<Role> roles = new HashSet<>();

    /**
     * Default constructor.
     */
    public User() {
        super();
        this.role = Constants.ROLE_SEEKER;
        this.roles.add(Role.ROLE_SEEKER);
    }

    /**
     * Parameterized constructor with String role.
     */
    public User(Long id, String firstName, String lastName, String email, String phone, String role) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.role = (role != null) ? role : Constants.ROLE_SEEKER;
        this.roles.add(Role.fromString(this.role));
    }

    /**
     * Parameterized constructor with Role enum set.
     */
    public User(Long id, String firstName, String lastName, String email, String phone, Set<Role> assignedRoles) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        if (assignedRoles != null && !assignedRoles.isEmpty()) {
            this.roles.addAll(assignedRoles);
            this.role = assignedRoles.iterator().next().name();
        } else {
            this.role = Constants.ROLE_SEEKER;
            this.roles.add(Role.ROLE_SEEKER);
        }
    }

    // --- Getters and Setters (Encapsulation) ---

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
        if (role != null) {
            this.roles.clear();
            this.roles.add(Role.fromString(role));
        }
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public void addRole(Role role) {
        if (role != null) {
            this.roles.add(role);
            this.role = role.name();
        }
    }

    public boolean hasRole(Role role) {
        return this.roles.contains(role);
    }

    /**
     * Checks if this user has access to a required role under role hierarchy rules.
     *
     * @param requiredRole the required role
     * @return true if any of the user's roles imply the required role
     */
    public boolean hasAccess(Role requiredRole) {
        return RoleHierarchy.hasAccess(this.roles, requiredRole);
    }

    /**
     * Helper method to get the full formatted name.
     */
    public String getFullName() {
        if (firstName == null && lastName == null) return "Unknown";
        if (firstName == null) return lastName;
        if (lastName == null) return firstName;
        return firstName.trim() + " " + lastName.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        if (getId() != null && user.getId() != null) {
            return Objects.equals(getId(), user.getId());
        }
        return email != null && email.equalsIgnoreCase(user.email);
    }

    @Override
    public int hashCode() {
        return getId() != null ? Objects.hash(getId()) : Objects.hash(email != null ? email.toLowerCase() : null);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", name='" + getFullName() + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", role='" + role + '\'' +
                ", roles=" + roles +
                '}';
    }
}