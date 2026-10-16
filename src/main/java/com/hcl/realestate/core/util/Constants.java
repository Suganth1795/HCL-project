package com.hcl.realestate.core.util;

/**
 * Global Constants for JFS-16 Real Estate Listing & Rental Management System.
 * Contains central definitions for system metadata, user roles, property types,
 * lifecycle statuses, business rules, and calendar day constants.
 */
public final class Constants {

    // --- Private Constructor to prevent instantiation ---
    private Constants() {
        throw new UnsupportedOperationException("Constants utility class cannot be instantiated.");
    }

    // --- System Metadata ---
    public static final String APP_NAME = "Real Estate Listing & Rental Management System";
    public static final String APP_VERSION = "1.0.0";
    public static final String DOMAIN = "Real Estate";

    // --- User Roles ---
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_OWNER = "OWNER";
    public static final String ROLE_SEEKER = "SEEKER";
    public static final String ROLE_TENANT = "TENANT";

    // --- Property Types ---
    public static final String TYPE_APARTMENT = "APARTMENT";
    public static final String TYPE_INDEPENDENT_HOUSE = "INDEPENDENT_HOUSE";
    public static final String TYPE_VILLA = "VILLA";
    public static final String TYPE_STUDIO = "STUDIO";
    public static final String TYPE_PG = "PG";

    // --- Furnishing Statuses ---
    public static final String FURNISHING_FULLY_FURNISHED = "FULLY_FURNISHED";
    public static final String FURNISHING_SEMI_FURNISHED = "SEMI_FURNISHED";
    public static final String FURNISHING_UNFURNISHED = "UNFURNISHED";

    // --- Property Listing Lifecycle Statuses ---
    public static final String STATUS_DRAFT = "DRAFT";
    public static final String STATUS_PENDING_REVIEW = "PENDING_REVIEW";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_DEACTIVATED = "DEACTIVATED";
    public static final String STATUS_RENTED = "RENTED";

    // --- Lease Lifecycle Statuses ---
    public static final String LEASE_ACTIVE = "ACTIVE";
    public static final String LEASE_EXPIRED = "EXPIRED";
    public static final String LEASE_TERMINATED = "TERMINATED";

    // --- Rent Due & Payment Statuses ---
    public static final String RENT_PENDING = "PENDING";
    public static final String RENT_PAID = "PAID";
    public static final String RENT_OVERDUE = "OVERDUE";

    // --- Site Visit Statuses ---
    public static final String VISIT_REQUESTED = "REQUESTED";
    public static final String VISIT_CONFIRMED = "CONFIRMED";
    public static final String VISIT_CANCELLED = "CANCELLED";
    public static final String VISIT_COMPLETED = "COMPLETED";

    // --- Business Validation Limits & Rules ---
    public static final double MIN_MONTHLY_RENT = 1000.00;
    public static final double MAX_MONTHLY_RENT = 1000000.00;
    public static final int MIN_SECURITY_DEPOSIT_MULTIPLIER = 1;
    public static final int DEFAULT_SECURITY_DEPOSIT_MULTIPLIER = 2;
    public static final int RENT_DUE_DAY_OF_MONTH = 5;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MIN_BHK = 1;
    public static final int MAX_BHK = 10;
    public static final String CURRENCY_SYMBOL = "INR";

    // --- Days of the Week Array (1 Week Representation) ---
    public static final String[] DAYS_OF_WEEK = {
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    };
}