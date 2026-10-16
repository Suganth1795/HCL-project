package com.hcl.realestate.core.dto;

/**
 * Data Transfer Object (DTO) modeled as an immutable Java 21 Record
 * representing property search parameters.
 *
 * @param city         target city filter (case-insensitive)
 * @param locality     target locality or neighborhood
 * @param minPrice     minimum monthly rent (inclusive)
 * @param maxPrice     maximum monthly rent (inclusive)
 * @param bhk          bedroom count filter
 * @param propertyType property category (e.g. APARTMENT, VILLA, INDEPENDENT_HOUSE)
 */
public record SearchCriteria(
        String city,
        String locality,
        Double minPrice,
        Double maxPrice,
        Integer bhk,
        String propertyType
) {

    /**
     * Creates an empty criteria with no filters applied.
     */
    public static SearchCriteria empty() {
        return new SearchCriteria(null, null, null, null, null, null);
    }

    /**
     * Factory method for filtering by city only.
     */
    public static SearchCriteria byCity(String city) {
        return new SearchCriteria(city, null, null, null, null, null);
    }

    /**
     * Factory method for filtering by price range only.
     */
    public static SearchCriteria byPriceRange(Double minPrice, Double maxPrice) {
        return new SearchCriteria(null, null, minPrice, maxPrice, null, null);
    }

    /**
     * Factory method for filtering by both city and price range.
     */
    public static SearchCriteria byCityAndPrice(String city, Double minPrice, Double maxPrice) {
        return new SearchCriteria(city, null, minPrice, maxPrice, null, null);
    }
}
