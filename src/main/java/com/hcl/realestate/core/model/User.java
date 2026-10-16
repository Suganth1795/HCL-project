package com.hcl.realestate.core.model;

import com.hcl.realestate.core.util.Constants;

import java.util.Objects;

/**
 * Represents a registered User on the Real Estate platform.
 * Supports roles: OWNER, SEEKER, TENANT, ADMIN.
 * Demonstrates:
 * - Encapsulation (private fields, controlled getters and setters)
 * - Constructors (default and parameterized)
 * - Object identity (equals and hashCode based on ID and email)
 */
public class User {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String role;

    /**
     * Default constructor.
     */
    public User() {
        this.role = Constants.ROLE_SEEKER;
    }

    /**
     * Parameterized constructor.
     */
    public User(Long id, String firstName, String lastName, String email, String phone, String role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.role = (role != null) ? role : Constants.ROLE_SEEKER;
    }

    // --- Getters and Setters (Encapsulation) ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
        return Objects.equals(id, user.id) ||
                (email != null && email.equalsIgnoreCase(user.email));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email != null ? email.toLowerCase() : null);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + getFullName() + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}