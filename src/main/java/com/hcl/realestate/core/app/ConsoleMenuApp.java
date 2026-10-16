package com.hcl.realestate.core.app;

import com.hcl.realestate.core.dto.SearchCriteria;
import com.hcl.realestate.core.exception.InvalidStateTransitionException;
import com.hcl.realestate.core.exception.ListingValidationException;
import com.hcl.realestate.core.exception.PropertyNotFoundException;
import com.hcl.realestate.core.exception.UnauthorizedAccessException;
import com.hcl.realestate.core.model.Property;
import com.hcl.realestate.core.model.User;
import com.hcl.realestate.core.service.ListingServiceImpl;
import com.hcl.realestate.core.util.Constants;
import com.hcl.realestate.core.util.SampleArrayData;
import com.hcl.realestate.core.validation.BusinessRuleEnforcer;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

/**
 * Interactive Console Menu Application for JFS-16 Real Estate Listing & Rental Management.
 * Demonstrates:
 * - One dedicated option per Functional Requirement (FR1 through FR8)
 * - Collections: List (A1-COL-01), Set (A1-COL-02), and Queue (A1-COL-04)
 * - FR1: Owner listing management with List catalog
 * - FR2: Admin listing moderation with FIFO Queue
 * - FR3: Seeker dynamic search (List) and deduplicated favourites (Set)
 * - Custom Checked Exception handling (A1-EX-01)
 * - Custom Unchecked Exception handling and Multi-catch (A1-EX-02, A1-EX-03)
 * - Try-with-resources (A1-EX-04)
 */
public class ConsoleMenuApp {

    private boolean running = true;
    private final ListingServiceImpl listingService;

    public ConsoleMenuApp() {
        this.listingService = new ListingServiceImpl();
        seedInitialData();
    }

    public ConsoleMenuApp(ListingServiceImpl listingService) {
        this.listingService = (listingService != null) ? listingService : new ListingServiceImpl();
        seedInitialData();
    }

    /**
     * Seeds initial catalog data with sample properties across cities and statuses.
     */
    private void seedInitialData() {
        // Seed verified approved listings for search
        listingService.seedApprovedProperty(new Property(101L, 1L, "Marina Bay 3BHK", "Luxury sea view apartment",
                "APARTMENT", "Chennai", "Besant Nagar", 3, 45000.0, 100000.0, Constants.STATUS_APPROVED));
        listingService.seedApprovedProperty(new Property(102L, 2L, "Prestige Tech Park Studio", "Near IT corridor",
                "STUDIO", "Bangalore", "Whitefield", 1, 18000.0, 50000.0, Constants.STATUS_APPROVED));
        listingService.seedApprovedProperty(new Property(103L, 1L, "Greenwood Gated Villa", "Spacious 4BHK with garden",
                "VILLA", "Bangalore", "Indiranagar", 4, 85000.0, 250000.0, Constants.STATUS_APPROVED));

        // Enqueue a pending submission into Admin FIFO Queue for moderation
        try {
            Property pendingProperty = new Property(104L, 2L, "Cyber Towers Executive Flat", "2BHK near Hitec City",
                    "APARTMENT", "Hyderabad", "Gachibowli", 2, 28000.0, 60000.0, Constants.STATUS_DRAFT);
            listingService.createListing(pendingProperty);
        } catch (ListingValidationException ignored) {
            // Seed data is pre-validated
        }
    }

    /**
     * Starts the interactive command-line loop.
     *
     * @param scanner the input scanner to read user choices
     */
    public void start(Scanner scanner) {
        printWelcomeBanner();

        while (running) {
            printMenu();
            System.out.print("Select an option (0-9): ");

            if (!scanner.hasNextLine()) {
                break;
            }

            String input = scanner.nextLine().trim();

            try {
                switch (input) {
                    case "1" -> handleFR1OwnerListing();
                    case "2" -> handleFR2AdminModeration();
                    case "3" -> handleFR3SeekerSearch();
                    case "4" -> handleFR4SeekerFavourites();
                    case "5" -> handleFR5SiteVisitsAndEnquiries();
                    case "6" -> handleFR6LeaseManagement();
                    case "7" -> handleFR7RentDueEngine();
                    case "8" -> handleFR8TenantPortal();
                    case "9" -> handleViewWeeklySampleData();
                    case "0" -> handleExit();
                    default -> System.out.println("\n[!] Invalid selection. Please enter a number between 0 and 9.\n");
                }
            } catch (ListingValidationException e) {
                // Recovering from custom checked exception
                System.out.println("\n[MENU RECOVERY] Caught Checked ListingValidationException: " + e.getMessage());
                System.out.println("[System Status] Handled cleanly. Returning to main menu.");
            } catch (PropertyNotFoundException | UnauthorizedAccessException | InvalidStateTransitionException e) {
                // Demonstrating Multi-Catch (A1-EX-03) and recovering from unchecked domain exceptions
                System.out.println("\n[MENU RECOVERY] Caught Domain Exception via Multi-Catch: " + e.getMessage());
                System.out.println("[System Status] Handled cleanly. Returning to main menu.");
            } catch (Exception e) {
                System.out.println("\n[MENU RECOVERY] Caught Unexpected Exception: " + e.getMessage());
            }

            if (running) {
                System.out.println("\nPress Enter to return to main menu...");
                if (scanner.hasNextLine()) {
                    scanner.nextLine();
                }
            }
        }

        System.out.println("\nThank you for using " + Constants.APP_NAME + ". Goodbye!");
    }

    private void printWelcomeBanner() {
        System.out.println("=========================================================================================");
        System.out.println("       " + Constants.APP_NAME.toUpperCase() + " (v" + Constants.APP_VERSION + ")");
        System.out.println("                   HCL Java Full Stack Engineer Trainee Capstone                         ");
        System.out.println("=========================================================================================");
    }

    public void printMenu() {
        System.out.println("\n============================= MAIN FUNCTIONAL MENU ======================================");
        System.out.println("  [1] FR1: Owner Listing Management        -> Create, Edit, or Deactivate Listings");
        System.out.println("  [2] FR2: Admin Listing Moderation        -> Review FIFO Queue & Approve/Reject");
        System.out.println("  [3] FR3: Seeker Dynamic Property Search  -> Filter by City, Budget, BHK & Type");
        System.out.println("  [4] FR4: Seeker Favourites & Shortlists  -> Bookmark Preferred Rental Homes");
        System.out.println("  [5] FR5: Site Visits & Inquiries         -> Request Viewings & Send Owner Inquiries");
        System.out.println("  [6] FR6: Lease Agreement Creation        -> Formulate Binding Digital Lease Contracts");
        System.out.println("  [7] FR7: Monthly Rent Due Engine         -> Generate Rent Schedules & Billing Dues");
        System.out.println("  [8] FR8: Tenant Portal                   -> View Lease Terms, Dues & Payment History");
        System.out.println("  [9] DEMO: 1-Week Sample Transaction Data -> Review 7-Day Property & Metric Arrays");
        System.out.println("  [0] Exit System");
        System.out.println("=========================================================================================");
    }

    // --- Option Handlers ---

    private void handleFR1OwnerListing() throws ListingValidationException {
        System.out.println("\n--- [FR1] OWNER PROPERTY LISTING MANAGEMENT ---");
        System.out.println("Role: " + Constants.ROLE_OWNER);
        System.out.println("Action: Create, edit, and deactivate real estate listings.");
        System.out.println("Status Lifecycle: " + Constants.STATUS_DRAFT + " -> " + Constants.STATUS_PENDING_REVIEW + " -> " + Constants.STATUS_APPROVED);
        System.out.println("Allowed Rent Limits: INR " + Constants.MIN_MONTHLY_RENT + " to INR " + Constants.MAX_MONTHLY_RENT);

        // Enforce Business Rule 1: Financial & Listing Invariant Validation
        System.out.println("\n[Enforcing Business Rule 1: Financial & Invariant Validation]");
        Property validProperty = new Property(101L, 1L, "Marina Bay 3BHK", "Luxury sea view apartment",
                "APARTMENT", "Chennai", "Besant Nagar", 3, 45000.0, 100000.0, Constants.STATUS_DRAFT);
        BusinessRuleEnforcer.validateListingInvariants(validProperty);
        System.out.println("  [PASS] Valid Property passed: " + validProperty.getTitle() + " (Rent: INR " + validProperty.getMonthlyRent() + ", Deposit: INR " + validProperty.getDepositAmount() + ")");

        System.out.println("\n[Testing Business Rule 1 Violation: Rent below INR " + Constants.MIN_MONTHLY_RENT + "]");
        Property invalidProperty = new Property(102L, 1L, "Budget Micro-Room", "Invalid rent",
                "ROOM", "Chennai", "Velachery", 1, 500.0, 2000.0, Constants.STATUS_DRAFT);
        try {
            BusinessRuleEnforcer.validateListingInvariants(invalidProperty);
        } catch (ListingValidationException e) {
            System.out.println("  [RECOVERED] Gracefully caught ListingValidationException: " + e.getMessage());
        }

        // Enforce Business Rule 2A: Ownership Authorization Check
        System.out.println("\n[Enforcing Business Rule 2A: Ownership Authorization (IDOR Protection)]");
        User owner = new User(1L, "Rajesh", "Kumar", "rajesh@example.com", "9876543210", Constants.ROLE_OWNER);
        User intruder = new User(99L, "Unauthorized", "User", "intruder@example.com", "9000000000", Constants.ROLE_SEEKER);

        BusinessRuleEnforcer.verifyOwnershipAuthorization(validProperty, owner);
        System.out.println("  [PASS] Owner verified for property modification: " + owner.getFullName());

        try {
            BusinessRuleEnforcer.verifyOwnershipAuthorization(validProperty, intruder);
        } catch (UnauthorizedAccessException e) {
            System.out.println("  [RECOVERED] Gracefully caught UnauthorizedAccessException: " + e.getMessage());
        }

        // Demonstrating List collection for Owner listings
        System.out.println("\n[List Demonstration: Querying Listings for Owner ID: 1]");
        List<Property> ownerListings = listingService.getListingsByOwner(1L);
        System.out.printf("  Found %d listings for Owner [%s]:%n", ownerListings.size(), owner.getFullName());
        for (Property p : ownerListings) {
            System.out.printf("   - #%d: %s | %s | Rent: INR %,.2f | Status: %s%n",
                    p.getId(), p.getTitle(), p.getCity(), p.getMonthlyRent(), p.getStatus());
        }
    }

    private void handleFR2AdminModeration() {
        System.out.println("\n--- [FR2] ADMIN LISTING REVIEW & MODERATION (QUEUE) ---");
        System.out.println("Role: " + Constants.ROLE_ADMIN);
        System.out.println("Action: Review listings pending moderation in a FIFO Queue.");
        System.out.println("Possible Decisions: [APPROVE] -> Makes listing public | [REJECT] -> Feedback to owner.");

        // Demonstrating Queue collection (A1-COL-04)
        int queueSize = listingService.getPendingQueueSize();
        System.out.println("\n[Queue Demonstration: Inspecting FIFO Moderation Queue]");
        System.out.println("  Current Pending Queue Size: " + queueSize);

        Optional<Property> headOptional = listingService.getNextPendingForModeration();
        if (headOptional.isPresent()) {
            Property pending = headOptional.get();
            System.out.println("  Head of Queue (Oldest Submission):");
            System.out.printf("   - Property ID: %d | Title: %s%n", pending.getId(), pending.getTitle());
            System.out.printf("   - City: %s | Rent: INR %,.2f | Deposit: INR %,.2f%n",
                    pending.getCity(), pending.getMonthlyRent(), pending.getDepositAmount());
            System.out.printf("   - Status: %s | Enqueued at: %s%n", pending.getStatus(), pending.getCreatedAt());

            // Admin decides to APPROVE the submission
            System.out.println("\n  [Admin Action: Executing Approval Decision for ID " + pending.getId() + "]");
            Property approved = listingService.approveListing(pending.getId());
            System.out.printf("  [SUCCESS] Property #%d approved! Status updated to: %s%n", approved.getId(), approved.getStatus());
            System.out.println("  Remaining Queue Size after FIFO poll: " + listingService.getPendingQueueSize());
        } else {
            System.out.println("  [Queue Empty] All submitted property listings have been moderated.");
        }
    }

    private void handleFR3SeekerSearch() {
        System.out.println("\n--- [FR3] SEEKER DYNAMIC SEARCH & FILTERING (LIST & SET) ---");
        System.out.println("Role: " + Constants.ROLE_SEEKER);
        System.out.println("Action: Multi-predicate dynamic search across approved listings.");

        // Demonstrating Set collection for unique available cities
        Set<String> cities = listingService.getAvailableCities();
        System.out.println("\n[Set Demonstration: Available Cities with Active Listings]");
        System.out.println("  Distinct Cities: " + cities);

        // Demonstrating List collection for filtered search results using Strategy Pattern
        System.out.println("\n[Strategy & List Demonstration: Searching Bangalore Listings <= INR 50,000]");
        SearchCriteria criteria = SearchCriteria.byCityAndPrice("Bangalore", 10000.0, 50000.0);
        List<Property> searchResults = listingService.search(criteria);
        System.out.printf("  Found %d matching properties in Bangalore:%n", searchResults.size());
        for (Property p : searchResults) {
            System.out.printf("   - #%d: %s | %s | Rent: INR %,.2f | Status: %s%n",
                    p.getId(), p.getTitle(), p.getCity(), p.getMonthlyRent(), p.getStatus());
        }

        // Demonstrating Set collection for deduplicated favourites
        if (!searchResults.isEmpty()) {
            Property selected = searchResults.get(0);
            System.out.println("\n[Set Demonstration: Adding Property to Seeker Favourites]");
            boolean firstAdd = listingService.addToFavourites(selected);
            System.out.printf("  Added Property #%d to favourites? %s (First attempt)%n", selected.getId(), firstAdd);

            boolean duplicateAdd = listingService.addToFavourites(selected);
            System.out.printf("  Added Property #%d again? %s (Deduplication enforced by Set)%n", selected.getId(), duplicateAdd);

            System.out.println("  Current Favourites Set Size: " + listingService.getFavourites().size());
        }
    }

    private void handleFR4SeekerFavourites() {
        System.out.println("\n--- [FR4] SEEKER FAVOURITES & SHORTLISTS ---");
        System.out.println("Role: " + Constants.ROLE_SEEKER);
        System.out.println("Action: Bookmark and manage preferred properties.");
        Set<Property> favs = listingService.getFavourites();
        System.out.printf("Current Favourites Count (Set): %d%n", favs.size());
        for (Property p : favs) {
            System.out.printf(" - #%d: %s (%s, INR %,.2f)%n", p.getId(), p.getTitle(), p.getCity(), p.getMonthlyRent());
        }
        System.out.println("[Ready] Ready for Map associations in Task 8.");
    }

    private void handleFR5SiteVisitsAndEnquiries() {
        System.out.println("\n--- [FR5] SITE VISITS & DIRECT OWNER ENQUIRIES ---");
        System.out.println("Roles: " + Constants.ROLE_SEEKER + " & " + Constants.ROLE_OWNER);
        System.out.println("Action: Schedule property visits and coordinate through direct inquiries.");
        System.out.println("Visit Lifecycle: " + Constants.VISIT_REQUESTED + " -> " + Constants.VISIT_CONFIRMED + " / " + Constants.VISIT_CANCELLED);
        System.out.println("[Ready] Ready for bidirectional engagement services in Task 8.");
    }

    private void handleFR6LeaseManagement() {
        System.out.println("\n--- [FR6] LEASE AGREEMENT CREATION ---");
        System.out.println("Roles: " + Constants.ROLE_OWNER + " & " + Constants.ROLE_TENANT);
        System.out.println("Action: Create digital rental lease binding tenant to approved property.");
        System.out.println("Property Status Transition: Property moves to " + Constants.STATUS_RENTED);

        // Enforce Business Rule 2B: State Transition Validation
        System.out.println("\n[Enforcing Business Rule 2B: Valid State Transition for Lease Creation]");
        Property approvedProperty = new Property(201L, 1L, "Greenwood Villa", "Spacious 4BHK",
                "VILLA", "Bangalore", "Indiranagar", 4, 80000.0, 250000.0, Constants.STATUS_APPROVED);
        BusinessRuleEnforcer.verifyCanCreateLease(approvedProperty);
        System.out.println("  [PASS] Lease permitted for APPROVED property: " + approvedProperty.getTitle());

        System.out.println("\n[Testing Business Rule 2B Violation: Attempting lease on DRAFT property]");
        Property draftProperty = new Property(202L, 1L, "Unapproved Apartment", "Pending property",
                "APARTMENT", "Bangalore", "Whitefield", 2, 28000.0, 60000.0, Constants.STATUS_DRAFT);
        try {
            BusinessRuleEnforcer.verifyCanCreateLease(draftProperty);
        } catch (InvalidStateTransitionException | PropertyNotFoundException e) {
            System.out.println("  [RECOVERED] Multi-catch caught domain exception: " + e.getMessage());
        }
    }

    private void handleFR7RentDueEngine() {
        System.out.println("\n--- [FR7] MONTHLY RENT DUE GENERATION ENGINE ---");
        System.out.println("Actor: Automated System Engine");
        System.out.println("Action: Generate 12 sequential monthly rent due entries per 1-year lease.");
        System.out.println("Standard Due Date: Day " + Constants.RENT_DUE_DAY_OF_MONTH + " of each calendar month.");
        System.out.println("Rent Status Lifecycle: " + Constants.RENT_PENDING + " -> " + Constants.RENT_PAID + " / " + Constants.RENT_OVERDUE);
        System.out.println("[Ready] Ready for date calculations and due generation in Task 8.");
    }

    private void handleFR8TenantPortal() {
        System.out.println("\n--- [FR8] TENANT PORTAL: LEASE & PAYMENT HISTORY ---");
        System.out.println("Role: " + Constants.ROLE_TENANT);
        System.out.println("Action: Review active lease, upcoming dues, and historical payment receipts.");
        System.out.println("[Ready] Ready for payment tracking and receipt ledger in Task 8.");
    }

    private void handleViewWeeklySampleData() {
        System.out.println("\n--- [DEMO] 1-WEEK SAMPLE TRANSACTION DATA (ARRAYS) ---");
        SampleArrayData.displayWeeklyReport();
    }

    private void handleExit() {
        System.out.println("\nExiting application...");
        this.running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public ListingServiceImpl getListingService() {
        return listingService;
    }

    public static void main(String[] args) {
        ConsoleMenuApp app = new ConsoleMenuApp();
        // Demonstrating try-with-resources (A1-EX-04)
        try (Scanner scanner = new Scanner(System.in)) {
            app.start(scanner);
        }
    }
}