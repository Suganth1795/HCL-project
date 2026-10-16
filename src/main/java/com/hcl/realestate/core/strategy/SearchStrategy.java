package com.hcl.realestate.core.strategy;

import com.hcl.realestate.core.dto.SearchCriteria;
import com.hcl.realestate.core.model.Property;

/**
 * Strategy interface demonstrating the Strategy Pattern (A1-DSG-06)
 * and the Open/Closed Principle (A1-DSG-02).
 * Enables plugging in dynamic property search algorithms without modifying callers.
 */
@FunctionalInterface
public interface SearchStrategy {

    /**
     * Evaluates whether a given property satisfies the search criteria under this strategy.
     *
     * @param property the property entity to evaluate
     * @param criteria search criteria containing filter parameters
     * @return true if the property matches the strategy rule; false otherwise
     */
    boolean matches(Property property, SearchCriteria criteria);
}
