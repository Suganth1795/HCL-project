package com.hcl.realestate.core.strategy;

import com.hcl.realestate.core.dto.SearchCriteria;
import com.hcl.realestate.core.model.Property;
import com.hcl.realestate.core.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Task 5: Strategy Pattern Search Tests")
class SearchStrategyTest {

    private Property chennaiProperty;
    private Property bangaloreProperty;
    private Property hyderabadProperty;

    @BeforeEach
    void setUp() {
        chennaiProperty = new Property(1L, 101L, "Ocean View Apartment", "Near Marina Beach",
                "APARTMENT", "Chennai", "Besant Nagar", 3, 35000.0, 150000.0, Constants.STATUS_APPROVED);

        bangaloreProperty = new Property(2L, 102L, "Tech Park Studio", "Near Whitefield",
                "STUDIO", "Bangalore", "Whitefield", 1, 18000.0, 80000.0, Constants.STATUS_APPROVED);

        hyderabadProperty = new Property(3L, 103L, "Gated Community Villa", "Near Hitec City",
                "VILLA", "Hyderabad", "Gachibowli", 4, 75000.0, 350000.0, Constants.STATUS_APPROVED);
    }

    @Test
    @DisplayName("CitySearchStrategy should match exact city case-insensitively and ignore blank criteria")
    void testCitySearchStrategy() {
        SearchStrategy cityStrategy = new CitySearchStrategy();

        // Exact match
        assertTrue(cityStrategy.matches(chennaiProperty, SearchCriteria.byCity("Chennai")));
        // Case-insensitive match
        assertTrue(cityStrategy.matches(chennaiProperty, SearchCriteria.byCity("chennai")));
        assertTrue(cityStrategy.matches(bangaloreProperty, SearchCriteria.byCity("BANGALORE")));

        // Mismatched city
        assertFalse(cityStrategy.matches(chennaiProperty, SearchCriteria.byCity("Bangalore")));

        // Blank or null criteria city should match all properties
        assertTrue(cityStrategy.matches(chennaiProperty, SearchCriteria.empty()));
        assertTrue(cityStrategy.matches(chennaiProperty, SearchCriteria.byCity("")));
        assertTrue(cityStrategy.matches(chennaiProperty, null));
    }

    @Test
    @DisplayName("PriceRangeSearchStrategy should match properties within budget bounds")
    void testPriceRangeSearchStrategy() {
        SearchStrategy priceStrategy = new PriceRangeSearchStrategy();

        // Budget: 15,000 to 40,000
        SearchCriteria midBudget = SearchCriteria.byPriceRange(15000.0, 40000.0);
        assertTrue(priceStrategy.matches(bangaloreProperty, midBudget), "18k is within [15k, 40k]");
        assertTrue(priceStrategy.matches(chennaiProperty, midBudget), "35k is within [15k, 40k]");
        assertFalse(priceStrategy.matches(hyderabadProperty, midBudget), "75k exceeds max budget 40k");

        // Min-only budget: >= 30,000
        SearchCriteria minBudget = SearchCriteria.byPriceRange(30000.0, null);
        assertTrue(priceStrategy.matches(chennaiProperty, minBudget));
        assertTrue(priceStrategy.matches(hyderabadProperty, minBudget));
        assertFalse(priceStrategy.matches(bangaloreProperty, minBudget));

        // Max-only budget: <= 25,000
        SearchCriteria maxBudget = SearchCriteria.byPriceRange(null, 25000.0);
        assertTrue(priceStrategy.matches(bangaloreProperty, maxBudget));
        assertFalse(priceStrategy.matches(chennaiProperty, maxBudget));
        assertFalse(priceStrategy.matches(hyderabadProperty, maxBudget));
    }

    @Test
    @DisplayName("Combining strategies dynamically to filter a collection of properties")
    void testDynamicStrategyComposition() {
        List<Property> propertyCatalog = List.of(chennaiProperty, bangaloreProperty, hyderabadProperty);

        SearchStrategy cityFilter = new CitySearchStrategy();
        SearchStrategy priceFilter = new PriceRangeSearchStrategy();

        // Search for Chennai properties under 40,000
        SearchCriteria criteria = SearchCriteria.byCityAndPrice("chennai", 20000.0, 40000.0);

        List<Property> matched = new ArrayList<>();
        for (Property p : propertyCatalog) {
            if (cityFilter.matches(p, criteria) && priceFilter.matches(p, criteria)) {
                matched.add(p);
            }
        }

        assertEquals(1, matched.size());
        assertEquals("Ocean View Apartment", matched.get(0).getTitle());
    }
}
