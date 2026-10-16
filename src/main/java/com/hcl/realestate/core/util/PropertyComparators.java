package com.hcl.realestate.core.util;

import com.hcl.realestate.core.model.Property;

import java.util.Comparator;

/**
 * Utility class providing reusable Comparators for sorting Property listings.
 * Demonstrates:
 * - A1-COL-06: Comparator interface implementations
 * - Single-field sorting (by rent ascending and descending)
 * - Multi-field Comparator chaining (by city alphabetically, then by rent ascending, then by BHK descending)
 */
public final class PropertyComparators {

    private PropertyComparators() {
        // Prevent instantiation
    }

    /**
     * Comparator 1: Sorts properties by monthly rent ascending (cheapest first).
     */
    public static final Comparator<Property> BY_RENT_ASC =
            Comparator.comparingDouble(Property::getMonthlyRent);

    /**
     * Sorts properties by monthly rent descending (most premium first).
     */
    public static final Comparator<Property> BY_RENT_DESC =
            BY_RENT_ASC.reversed();

    /**
     * Comparator 2: Multi-field Comparator.
     * Sorts primarily by City (case-insensitive alphabetical),
     * then by Monthly Rent ascending,
     * and finally breaks ties by BHK count descending.
     */
    public static final Comparator<Property> BY_CITY_THEN_RENT =
            Comparator.comparing(Property::getCity, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                    .thenComparingDouble(Property::getMonthlyRent)
                    .thenComparing(Comparator.comparingInt(Property::getBhk).reversed());

    /**
     * Sorts properties by bedroom count (BHK) descending.
     */
    public static final Comparator<Property> BY_BHK_DESC =
            Comparator.comparingInt(Property::getBhk).reversed();
}
