package com.hcl.realestate.core.app;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Console Menu Application Tests")
class ConsoleMenuAppTest {

    @Test
    @DisplayName("Option 0 terminates the menu loop cleanly")
    void testExitOptionTerminatesLoop() {
        ConsoleMenuApp app = new ConsoleMenuApp();
        String input = "0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        assertTrue(app.isRunning(), "App should initially be running");
        app.start(scanner);
        assertFalse(app.isRunning(), "App should terminate after option 0");
    }

    @Test
    @DisplayName("Option 9 prints 1-week sample data and sorted city report")
    void testSampleDataOptionAndSortedReport() {
        ConsoleMenuApp app = new ConsoleMenuApp();
        String input = "9\n\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            app.start(scanner);
            String output = outContent.toString();
            assertTrue(output.contains("1 WEEK SAMPLE TRANSACTION DATA"), "Should display sample data header");
            assertTrue(output.contains("Prestige Tech Park"), "Should display Bangalore property listing");
            assertTrue(output.contains("Sea Breeze"), "Should display Chennai property listing");
            assertTrue(output.contains("Total 7-Day Rent Dues Collected"), "Should display aggregate summary");
            assertTrue(output.contains("REAL ESTATE VERIFIED PROPERTIES REPORT"), "Should display sorted city report");
            assertTrue(output.contains("OVERALL PORTFOLIO"), "Should display overall portfolio summary");
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Menu displays all 8 Functional Requirements (FR1 to FR8)")
    void testMenuContainsAllFunctionalRequirements() {
        ConsoleMenuApp app = new ConsoleMenuApp();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            app.printMenu();
            String output = outContent.toString();
            assertTrue(output.contains("FR1: Owner Listing Management"));
            assertTrue(output.contains("FR2: Admin Listing Moderation"));
            assertTrue(output.contains("FR3: Seeker Dynamic Property Search"));
            assertTrue(output.contains("FR4: Seeker Favourites & Shortlists"));
            assertTrue(output.contains("FR5: Site Visits & Inquiries"));
            assertTrue(output.contains("FR6: Lease Agreement Creation"));
            assertTrue(output.contains("FR7: Monthly Rent Due Engine"));
            assertTrue(output.contains("FR8: Tenant Portal"));
            assertTrue(output.contains("Exit System"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Option 1 executes Business Rule 1 & 2A enforcement and recovers gracefully")
    void testFR1OptionEnforcesRulesAndRecovers() {
        ConsoleMenuApp app = new ConsoleMenuApp();
        String input = "1\n\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            app.start(scanner);
            String output = outContent.toString();
            assertTrue(output.contains("[Enforcing Business Rule 1: Financial & Invariant Validation]"));
            assertTrue(output.contains("[PASS] Valid Property passed"));
            assertTrue(output.contains("[RECOVERED] Gracefully caught ListingValidationException"));
            assertTrue(output.contains("[PASS] Owner verified for property modification"));
            assertTrue(output.contains("[RECOVERED] Gracefully caught UnauthorizedAccessException"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Option 4 displays Seeker Favourites mapped per User ID")
    void testFR4OptionDisplaysUserFavouritesMap() {
        ConsoleMenuApp app = new ConsoleMenuApp();
        String input = "4\n\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            app.start(scanner);
            String output = outContent.toString();
            assertTrue(output.contains("[FR4] SEEKER FAVOURITES & SHORTLISTS"));
            assertTrue(output.contains("Seeker ID 10 Favourites"));
            assertTrue(output.contains("Total Users with Active Shortlists"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Option 5 executes Inquiry and Site Visit flows mapped per Property ID")
    void testFR5OptionExecutesEngagementFlows() {
        ConsoleMenuApp app = new ConsoleMenuApp();
        String input = "5\n\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            app.start(scanner);
            String output = outContent.toString();
            assertTrue(output.contains("[FR5] SITE VISITS & DIRECT OWNER ENQUIRIES"));
            assertTrue(output.contains("Enquiry Flow"));
            assertTrue(output.contains("Owner responded"));
            assertTrue(output.contains("Site Visit Flow"));
            assertTrue(output.contains("Owner confirmed visit"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Option 6 executes Business Rule 2B state transition check and creates digital lease")
    void testFR6OptionEnforcesStateTransitionAndCreatesLease() {
        ConsoleMenuApp app = new ConsoleMenuApp();
        String input = "6\n\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            app.start(scanner);
            String output = outContent.toString();
            assertTrue(output.contains("[Enforcing Business Rule 2B: Valid State Transition for Lease Creation]"));
            assertTrue(output.contains("[PASS] Lease permitted for APPROVED property"));
            assertTrue(output.contains("[RECOVERED] Multi-catch caught domain exception"));
            assertTrue(output.contains("Creating & Registering Active Lease Contract"));
            assertTrue(output.contains("Total Active Leases in System Map"));
        } finally {
            System.setOut(originalOut);
        }
    }
}