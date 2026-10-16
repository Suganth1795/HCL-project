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

---

### 5. Daily Work Log (HCL 10 Selected Tasks)

| Task / Day | Task Description | Files Changed | Validation Performed & Actual Result | Commit Hash | Push Status | Next Task |
| :---: | :--- | :--- | :--- | :---: | :---: | :--- |
| **Task 1** | Convert 8 FRs into user stories, estimate story points, and define Definition of Done (DoD) | `docs/USER-STORIES.md`<br>`README.md` | Verified all 8 FRs mapped to US-01..US-08 with Given-When-Then acceptance criteria, 34 story points estimation, DoR/DoD checklists, and Kanban workflow. | [`c365ff3`](https://github.com/Suganth1795/HCL-project/commit/c365ff3) | **Pushed** | Task 2: Repository docs, `.gitignore`, Constants class, and 1-week array sample data |
| **Task 2** | Set up repository docs and `.gitignore`, create Constants class, add 1-week sample data using arrays | `.gitignore`<br>`src/.../Constants.java`<br>`src/.../SampleArrayData.java`<br>`README.md` | Compiled with `javac` (0 errors); executed `SampleArrayData.main()` verifying 7-day tabular report, total collections (INR 276,000), and visit metrics. | *Pending Approval* | *Awaiting Approval* | Task 3: Maven parent structure and console menu with one option per functional requirement |

---

### 6. Repository & Backlog Links
- **GitHub Repository**: [https://github.com/Suganth1795/HCL-project.git](https://github.com/Suganth1795/HCL-project.git)
- **Target Branch**: `main`