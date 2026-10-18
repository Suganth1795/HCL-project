package com.hcl.realestate.core.io;

import com.hcl.realestate.core.model.Property;
import com.hcl.realestate.core.repository.PropertyRepository;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility for parsing and loading Property seed data from CSV sources.
 * Demonstrates:
 * - Modern Java I/O with BufferedReader and try-with-resources
 * - UTF-8 character encoding handling
 * - Classpath resource stream loading and local file system path parsing
 * - Batch repository persistence via PropertyRepository
 */
public class CsvSeedLoader {

    private CsvSeedLoader() {
        // Utility class
    }

    /**
     * Loads property records from an input stream containing CSV data.
     *
     * @param inputStream the open input stream, must not be null
     * @return unmodifiable list of parsed Property entities
     * @throws IOException if an I/O error occurs reading the stream
     */
    public static List<Property> loadPropertiesFromStream(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null.");
        }

        List<Property> properties = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Skip header row
                if (isFirstLine && (line.toLowerCase().startsWith("id,") || line.toLowerCase().contains("title"))) {
                    isFirstLine = false;
                    continue;
                }
                isFirstLine = false;

                Property property = parseCsvLine(line);
                if (property != null) {
                    properties.add(property);
                }
            }
        }

        return Collections.unmodifiableList(properties);
    }

    /**
     * Loads property records from a classpath resource path (e.g. "/data/properties.csv").
     *
     * @param resourcePath classpath relative or absolute resource path
     * @return unmodifiable list of parsed Property entities
     * @throws IOException if resource is not found or fails to read
     */
    public static List<Property> loadPropertiesFromResource(String resourcePath) throws IOException {
        if (resourcePath == null || resourcePath.isBlank()) {
            throw new IllegalArgumentException("Resource path cannot be blank.");
        }

        String normalizedPath = resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath;
        InputStream is = CsvSeedLoader.class.getResourceAsStream(normalizedPath);
        if (is == null) {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(normalizedPath.substring(1));
        }

        if (is == null) {
            throw new FileNotFoundException("Classpath CSV resource not found: " + resourcePath);
        }

        return loadPropertiesFromStream(is);
    }

    /**
     * Loads property records from an external filesystem path.
     *
     * @param filePath path to the CSV file
     * @return unmodifiable list of parsed Property entities
     * @throws IOException if file does not exist or cannot be read
     */
    public static List<Property> loadPropertiesFromFile(Path filePath) throws IOException {
        if (filePath == null || !Files.exists(filePath)) {
            throw new FileNotFoundException("File does not exist: " + filePath);
        }
        try (InputStream is = Files.newInputStream(filePath)) {
            return loadPropertiesFromStream(is);
        }
    }

    /**
     * Loads properties from a classpath CSV resource directly into a PropertyRepository.
     *
     * @param repository   target property repository
     * @param resourcePath classpath resource path
     * @return count of successfully loaded and saved properties
     * @throws IOException if an error occurs reading the resource
     */
    public static int loadPropertiesIntoRepository(PropertyRepository repository, String resourcePath) throws IOException {
        if (repository == null) {
            throw new IllegalArgumentException("Repository cannot be null.");
        }
        List<Property> properties = loadPropertiesFromResource(resourcePath);
        for (Property p : properties) {
            repository.save(p);
        }
        return properties.size();
    }

    /**
     * Loads properties from an InputStream directly into a PropertyRepository.
     *
     * @param repository  target property repository
     * @param inputStream input stream containing CSV content
     * @return count of successfully loaded and saved properties
     * @throws IOException if an error occurs reading the stream
     */
    public static int loadPropertiesIntoRepository(PropertyRepository repository, InputStream inputStream) throws IOException {
        if (repository == null) {
            throw new IllegalArgumentException("Repository cannot be null.");
        }
        List<Property> properties = loadPropertiesFromStream(inputStream);
        for (Property p : properties) {
            repository.save(p);
        }
        return properties.size();
    }

    /**
     * Parses a single comma-separated line into a Property domain instance.
     * Expected CSV Columns:
     * 0: id
     * 1: ownerId
     * 2: title
     * 3: description
     * 4: propertyType
     * 5: city
     * 6: locality
     * 7: bhk
     * 8: monthlyRent
     * 9: depositAmount
     * 10: status
     */
    private static Property parseCsvLine(String line) {
        String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        if (tokens.length < 11) {
            return null; // Skip malformed rows
        }

        try {
            Long id = Long.parseLong(clean(tokens[0]));
            Long ownerId = Long.parseLong(clean(tokens[1]));
            String title = clean(tokens[2]);
            String description = clean(tokens[3]);
            String propertyType = clean(tokens[4]);
            String city = clean(tokens[5]);
            String locality = clean(tokens[6]);
            int bhk = Integer.parseInt(clean(tokens[7]));
            double monthlyRent = Double.parseDouble(clean(tokens[8]));
            double depositAmount = Double.parseDouble(clean(tokens[9]));
            String status = clean(tokens[10]);

            return new Property(id, ownerId, title, description, propertyType, city, locality, bhk, monthlyRent, depositAmount, status);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return null; // Skip rows with invalid numeric formatting
        }
    }

    private static String clean(String token) {
        if (token == null) return "";
        String trimmed = token.trim();
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length() >= 2) {
            return trimmed.substring(1, trimmed.length() - 1).trim();
        }
        return trimmed;
    }
}
