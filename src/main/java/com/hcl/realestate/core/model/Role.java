package com.hcl.realestate.core.model;

/**
 * Enumeration representing user security and operational roles in the Real Estate platform.
 * Supports hierarchical authority ordering.
 */
public enum Role {

    ROLE_ADMIN("Platform Administrator", 4),
    ROLE_OWNER("Property Owner / Landlord", 3),
    ROLE_TENANT("Active Tenant", 2),
    ROLE_SEEKER("Property Seeker", 1);

    private final String description;
    private final int authorityLevel;

    Role(String description, int authorityLevel) {
        this.description = description;
        this.authorityLevel = authorityLevel;
    }

    public String getDescription() {
        return description;
    }

    public int getAuthorityLevel() {
        return authorityLevel;
    }

    /**
     * Checks if this role has equal or higher authority level compared to another role.
     *
     * @param other role to compare against
     * @return true if this role's authority level is greater than or equal to other
     */
    public boolean hasHigherOrEqualAuthority(Role other) {
        if (other == null) return false;
        return this.authorityLevel >= other.authorityLevel;
    }

    /**
     * Parse role string safely into Role enum.
     *
     * @param roleStr role name or shorthand (e.g. "ROLE_ADMIN", "ADMIN")
     * @return matched Role or ROLE_SEEKER as default
     */
    public static Role fromString(String roleStr) {
        if (roleStr == null || roleStr.isBlank()) {
            return ROLE_SEEKER;
        }
        String normalized = roleStr.trim().toUpperCase();
        if (!normalized.startsWith("ROLE_")) {
            normalized = "ROLE_" + normalized;
        }
        for (Role r : values()) {
            if (r.name().equals(normalized)) {
                return r;
            }
        }
        return ROLE_SEEKER;
    }
}
