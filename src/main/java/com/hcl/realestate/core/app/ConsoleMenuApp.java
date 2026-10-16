package com.hcl.realestate.core.app;

import com.hcl.realestate.core.dto.SearchCriteria;
import com.hcl.realestate.core.exception.InvalidStateTransitionException;
import com.hcl.realestate.core.exception.ListingValidationException;
import com.hcl.realestate.core.exception.PropertyNotFoundException;
import com.hcl.realestate.core.exception.UnauthorizedAccessException;
import com.hcl.realestate.core.model.Enquiry;
import com.hcl.realestate.core.model.Lease;
import com.hcl.realestate.core.model.Property;
import com.hcl.realestate.core.model.User;
import com.hcl.realestate.core.model.VisitRequest;
import com.hcl.realestate.core.service.ListingServiceImpl;
import com.hcl.realestate.core.util.Constants;
import com.hcl.realestate.core.util.PropertyComparators;
import com.hcl.realestate.core.util.SampleArrayData;
import com.hcl.realestate.core.validation.BusinessRuleEnforcer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

/**
 * Interactive Console Menu Application for JFS-16 Real Estate Listing & Rental Management.
 * Demonstrates:
 * - One dedicated option per Functional Requirement (FR1 through FR8)
 * - Collections: List (A1-COL-01), Set (A1-COL-02), Map (A1-COL-03), and Queue (A1-COL-04)
 * - Comparators (A1-COL-06): BY_RENT_ASC, BY_CITY_THEN_RENT, BY_BHK_DESC
 * - Sorted tabular reports grouped by Map key
 * - Full interactive implementation of FR1 through FR6
 * - Custom Checked/Unchecked Exception handling, Multi-catch, and Try-with-resources (A1-EX-01..04)
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
        listingService.seedApprovedProperty(new Property(101L, 1L, "Marina Bay 3BHK", "Luxury sea view apartment",
                "APARTMENT", "Chennai", "Besant Nagar", 3, 45000.0, 100000.0, Constants.STATUS_APPROVED));
        listingService.seedApprovedProperty(new Property(102L, 2L, "Prestige Tech Park Studio", "Near IT corridor",
                "STUDIO", "Bangalore", "Whitefield", 1, 18000.0, 50000.0, Constants.STATUS_APPROVED));
        listingService.seedApprovedProperty(new Property(103L, 1L, "Greenwood Gated Villa", "Spacious 4BHK with garden",
                "VILLA", "Bangalore", "Indiranagar", 4, 85000.0, 250000.0, Constants.STATUS_APPROVED));
        listingService.seedApprovedProperty(new Property(104L, 3L, "Sea Breeze Beachfront Flat", "Ocean front 2BHK",
                "APARTMENT", "Chennai", "ECR", 2, 38000.0, 80000.0, Constants.STATUS_APPROVED));

        try {
            Property pendingProperty = new Property(105L, 2L, "Cyber Towers Executive Flat", "2BHK near Hitec City",
                    "APARTMENT", "Hyderabad", "Gachibowli", 2, 28000.0, 60000.0, Constants.STATUS_DRAFT);
            listingService.createListing(pendingProperty);
        } catch (ListingValidationException ignored) {
            // Pre-validated seed
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
                System.out.println("\n[MENU RECOVERY] Caught Checked ListingValidationException: " + e.getMessage());
                System.out.println("[System Status] Handled cleanly. Returning to main menu.");
            } catch (PropertyNotFoundException | UnauthorizedAccessException | InvalidStateTransitionException e) {
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
        System.out.println("  [4] FR4: Seeker Favourites & Shortlists  -> Bookmark Preferred Rental Homes (Map)");
        System.out.println("  [5] FR5: Site Visits & Inquiries         -> Request Viewings & Send Inquiries (Maps)");
        System.out.println("  [6] FR6: Lease Agreement Creation        -> Formulate Digital Leases (Map)");
        System.out.println("  [7] FR7: Monthly Rent Due Engine         -> Generate Rent Schedules & Billing Dues");
        System.out.println("  [8] FR8: Tenant Portal                   -> View Lease Terms, Dues & Payment History");
        System.out.println("  [9] DEMO: Sorted City Report & Arrays    -> View Map Reports & 7-Day Metric Arrays");
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

        Set<String> cities = listingService.getAvailableCities();
        System.out.println("\n[Set Demonstration: Available Cities with Active Listings]");
        System.out.println("  Distinct Cities: " + cities);

        System.out.println("\n[Strategy & List Demonstration: Searching Bangalore Listings <= INR 50,000]");
        SearchCriteria criteria = SearchCriteria.byCityAndPrice("Bangalore", 10000.0, 50000.0);
        List<Property> searchResults = listingService.search(criteria);
        System.out.printf("  Found %d matching properties in Bangalore:%n", searchResults.size());
        for (Property p : searchResults) {
            System.out.printf("   - #%d: %s | %s | Rent: INR %,.2f | Status: %s%n",
                    p.getId(), p.getTitle(), p.getCity(), p.getMonthlyRent(), p.getStatus());
        }

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
        System.out.println("\n--- [FR4] SEEKER FAVOURITES & SHORTLISTS (MAP<USER_ID, SET<PROPERTY>>) ---");
        System.out.println("Role: " + Constants.ROLE_SEEKER);
        System.out.println("Action: Bookmark and manage preferred properties mapped per Seeker ID.");

        Long seeker1Id = 10L;
        Long seeker2Id = 20L;

        Optional<Property> p1 = listingService.findById(101L);
        Optional<Property> p2 = listingService.findById(102L);
        Optional<Property> p3 = listingService.findById(103L);

        p1.ifPresent(p -> listingService.addFavourite(seeker1Id, p));
        p2.ifPresent(p -> listingService.addFavourite(seeker1Id, p));
        p3.ifPresent(p -> listingService.addFavourite(seeker2Id, p));

        System.out.println("\n[Map Demonstration: Seeker ID 10 Favourites]");
        Set<Property> s1Favs = listingService.getFavourites(seeker1Id);
        System.out.printf("  Seeker #%d has %d shortlisted properties:%n", seeker1Id, s1Favs.size());
        for (Property p : s1Favs) {
            System.out.printf("   - #%d: %s in %s (Rent: INR %,.2f)%n", p.getId(), p.getTitle(), p.getCity(), p.getMonthlyRent());
        }

        System.out.println("\n[Map Demonstration: Seeker ID 20 Favourites]");
        Set<Property> s2Favs = listingService.getFavourites(seeker2Id);
        System.out.printf("  Seeker #%d has %d shortlisted properties:%n", seeker2Id, s2Favs.size());
        for (Property p : s2Favs) {
            System.out.printf("   - #%d: %s in %s (Rent: INR %,.2f)%n", p.getId(), p.getTitle(), p.getCity(), p.getMonthlyRent());
        }

        System.out.println("\n[Map Overview: Total Users with Active Shortlists: " + listingService.getAllFavouritesByUser().size() + "]");
    }

    private void handleFR5SiteVisitsAndEnquiries() {
        System.out.println("\n--- [FR5] SITE VISITS & DIRECT OWNER ENQUIRIES (MAPS) ---");
        System.out.println("Roles: " + Constants.ROLE_SEEKER + " & " + Constants.ROLE_OWNER);
        System.out.println("Action: Schedule property visits and coordinate through direct inquiries.");

        Long targetPropertyId = 101L;
        Long seekerId = 10L;

        // Inquiry flow using Map<Long, List<Enquiry>>
        System.out.println("\n[Enquiry Flow (Map<PropertyID, List<Enquiry>>)]");
        Enquiry enquiry = listingService.sendEnquiry(targetPropertyId, seekerId, "Is parking space included in the rent?");
        System.out.printf("  Seeker #%d sent Enquiry #%d for Property #%d: \"%s\"%n",
                seekerId, enquiry.id(), targetPropertyId, enquiry.message());

        Enquiry answered = listingService.respondToEnquiry(targetPropertyId, enquiry.id(), "Yes, covered dedicated 4-wheeler parking is included.");
        System.out.printf("  Owner responded: \"%s\" (Responded at: %s)%n",
                answered.ownerResponse(), answered.respondedAt());

        // Site Visit flow using Map<Long, List<VisitRequest>>
        System.out.println("\n[Site Visit Flow (Map<PropertyID, List<VisitRequest>>)]");
        LocalDateTime scheduledTime = LocalDateTime.now().plusDays(3).withHour(11).withMinute(0);
        VisitRequest visit = listingService.requestSiteVisit(targetPropertyId, seekerId, scheduledTime);
        System.out.printf("  Seeker #%d requested visit #%d for %s (Status: %s)%n",
                seekerId, visit.id(), visit.preferredDateTime(), visit.status());

        VisitRequest confirmed = listingService.confirmSiteVisit(targetPropertyId, visit.id(), "Confirmed by owner. Gate pass arranged.");
        System.out.printf("  Owner confirmed visit: Status [%s] | Note: \"%s\"%n",
                confirmed.status(), confirmed.ownerNotes());
    }

    private void handleFR6LeaseManagement() throws ListingValidationException {
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

        // Live Lease Execution using Map<Long, Lease>
        System.out.println("\n[Map Demonstration: Creating & Registering Active Lease Contract]");
        User owner = new User(1L, "Rajesh", "Kumar", "rajesh@example.com", "9876543210", Constants.ROLE_OWNER);
        Long tenantId = 55L;

        Optional<Property> targetOptional = listingService.findById(101L);
        if (targetOptional.isPresent()) {
            Property target = targetOptional.get();
            if (Constants.STATUS_APPROVED.equalsIgnoreCase(target.getStatus())) {
                LocalDate start = LocalDate.now();
                LocalDate end = start.plusYears(1);
                Lease newLease = new Lease(null, target.getId(), tenantId, target.getOwnerId(),
                        start, end, target.getMonthlyRent(), target.getDepositAmount(), Constants.LEASE_ACTIVE);

                Lease created = listingService.createLease(newLease, owner);
                System.out.printf("  [SUCCESS] Lease #%d created for Tenant #%d!%n", created.getId(), tenantId);
                System.out.printf("   - Property ID: %d | Status transitioned to: %s%n", target.getId(), target.getStatus());
                System.out.printf("   - Tenure: %s to %s (%d months)%n",
                        created.getStartDate(), created.getEndDate(), created.getDurationInMonths());
                System.out.printf("   - Monthly Rent: INR %,.2f | Deposit: INR %,.2f%n",
                        created.getMonthlyRent(), created.getDepositAmount());
            } else {
                System.out.printf("  Property #%d is currently [%s]. Active lease already registered.%n",
                        target.getId(), target.getStatus());
            }
        }
        System.out.println("  Total Active Leases in System Map: " + listingService.getActiveLeases().size());
    }

    private void handleFR7RentDueEngine() {
        System.out.println("\n--- [FR7] MONTHLY RENT DUE GENERATION ENGINE ---");
        System.out.println("Actor: Automated System Engine");
        System.out.println("Action: Generate 12 sequential monthly rent due entries per 1-year lease.");
        System.out.println("Standard Due Date: Day " + Constants.RENT_DUE_DAY_OF_MONTH + " of each calendar month.");
        System.out.println("Rent Status Lifecycle: " + Constants.RENT_PENDING + " -> " + Constants.RENT_PAID + " / " + Constants.RENT_OVERDUE);
        System.out.println("[Ready] Ready for date calculations and due generation in Task 9.");
    }

    private void handleFR8TenantPortal() {
        System.out.println("\n--- [FR8] TENANT PORTAL: LEASE & PAYMENT HISTORY ---");
        System.out.println("Role: " + Constants.ROLE_TENANT);
        System.out.println("Action: Review active lease, upcoming dues, and historical payment receipts.");
        System.out.println("[Ready] Ready for payment tracking and receipt ledger in Task 9.");
    }

    private void handleViewWeeklySampleData() {
        System.out.println("\n--- [DEMO] 1-WEEK SAMPLE TRANSACTION DATA (ARRAYS) ---");
        SampleArrayData.displayWeeklyReport();

        System.out.println("\n" + listingService.generateSortedCityReport(PropertyComparators.BY_CITY_THEN_RENT));
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
        try (Scanner scanner = new Scanner(System.in)) {
            app.start(scanner);
        }
    }
}