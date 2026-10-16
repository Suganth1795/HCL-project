package com.hcl.realestate.core.strategy;

import com.hcl.realestate.core.dto.SearchCriteria;
import com.hcl.realestate.core.model.Property;

/**
 * Concrete SearchStrategy implementation that filters properties within a specified
 * monthly rent price range [minPrice, maxPrice].
 * Gracefully handles open-ended ranges (e.g. minPrice only or maxPrice only).
 */
public class PriceRangeSearchStrategy implements SearchStrategy {

    @Override
    public boolean matches(Property property, SearchCriteria criteria) {
        if (criteria == null || property == null) {
            return true;
        }

        double rent = property.getMonthlyRent();

        if (criteria.minPrice() != null && rent < criteria.minPrice()) {
            return false;
        }

        if (criteria.maxPrice() != null && rent > criteria.maxPrice()) {
            return false;
        }

        return true;
    }
}
