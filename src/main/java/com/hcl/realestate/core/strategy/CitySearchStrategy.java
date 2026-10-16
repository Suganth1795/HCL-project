package com.hcl.realestate.core.strategy;

import com.hcl.realestate.core.dto.SearchCriteria;
import com.hcl.realestate.core.model.Property;

/**
 * Concrete SearchStrategy implementation that filters properties by city name.
 * Performs null-safe, case-insensitive string matching.
 */
public class CitySearchStrategy implements SearchStrategy {

    @Override
    public boolean matches(Property property, SearchCriteria criteria) {
        if (criteria == null || criteria.city() == null || criteria.city().isBlank()) {
            return true; // No city filter requested -> match all
        }
        if (property == null || property.getCity() == null) {
            return false;
        }
        return property.getCity().trim().equalsIgnoreCase(criteria.city().trim());
    }
}
