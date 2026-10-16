package com.hcl.realestate.core.util;

import com.hcl.realestate.core.model.Property;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Task 8: Property Comparators Tests")
class PropertyComparatorsTest {

    private Property p1;
    private Property p2;
    private Property p3;
    private Property p4;

    @BeforeEach
    void setUp() {
        p1 = new Property(1L, 10L, "Alpha Flat", "Desc", "APARTMENT", "Chennai", "Loc", 2, 35000.0, 70000.0, Constants.STATUS_APPROVED);
        p2 = new Property(2L, 10L, "Beta Studio", "Desc", "STUDIO", "Bangalore", "Loc", 1, 18000.0, 36000.0, Constants.STATUS_APPROVED);
        p3 = new Property(3L, 10L, "Gamma Villa", "Desc", "VILLA", "Bangalore", "Loc", 4, 85000.0, 250000.0, Constants.STATUS_APPROVED);
        p4 = new Property(4L, 10L, "Delta House", "Desc", "HOUSE", "Chennai", "Loc", 3, 35000.0, 70000.0, Constants.STATUS_APPROVED);
    }

    @Test
    @DisplayName("BY_RENT_ASC sorts properties from cheapest to most expensive")
    void testByRentAscending() {
        List<Property> list = new ArrayList<>(List.of(p1, p2, p3));
        list.sort(PropertyComparators.BY_RENT_ASC);

        assertEquals(18000.0, list.get(0).getMonthlyRent()); // Beta Studio (18k)
        assertEquals(35000.0, list.get(1).getMonthlyRent()); // Alpha Flat (35k)
        assertEquals(85000.0, list.get(2).getMonthlyRent()); // Gamma Villa (85k)
    }

    @Test
    @DisplayName("BY_RENT_DESC sorts properties from most expensive to cheapest")
    void testByRentDescending() {
        List<Property> list = new ArrayList<>(List.of(p1, p2, p3));
        list.sort(PropertyComparators.BY_RENT_DESC);

        assertEquals(85000.0, list.get(0).getMonthlyRent());
        assertEquals(35000.0, list.get(1).getMonthlyRent());
        assertEquals(18000.0, list.get(2).getMonthlyRent());
    }

    @Test
    @DisplayName("BY_CITY_THEN_RENT sorts by City alphabetically, then by Rent, then by BHK descending")
    void testByCityThenRent() {
        List<Property> list = new ArrayList<>(List.of(p1, p2, p3, p4));
        list.sort(PropertyComparators.BY_CITY_THEN_RENT);

        // Bangalore properties first (alphabetical):
        // p2 (18k, 1 BHK) before p3 (85k, 4 BHK)
        assertEquals("Bangalore", list.get(0).getCity());
        assertEquals("Beta Studio", list.get(0).getTitle());
        assertEquals("Bangalore", list.get(1).getCity());
        assertEquals("Gamma Villa", list.get(1).getTitle());

        // Chennai properties next (alphabetical):
        // p1 and p4 both have 35k rent; p4 has 3 BHK, p1 has 2 BHK -> p4 comes before p1 (BHK desc)
        assertEquals("Chennai", list.get(2).getCity());
        assertEquals("Delta House", list.get(2).getTitle());
        assertEquals("Chennai", list.get(3).getCity());
        assertEquals("Alpha Flat", list.get(3).getTitle());
    }

    @Test
    @DisplayName("BY_BHK_DESC sorts properties by bedroom count descending")
    void testByBhkDescending() {
        List<Property> list = new ArrayList<>(List.of(p1, p2, p3));
        list.sort(PropertyComparators.BY_BHK_DESC);

        assertEquals(4, list.get(0).getBhk()); // Gamma Villa (4 BHK)
        assertEquals(2, list.get(1).getBhk()); // Alpha Flat (2 BHK)
        assertEquals(1, list.get(2).getBhk()); // Beta Studio (1 BHK)
    }
}
