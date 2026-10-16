package com.hcl.realestate.core.model;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Service/Utility class modeling the Role Hierarchy for Role-Based Access Control (RBAC).
 * Defines role reachability and inheritance:
 * - ROLE_ADMIN &gt; ROLE_OWNER, ROLE_TENANT, ROLE_SEEKER
 * - ROLE_OWNER &gt; ROLE_SEEKER
 * - ROLE_TENANT &gt; ROLE_SEEKER
 * - ROLE_SEEKER (base level)
 */
public final class RoleHierarchy {

    private static final Map<Role, Set<Role>> HIERARCHY_MAP = new EnumMap<>(Role.class);

    static {
        // ADMIN inherits all roles
        HIERARCHY_MAP.put(Role.ROLE_ADMIN,
                Collections.unmodifiableSet(EnumSet.of(Role.ROLE_ADMIN, Role.ROLE_OWNER, Role.ROLE_TENANT, Role.ROLE_SEEKER)));

        // OWNER can perform seeker activities (search, view, enquire)
        HIERARCHY_MAP.put(Role.ROLE_OWNER,
                Collections.unmodifiableSet(EnumSet.of(Role.ROLE_OWNER, Role.ROLE_SEEKER)));

        // TENANT can perform seeker activities
        HIERARCHY_MAP.put(Role.ROLE_TENANT,
                Collections.unmodifiableSet(EnumSet.of(Role.ROLE_TENANT, Role.ROLE_SEEKER)));

        // SEEKER has base seeker access
        HIERARCHY_MAP.put(Role.ROLE_SEEKER,
                Collections.unmodifiableSet(EnumSet.of(Role.ROLE_SEEKER)));
    }

    private RoleHierarchy() {
        // Utility / static class
    }

    /**
     * Retrieves all reachable roles implied by the given role.
     *
     * @param role the primary role
     * @return an unmodifiable Set of reachable roles
     */
    public static Set<Role> getReachableRoles(Role role) {
        if (role == null) {
            return Collections.emptySet();
        }
        return HIERARCHY_MAP.getOrDefault(role, EnumSet.of(role));
    }

    /**
     * Retrieves all reachable roles implied by a collection of assigned roles.
     *
     * @param roles collection of roles assigned to a user
     * @return union of all reachable roles
     */
    public static Set<Role> getReachableRoles(Collection<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptySet();
        }
        EnumSet<Role> reachable = EnumSet.noneOf(Role.class);
        for (Role r : roles) {
            if (r != null) {
                reachable.addAll(getReachableRoles(r));
            }
        }
        return Collections.unmodifiableSet(reachable);
    }

    /**
     * Checks if a single role satisfies the required role under hierarchy rules.
     *
     * @param assignedRole the user's assigned role
     * @param requiredRole the role required to perform an action
     * @return true if assignedRole implies requiredRole
     */
    public static boolean hasAccess(Role assignedRole, Role requiredRole) {
        if (assignedRole == null || requiredRole == null) {
            return false;
        }
        return getReachableRoles(assignedRole).contains(requiredRole);
    }

    /**
     * Checks if any of the assigned roles satisfy the required role under hierarchy rules.
     *
     * @param assignedRoles collection of assigned roles
     * @param requiredRole  the role required
     * @return true if at least one assigned role implies requiredRole
     */
    public static boolean hasAccess(Collection<Role> assignedRoles, Role requiredRole) {
        if (assignedRoles == null || requiredRole == null) {
            return false;
        }
        return getReachableRoles(assignedRoles).contains(requiredRole);
    }
}
