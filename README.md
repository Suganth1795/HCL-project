# JFS-16: Real Estate Listing & Rental Management
## HCL Java Full Stack Engineer Trainee Capstone Project

### 1. Project Overview
The **Real Estate Listing & Rental Management System** is an enterprise-grade full-stack platform designed to manage the end-to-end lifecycle of property rentals. The application coordinates interactions across four distinct personas:
- **Property Owners / Agents**: Create and manage listings, coordinate site visits, answer prospective inquiries, and formulate digital rental lease contracts.
- **Property Seekers**: Discover verified homes via dynamic multi-criteria search, shortlist favorite listings, schedule site visits, and inquire directly with owners.
- **Platform Administrators**: Moderate listing submissions via a FIFO approval queue to maintain platform integrity and quality standards.
- **Tenants**: Access binding lease terms, review automated monthly rent dues, and track payment receipts.

---

### 2. Functional Requirements (FR1 to FR8)
1. **FR1**: Owner can create, edit, and deactivate property listings.
2. **FR2**: Admin can review, approve, or reject property listings.
3. **FR3**: Seeker can search and filter properties by city, locality, price range, BHK, property type, and furnishing.
4. **FR4**: Seeker can shortlist and favorite properties.
5. **FR5**: Seeker can request site visits and send inquiries; Owner can respond and manage them.
6. **FR6**: Owner can create and manage rental leases for tenants.
7. **FR7**: System automatically generates and manages monthly rent due schedules.
8. **FR8**: Tenant can view current lease agreements, rent dues, and payment transaction history.

---

### 3. Agile Specifications & Backlog
- Detailed User Stories (`US-01` through `US-08`), Story Point estimations (Fibonacci scale), Acceptance Criteria (Given-When-Then), Definition of Ready (DoR), and Definition of Done (DoD) are documented in:
  - **[docs/USER-STORIES.md](docs/USER-STORIES.md)**

---

### 4. Core Java Foundations (Tasks 1 to 10 Progression)

#### A. Global Constants (`Constants.java`)
Centralized definitions residing in `com.hcl.realestate.core.util.Constants`:
- **User Roles**: `ADMIN`, `OWNER`, `SEEKER`, `TENANT`.
- **Property Types**: `APARTMENT`, `INDEPENDENT_HOUSE`, `VILLA`, `STUDIO`, `PG`.
- **Listing Lifecycle Statuses**: `DRAFT`, `PENDING_REVIEW`, `APPROVED`, `REJECTED`, `DEACTIVATED`, `RENTED`.
- **Lease & Rent Statuses**: `ACTIVE`, `EXPIRED`, `TERMINATED`, `PENDING`, `PAID`, `OVERDUE`.
- **Business Limits**: Rent bounds (`INR 1,000` to `INR 1,000,000`), deposit multiplier, and billing due dates.
- **Calendar Arrays**: 7-day week schedule constants.

#### B. 1-Week Sample Transaction Data (`SampleArrayData.java`)
Demonstrates Java 1D and 2D arrays:
- `String[] DAYS`: 7 days of the week (Monday through Sunday).
- `double[] DAILY_COLLECTIONS`: Rent dues collected over the 7-day period.
- `int[] DAILY_VISIT_REQUESTS`: Site visit requests received each day.
- `String[][] WEEKLY_LISTINGS`: 2D tabular array modeling weekly property listing submissions with titles, cities, BHKs, rents, and statuses.
- **Algorithms**: Array traversal, sum calculation (`calculateTotalWeeklyCollections`), average computation (`calculateAverageDailyCollection`), and status filtering.

#### C. Interactive Console Application & Menu (`ConsoleMenuApp.java`)
Provides a menu-driven CLI interface with dedicated handlers for each Functional Requirement:
- `[1] FR1`: Owner Property Listing Management
- `[2] FR2`: Admin Listing Moderation (FIFO Queue Review)
- `[3] FR3`: Seeker Dynamic Property Search & Filter
- `[4] FR4`: Seeker Favourites & Shortlists
- `[5] FR5`: Site Visits & Inquiries
- `[6] FR6`: Lease Agreement Creation
- `[7] FR7`: Monthly Rent Due Generation Schedule
- `[8] FR8`: Tenant Portal (Lease, Dues & Receipts)
- `[9] DEMO`: View 1-Week Sample Transaction Data
- `[0]`: Exit System

#### D. Core Domain Models (`com.hcl.realestate.core.model`)
- **`Property`**: Represents real estate listings. Implements `Comparable<Property>` for natural ordering by `monthlyRent` ascending, full encapsulation, parameterized and default constructors, and equality by ID.
- **`User`**: Models platform participants across roles (`OWNER`, `SEEKER`, `TENANT`, `ADMIN`) with full profile encapsulation and `getFullName()` helper.
- **`Lease`**: Codifies binding tenancy contracts between Owner and Tenant with start and end dates (`java.time.LocalDate`), duration calculations via `java.time.Period`, monthly rent, and deposit tracking.

#### E. BaseEntity, Role Hierarchy & Strategy Pattern
- **`BaseEntity`**: Abstract domain base class providing unified identity (`Long id`) and automated auditing timestamps (`createdAt`, `updatedAt`), demonstrating Abstraction (A1-OOP-09) and Encapsulation (A1-OOP-02). Extended by `Property`, `User`, and `Lease`.
- **`Role` & `RoleHierarchy`**: Enum and RBAC authority engine defining hierarchical privileges:
  - `ROLE_ADMIN` (level 4) implies all lower roles (`ROLE_OWNER`, `ROLE_TENANT`, `ROLE_SEEKER`).
  - `ROLE_OWNER` (level 3) implies `ROLE_SEEKER`.
  - `ROLE_TENANT` (level 2) implies `ROLE_SEEKER`.
  - `ROLE_SEEKER` (level 1) base access.
- **`SearchStrategy`**: Pluggable interface demonstrating the Strategy Pattern (A1-DSG-06) and Open/Closed Principle (A1-DSG-02) with two concrete implementations:
  1. `CitySearchStrategy`: Case-insensitive property location matching.
  2. `PriceRangeSearchStrategy`: Dynamic budget filtering between minimum and maximum monthly rent bounds.
- **`SearchCriteria`**: Immutable Java 21 Record holding multi-attribute search parameters.

#### F. Custom Exceptions, Business Rule Enforcement & Error Recovery
- **Custom Checked Exception (`A1-EX-01`)**:
  - `ListingValidationException`: Thrown when a property violates financial or invariant rules (mandatory title/city, rent out of bounds, insufficient deposit).
- **Custom Unchecked Exceptions (`A1-EX-02`)**:
  - `PropertyNotFoundException`: Thrown when querying a non-existent property identifier.
  - `UnauthorizedAccessException`: Thrown when an unprivileged user attempts unauthorized mutation (IDOR protection).
  - `InvalidStateTransitionException`: Thrown on illegal finite state machine transitions (e.g. attempting to lease a `DRAFT` or already `RENTED` property).
- **Enforced Business Rules (`BusinessRuleEnforcer.java`)**:
  - **Rule 1 (Listing Invariants & Financials)**: Title & city non-blank, rent bounded to `[INR 1,000, INR 1,000,000]`, and deposit $\ge$ 2x monthly rent.
  - **Rule 2 (Ownership & State Machine)**: Only registered owner or Admin can modify a property; leases can only be formed on `APPROVED` properties.
- **Menu Error Recovery (`A1-EX-03`, `A1-EX-04`)**:
  - Implements multi-catch `catch (PropertyNotFoundException | UnauthorizedAccessException | InvalidStateTransitionException e)` and checked exception handlers in `ConsoleMenuApp`.
  - Try-with-resources (`try (Scanner scanner = new Scanner(System.in))`) guarantees automatic resource cleanup.
  - Interactive loop catches all domain errors, outputs diagnostic guidance, and resumes execution seamlessly without crashing.

#### G. Collections Framework (List, Set, Queue) & FR1–FR3 Implementation
- **`List` Collection (`A1-COL-01`)**:
  - `List<Property>` stores the active property catalog (`ListingServiceImpl.catalog`).
  - Search results returned dynamically as filtered `List<Property>` matching city and price range predicates.
  - Owner listings retrieved as `List<Property>` filtered by `ownerId`.
- **`Set` Collection (`A1-COL-02`)**:
  - `Set<Property>` manages seeker shortlists and favourites (`ListingServiceImpl.favourites`), naturally enforcing deduplication.
  - `Set<String>` retrieves sorted distinct cities (`TreeSet<>(String.CASE_INSENSITIVE_ORDER)`) with active approved listings.
- **`Queue` Collection (`A1-COL-04`)**:
  - `Queue<Property>` (`LinkedBlockingQueue`) in `ApprovalQueueService` powers the Administrator moderation workflow.
  - Submissions are queued in strict First-In-First-Out (FIFO) arrival order (`offer()`, `poll()`, `peek()`), ensuring older submissions are reviewed first.
- **Interactive FR1–FR3 Workflows in Console Application**:
  - **FR1 (Owner Management)**: Validates and creates listings, saves to catalog (`List`), and enqueues into review queue (`Queue`).
  - **FR2 (Admin Moderation)**: Displays FIFO queue depth, peeks oldest submission, and executes approval decision transitioning status to `APPROVED`.
  - **FR3 (Seeker Dynamic Search & Shortlists)**: Displays available cities (`Set`), executes multi-criteria search (`List`), and bookmarks preferred properties into deduplicated favourites (`Set`).

#### H. Maps, Comparators, Sorted Reports & FR4–FR6 Workflows
- **`Map` Collections (`A1-COL-03`)**:
  - `Map<String, List<Property>>`: City grouping structure (`TreeMap<>(String.CASE_INSENSITIVE_ORDER)`) categorizing properties by metropolitan location.
  - `Map<Long, Set<Property>>`: Seeker favorites mapped by user ID (FR4), guaranteeing unique shortlists per user.
  - `Map<Long, List<Enquiry>>`: Direct prospective messages mapped per property ID (FR5).
  - `Map<Long, List<VisitRequest>>`: Physical site visit appointments mapped per property ID (FR5).
  - `Map<Long, Lease>`: Binding digital tenancy agreements mapped per property ID (FR6).
- **Two Custom Comparators (`A1-COL-06`) (`PropertyComparators.java`)**:
  - **Comparator 1 (`BY_RENT_ASC` / `BY_RENT_DESC`)**: Evaluates natural budget pricing ascending or descending.
  - **Comparator 2 (`BY_CITY_THEN_RENT`)**: Multi-field chained Comparator sorting primarily by City name alphabetically, then by monthly rent ascending, and breaking ties by BHK descending.
- **Sorted City Report Generator**:
  - Groups verified active listings by city via `Map<String, List<Property>>`.
  - Sorts each city's properties using selected Comparator.
  - Calculates city and portfolio aggregates (listing count, average rent, min/max rent).
- **Interactive FR4–FR6 Workflows in Console Application**:
  - **FR4 (Seeker Shortlists)**: View and bookmark favorite homes indexed by Seeker ID (`Map`).
  - **FR5 (Site Visits & Inquiries)**: Send direct inquiries, receive owner replies, and schedule/confirm site visits (`Maps`).
  - **FR6 (Digital Lease Creation)**: Formulates binding lease contracts, checks ownership and state transitions, registers active lease in `Map<Long, Lease>`, and transitions property to `RENTED`.

#### I. Generic CRUD Repository Architecture, Reusable In-Memory Store & CSV Seed Loader
- **Generic CRUD Repository Contract (`A1-GEN-01`, `A1-GEN-02`) (`CrudRepository<T, ID>`)**:
  - Defines a standard generic repository interface parameterised by domain entity `<T>` and identifier `<ID>`.
  - Operations include `save(T entity)`, `findById(ID id)`, `findAll()`, `deleteById(ID id)`, `existsById(ID id)`, `count()`, and `deleteAll()`.
  - Leverages Java `Optional<T>` to provide null-safe single entity retrieval without risking `NullPointerException`.
- **Reusable In-Memory Repository Implementation (`InMemoryGenericRepository<T, ID>`)**:
  - Provides thread-safe, lock-free in-memory persistence powered by `ConcurrentHashMap<ID, T>`.
  - Employs functional lambda interfaces (`Function<T, ID> idExtractor`, `BiConsumer<T, ID> idSetter`, `Supplier<ID> idGenerator`) to manipulate entity identities generically across disparate models without reflection overhead.
  - Returns unmodifiable defensive copies on queries to guarantee data encapsulation and prevent outside state pollution.
- **Specialized Domain Repositories**:
  - `PropertyRepository` / `InMemoryPropertyRepository`: Extends generic CRUD with domain queries (`findByCity`, `findByOwnerId`, `findByStatus`, `findByPriceRange`).
  - `UserRepository` / `InMemoryUserRepository`: Extends generic CRUD with identity lookups (`findByEmail`, `findByRole`).
  - Automatically advances internal sequence IDs to prevent collisions when existing seed records are inserted.
- **CSV Seed Data & Modern Java I/O (`A1-IO-01..03`) (`CsvSeedLoader.java` & `properties.csv`)**:
  - `properties.csv` contains realistic seed listings across metropolitan hubs (Bangalore, Chennai, Mumbai, Hyderabad, Gurgaon, Pune) spanning property types and statuses.
  - `CsvSeedLoader` utilizes modern `try-with-resources` with `BufferedReader` and explicit UTF-8 character encoding.
  - Features robust row parsing, header autodetection and skipping, quotation trimming, and error tolerance that gracefully ignores malformed records without aborting execution.
  - Provides direct stream, classpath resource, and external file loading into any `PropertyRepository`.
- **Service & Console App Integration**:
  - `ListingServiceImpl` accepts `PropertyRepository` via constructor injection.
  - `ConsoleMenuApp` seeds initial catalog data directly from `/data/properties.csv` via `CsvSeedLoader` and reports repository statistics.

#### J. JVM Memory Architecture, Memory Pitfalls Review & Stack/Heap Execution Trace
- **Runtime Memory Structure (`A1-JVM-01`, `A1-JVM-02`)**:
  - Thread-private Stack: Frames for method invocations, Local Variable Arrays (primitives stored by value, references stored by 64-bit pointers), operand evaluation stack.
  - JVM-shared Heap: Young Generation (Eden space for initial `new` allocations, Survivor spaces S0/S1 for object tenuring), Old/Tenured Generation (long-lived services, in-memory repository hash maps), and String Constant Pool (interned literals).
  - Metaspace: Native memory storing class metadata, bytecode tables, and method definitions.
- **Five Critical Memory Pitfalls Analyzed & Remediated**:
  1. *Unclosed I/O Resources*: Addressed via modern `try-with-resources` (`AutoCloseable`) in `CsvSeedLoader.java` preventing OS file descriptor exhaustion.
  2. *Unbounded In-Memory Collections*: Addressed via encapsulation and explicit cleanup methods (`deleteById`, `deleteAll`) preventing heap bloat.
  3. *String Concatenation in Loops*: Addressed via upfront `StringBuilder` allocation in `generateSortedCityReport()` mitigating excessive Eden garbage churn.
  4. *Mutating Hash Keys*: Addressed via immutable ID-based `hashCode()` in `BaseEntity` and Java 21 `record`s for engagement entities.
  5. *Static ClassLoader References*: Addressed via constructor dependency injection instead of static singleton collections.
- **Detailed Stack & Heap Diagram for `ListingServiceImpl.createLease()`**:
  - Traces 6 execution phases: frame allocation, target lookup, ownership validation, active duplicate verification, atomic ID generation, heap graph mutation (`HashMap.put` and String pool reference swap to `"RENTED"`), and frame popping.
  - Complete technical documentation and visual ASCII diagrams recorded in [`docs/MEMORY-ANALYSIS.md`](docs/MEMORY-ANALYSIS.md).

---

### 5. Maven Build & CLI Execution Instructions

```bash
# Clean and compile Java 21 classes
mvn clean compile

# Execute automated JUnit 5 test suite
mvn test

# Run interactive console application
mvn exec:java
```

---

### 6. Daily Work Log (HCL 10 Selected Tasks)

| Task / Day | Task Description | Files Changed | Validation Performed & Actual Result | Commit Hash | Push Status | Next Task |
| :---: | :--- | :--- | :--- | :--- | :---: | :--- |
| **Task 1** | Convert 8 FRs into user stories, estimate story points, and define Definition of Done (DoD) | `docs/USER-STORIES.md`<br>`README.md` | Verified all 8 FRs mapped to US-01..US-08 with Given-When-Then acceptance criteria, 34 story points estimation, DoR/DoD checklists, and Kanban workflow. | [`c365ff3`](https://github.com/Suganth1795/HCL-project/commit/c365ff3) | **Pushed** | Task 2: Repository docs, `.gitignore`, Constants class, and 1-week array sample data |
| **Task 2** | Set up repository docs and `.gitignore`, create Constants class, add 1-week sample data using arrays | `.gitignore`<br>`src/.../Constants.java`<br>`src/.../SampleArrayData.java`<br>`README.md` | Compiled with `javac` (0 errors); executed `SampleArrayData.main()` verifying 7-day tabular report, total collections (INR 276,000), and visit metrics. | [`39b5ccc`](https://github.com/Suganth1795/HCL-project/commit/39b5ccc) | **Pushed** | Task 3: Maven parent structure and console menu with one option per functional requirement |
| **Task 3** | Prepare Maven parent structure (`pom.xml`) and console menu with one option per functional requirement | `pom.xml`<br>`src/.../ConsoleMenuApp.java`<br>`src/test/.../ConsoleMenuAppTest.java`<br>`README.md` | Executed `mvn clean test` (Java 21): 3/3 tests passed with 0 failures; verified console menu displays FR1-FR8 options, sample report, and exits cleanly. | [`45f12d4`](https://github.com/Suganth1795/HCL-project/commit/45f12d4) | **Pushed** | Task 4: Create model classes for the three main entities |
| **Task 4** | Create model classes for the three main entities (`Property`, `User`, `Lease`) | `src/.../Property.java`<br>`src/.../User.java`<br>`src/.../Lease.java`<br>`src/test/.../ModelTest.java`<br>`README.md` | Executed `mvn clean test` (Java 21): 8/8 tests passed with 0 failures; validated encapsulation, Comparable sorting by rent, Period duration calculation, and identity contracts. | [`475099a`](https://github.com/Suganth1795/HCL-project/commit/475099a) | **Pushed** | Task 5: Add BaseEntity, the role hierarchy, and a Strategy interface with two implementations |
| **Task 5** | Add BaseEntity, the role hierarchy, and a Strategy interface with two implementations; use feature branch and pull request workflow | `src/.../BaseEntity.java`<br>`src/.../Role.java`<br>`src/.../RoleHierarchy.java`<br>`src/.../Property.java`<br>`src/.../User.java`<br>`src/.../Lease.java`<br>`src/.../SearchCriteria.java`<br>`src/.../SearchStrategy.java`<br>`src/.../CitySearchStrategy.java`<br>`src/.../PriceRangeSearchStrategy.java`<br>`src/test/.../BaseEntityAndRoleTest.java`<br>`src/test/.../SearchStrategyTest.java`<br>`README.md` | Executed `mvn clean test` (Java 21): 16/16 tests passed with 0 failures; validated BaseEntity audit timestamps, role hierarchy resolution & access rules, and city/price-range search strategies. | [`fe360ff`](https://github.com/Suganth1795/HCL-project/commit/fe360ff) | **Pushed** | Task 6: Add custom exception package, enforce two business rules, and recover from errors in menu |
| **Task 6** | Add custom exception package, enforce two business rules, and recover from errors in the menu | `src/.../ListingValidationException.java`<br>`src/.../PropertyNotFoundException.java`<br>`src/.../UnauthorizedAccessException.java`<br>`src/.../InvalidStateTransitionException.java`<br>`src/.../BusinessRuleEnforcer.java`<br>`src/.../ConsoleMenuApp.java`<br>`src/.../Constants.java`<br>`src/test/.../BusinessRuleExceptionTest.java`<br>`src/test/.../ConsoleMenuAppTest.java`<br>`README.md` | Executed `mvn clean test` (Java 21): 26/26 tests passed with 0 failures; validated custom checked/unchecked exceptions, financial/ownership business rules, multi-catch, and in-menu error recovery. | [`4f7ff51`](https://github.com/Suganth1795/HCL-project/commit/4f7ff51) | **Pushed** | Task 7: Use List, Set, and Queue and implement FR1–FR3 in console application |
| **Task 7** | Use List, Set, and Queue and implement FR1–FR3 in the console application | `src/.../ApprovalQueueService.java`<br>`src/.../ListingService.java`<br>`src/.../ListingServiceImpl.java`<br>`src/.../ConsoleMenuApp.java`<br>`src/test/.../ListingServiceCollectionsTest.java`<br>`README.md` | Executed `mvn clean test` (Java 21): 34/34 tests passed with 0 failures; validated List catalog & search results, Set deduplicated favourites & cities, Queue FIFO admin moderation, and interactive FR1-FR3 workflows. | [`72ad858`](https://github.com/Suganth1795/HCL-project/commit/72ad858) | **Pushed** | Task 8: Use Maps, a sorted report, and two Comparators; implement FR4–FR6 |
| **Task 8** | Use Maps, a sorted report, and two Comparators; implement FR4–FR6 | `src/.../PropertyComparators.java`<br>`src/.../Enquiry.java`<br>`src/.../VisitRequest.java`<br>`src/.../ListingService.java`<br>`src/.../ListingServiceImpl.java`<br>`src/.../ConsoleMenuApp.java`<br>`src/test/.../PropertyComparatorsTest.java`<br>`src/test/.../EngagementAndLeaseServiceTest.java`<br>`src/test/.../ConsoleMenuAppTest.java`<br>`README.md` | Executed `mvn clean test` (Java 21): 46/46 tests passed with 0 failures; validated single and multi-field Comparators, city-grouped Map reports, FR4 user favourites Map, FR5 inquiries/visits Maps, and FR6 digital lease Map contract. | [`0c00a3d`](https://github.com/Suganth1795/HCL-project/commit/0c00a3d) | **Pushed** | Task 9: Create CrudRepository<T, ID> and reusable InMemoryRepository implementations, and load seed data from CSV |
| **Task 9** | Create CrudRepository<T, ID> and reusable InMemoryRepository implementations, and load seed data from CSV | `src/.../CrudRepository.java`<br>`src/.../InMemoryGenericRepository.java`<br>`src/.../PropertyRepository.java`<br>`src/.../InMemoryPropertyRepository.java`<br>`src/.../UserRepository.java`<br>`src/.../InMemoryUserRepository.java`<br>`src/.../CsvSeedLoader.java`<br>`src/main/resources/data/properties.csv`<br>`src/.../ListingServiceImpl.java`<br>`src/.../ConsoleMenuApp.java`<br>`src/test/.../InMemoryGenericRepositoryTest.java`<br>`src/test/.../EntityRepositoriesTest.java`<br>`src/test/.../CsvSeedLoaderTest.java`<br>`README.md` | Executed `mvn clean test` (Java 21): 63/63 tests passed with 0 failures; validated generic type parameters & lambda functional hooks, thread-safe ConcurrentHashMap storage, domain repository queries, CSV resource parsing, and seed loading. | [`b33246f`](https://github.com/Suganth1795/HCL-project/commit/b33246f) | **Pushed** | Task 10: Review memory pitfalls and document stack/heap diagram for one business method |
| **Task 10** | Review memory pitfalls and document stack/heap diagram for one business method | `docs/MEMORY-ANALYSIS.md`<br>`README.md` | Documented JVM runtime memory regions, reviewed 5 critical enterprise memory pitfalls with platform mitigations, produced comprehensive visual Stack & Heap execution trace for `ListingServiceImpl.createLease()`, and verified 63/63 JUnit tests. | *Pending Approval* | *Awaiting Approval* | All 10 Selected HCL Tasks Completed |

---

### 7. Repository & Backlog Links
- **GitHub Repository**: [https://github.com/Suganth1795/HCL-project.git](https://github.com/Suganth1795/HCL-project.git)
- **Target Branch**: `main`