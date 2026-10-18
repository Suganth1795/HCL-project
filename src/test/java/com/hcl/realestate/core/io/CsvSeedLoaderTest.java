package com.hcl.realestate.core.io;

import com.hcl.realestate.core.model.Property;
import com.hcl.realestate.core.repository.InMemoryPropertyRepository;
import com.hcl.realestate.core.repository.PropertyRepository;
import com.hcl.realestate.core.util.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CSV Seed Loader Tests")
class CsvSeedLoaderTest {

    @Test
    @DisplayName("loadPropertiesFromResource parses classpath /data/properties.csv successfully")
    void testLoadPropertiesFromResource() throws IOException {
        List<Property> properties = CsvSeedLoader.loadPropertiesFromResource("/data/properties.csv");

        assertNotNull(properties);
        assertEquals(12, properties.size(), "Should load 12 properties from properties.csv");

        Property first = properties.get(0);
        assertEquals(101L, first.getId());
        assertEquals(1L, first.getOwnerId());
        assertEquals("Marina Bay Sea View", first.getTitle());
        assertEquals("Chennai", first.getCity());
        assertEquals("Besant Nagar", first.getLocality());
        assertEquals(3, first.getBhk());
        assertEquals(45000.0, first.getMonthlyRent());
        assertEquals(90000.0, first.getDepositAmount());
        assertEquals(Constants.STATUS_APPROVED, first.getStatus());
    }

    @Test
    @DisplayName("loadPropertiesIntoRepository populates PropertyRepository directly")
    void testLoadPropertiesIntoRepository() throws IOException {
        PropertyRepository repo = new InMemoryPropertyRepository();
        int loadedCount = CsvSeedLoader.loadPropertiesIntoRepository(repo, "/data/properties.csv");

        assertEquals(12, loadedCount);
        assertEquals(12, repo.count());

        List<Property> bangaloreListings = repo.findByCity("Bangalore");
        assertFalse(bangaloreListings.isEmpty(), "Should find Bangalore listings loaded from CSV");

        List<Property> luxuryListings = repo.findByPriceRange(80000.0, 150000.0);
        assertFalse(luxuryListings.isEmpty(), "Should find luxury tier listings");
    }

    @Test
    @DisplayName("loadPropertiesFromStream handles quoted fields, comments, and empty lines")
    void testLoadPropertiesFromStreamWithEdgeCases() throws IOException {
        String csvData = """
                # Sample properties comment line
                id,ownerId,title,description,propertyType,city,locality,bhk,monthlyRent,depositAmount,status
                
                201,10,"Sunny, Airy Flat","Beautiful view, 3-side open",APARTMENT,Bangalore,Whitefield,2,30000.0,60000.0,APPROVED
                202,11,"Quiet Studio","Near Metro, furnished",STUDIO,Delhi,Saket,1,16000.0,32000.0,APPROVED
                # Another comment
                """;

        InputStream stream = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));
        List<Property> result = CsvSeedLoader.loadPropertiesFromStream(stream);

        assertEquals(2, result.size());
        assertEquals("Sunny, Airy Flat", result.get(0).getTitle());
        assertEquals("Beautiful view, 3-side open", result.get(0).getDescription());
        assertEquals(202L, result.get(1).getId());
    }

    @Test
    @DisplayName("loadPropertiesFromStream gracefully ignores malformed rows")
    void testLoadPropertiesFromStreamIgnoresMalformedRows() throws IOException {
        String csvData = """
                id,ownerId,title,description,propertyType,city,locality,bhk,monthlyRent,depositAmount,status
                201,10,Valid Row,Desc,APARTMENT,Bangalore,Whitefield,2,30000.0,60000.0,APPROVED
                not_enough_columns,1,2,3
                202,11,Invalid Number,Desc,APARTMENT,Bangalore,Whitefield,INVALID_BHK,30000.0,60000.0,APPROVED
                203,12,Second Valid Row,Desc,APARTMENT,Chennai,Adyar,3,40000.0,80000.0,APPROVED
                """;

        InputStream stream = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));
        List<Property> result = CsvSeedLoader.loadPropertiesFromStream(stream);

        assertEquals(2, result.size(), "Should skip the 2 malformed rows and parse the 2 valid rows");
        assertEquals(201L, result.get(0).getId());
        assertEquals(203L, result.get(1).getId());
    }

    @Test
    @DisplayName("loadPropertiesFromResource throws FileNotFoundException for nonexistent resource")
    void testNonexistentResourceThrows() {
        assertThrows(FileNotFoundException.class, () ->
                CsvSeedLoader.loadPropertiesFromResource("/data/nonexistent_file.csv"));
    }

    @Test
    @DisplayName("loadPropertiesFromStream throws IllegalArgumentException for null stream")
    void testNullStreamThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                CsvSeedLoader.loadPropertiesFromStream(null));
    }
}
