# Agile Requirements Specification & User Stories
## Project: JFS-16 Real Estate Listing & Rental Management
**Document Version:** 1.0  
**Phase:** Core Java Foundation / Task 1  
**Author:** Suganth C L  

---

## 1. Executive Summary & Domain Scope

The **Real Estate Listing & Rental Management System** is a full-lifecycle platform connecting Property Owners/Agents, Property Seekers, Tenants, and Platform Administrators. The system manages property listing creation and approval, search and discovery, inquiries and site visits, lease generation, and automated monthly rent dues.

### User Roles
- **OWNER**: Posts and manages listings, reviews inquiries, manages site visits, creates tenant leases.
- **SEEKER**: Searches and filters properties, shortlists favorites, submits inquiries, and requests site visits.
- **ADMIN**: Moderates platform listings (approves/rejects), oversees platform health and compliance.
- **TENANT**: Holds active leases, tracks upcoming monthly rent dues, and views payment history.

---

## 2. Functional Requirements to User Stories Mapping

| FR ID | Functional Requirement Summary | User Story ID | Target Persona | Story Points | Priority |
| :---: | :--- | :---: | :---: | :---: | :---: |
| **FR1** | Owner can create, edit, and deactivate listings | **US-01** | Property Owner | 5 | Must Have (P1) |
| **FR2** | Admin can review, approve, or reject listings | **US-02** | Platform Admin | 3 | Must Have (P1) |
| **FR3** | Seeker can search/filter properties dynamically | **US-03** | Property Seeker | 5 | Must Have (P1) |
| **FR4** | Seeker can shortlist / favourite properties | **US-04** | Property Seeker | 3 | Should Have (P2) |
| **FR5** | Seeker can request site visit and send enquiries | **US-05** | Seeker & Owner | 5 | Must Have (P1) |
| **FR6** | Owner can create and manage leases for tenants | **US-06** | Owner & Tenant | 5 | Must Have (P1) |
| **FR7** | System generates and manages monthly rent dues | **US-07** | Automated Engine | 5 | Must Have (P1) |
| **FR8** | Tenant can view current lease, rent dues & history | **US-08** | Active Tenant | 3 | Must Have (P1) |
| **Total** | **8 Functional Requirements** | **8 User Stories** | â€” | **34 SP** | â€” |

---

## 3. Detailed User Stories & Acceptance Criteria

### US-01: Property Listing Management (FR1)
* **As a** Property Owner / Landlord,  
* **I want to** create new property listings with specifications, update details, or deactivate them,  
* **So that** I can advertise my vacant properties to potential tenants and keep information accurate.

#### Acceptance Criteria
1. **Given** an authenticated Owner with valid credentials,  
   **When** they submit a new listing with title, property type, city, locality, BHK, monthly rent, and security deposit,  
   **Then** the listing is created in `PENDING_REVIEW` status and queued for administrative approval.
2. **Given** an existing listing owned by the authenticated Owner,  
   **When** the Owner edits the rent or description,  
   **Then** the updates are saved and logged with an updated timestamp.
3. **Given** an approved or active listing,  
   **When** the Owner marks it as deactivated,  
   **Then** its status transitions to `DEACTIVATED` and it is immediately excluded from public search results.
4. **Validation Rule:** Monthly rent must be positive (> 0) and security deposit must be >= 1x monthly rent.

---

### US-02: Administrative Listing Moderation (FR2)
* **As a** Platform Administrator,  
* **I want to** review submitted property listings from a FIFO queue and either approve or reject them with feedback,  
* **So that** only authentic, verified, and policy-compliant listings are made visible to seekers.

#### Acceptance Criteria
1. **Given** an authenticated Administrator,  
   **When** they view the pending listings queue,  
   **Then** unreviewed listings are presented in FIFO (First-In, First-Out) chronological order.
2. **Given** a listing in `PENDING_REVIEW` status,  
   **When** the Admin approves the listing,  
   **Then** its status transitions to `APPROVED`, an approval timestamp is recorded, and it becomes searchable publicly.
3. **Given** a listing in `PENDING_REVIEW` status that violates guidelines,  
   **When** the Admin rejects it with a mandatory reason note,  
   **Then** its status transitions to `REJECTED` and the rejection reason is stored for the owner to view.

---

### US-03: Dynamic Property Search & Multi-criteria Filtering (FR3)
* **As a** Property Seeker,  
* **I want to** search and filter approved properties by city, locality, budget range, BHK count, property type, and furnishing status,  
* **So that** I can quickly find rental homes that match my specific preferences and budget.

#### Acceptance Criteria
1. **Given** any visitor or authenticated Seeker,  
   **When** they initiate a search query,  
   **Then** only listings in `APPROVED` status are returned.
2. **Given** search criteria specifying city and maximum rent (e.g., Chennai, <= 35,000),  
   **When** the query executes,  
   **Then** only matching properties within that geographic and budget boundary are returned.
3. **Given** combined criteria (e.g., Bangalore + 2 BHK + Semi-Furnished + Apartment),  
   **When** the search executes using Composite Search Strategy,  
   **Then** all specified predicates must evaluate to true (AND logic).
4. **Given** no search criteria specified,  
   **When** the user requests properties,  
   **Then** the complete catalog of approved listings is returned with default sorting.

---

### US-04: Shortlist & Bookmark Properties (FR4)
* **As a** Property Seeker,  
* **I want to** shortlist and bookmark favorite properties,  
* **So that** I can compare preferred homes in one place and easily revisit them later.

#### Acceptance Criteria
1. **Given** an authenticated Seeker viewing an approved listing,  
   **When** they click "Add to Favourites",  
   **Then** the property ID is added to their shortlist set without duplication.
2. **Given** an already bookmarked property in the Seeker's shortlist,  
   **When** they attempt to add it again,  
   **Then** the system prevents duplicate entries and maintains set uniqueness.
3. **Given** a Seeker viewing their favorites dashboard,  
   **When** they remove a property,  
   **Then** it is immediately removed from their shortlist collection.

---

### US-05: Site Visit Scheduling & Direct Enquiries (FR5)
* **As a** Property Seeker,  
* **I want to** schedule in-person site visits and send inquiries to the property owner, and  
* **As an** Owner,  
* **I want to** confirm visit appointments and reply to inquiries,  
* **So that** both parties can coordinate physical viewings and clarify rental details.

#### Acceptance Criteria
1. **Given** an authenticated Seeker on a property detail page,  
   **When** they select a future date/time and submit a site visit request,  
   **Then** a `VisitRequest` is created in `REQUESTED` status and delivered to the Owner's inbox.
2. **Given** an incoming visit request in the Owner's console,  
   **When** the Owner confirms the appointment with optional meeting notes,  
   **Then** the visit status transitions to `CONFIRMED` and the seeker can view the confirmation.
3. **Given** a Seeker inquiry message on a property listing,  
   **When** the Owner provides a text reply,  
   **Then** the inquiry transitions from `PENDING` to `RESPONDED` with timestamped notes.

---

### US-06: Digital Rental Lease Agreement Creation (FR6)
* **As a** Property Owner,  
* **I want to** generate a digital lease agreement binding a verified tenant to an approved property,  
* **So that** legal terms, tenancy duration, security deposit, and monthly rent are formally codified.

#### Acceptance Criteria
1. **Given** an authenticated Owner and an approved property,  
   **When** the Owner creates a lease specifying Tenant ID, start date, end date, monthly rent, and deposit,  
   **Then** the lease is instantiated in `ACTIVE` status.
2. **Validation Rule:** End date must be strictly after Start date (minimum tenancy period: 1 month).
3. **Validation Rule:** The selected property must be in `APPROVED` status and not currently bound to another active lease.
4. **Given** an active lease creation,  
   **Then** the property status transitions to `RENTED` to prevent duplicate active tenancies.

---

### US-07: Automated Monthly Rent Due Generation (FR7)
* **As a** Rental Management System,  
* **I want to** automatically calculate and generate sequential monthly rent due entries for the entire lease tenure,  
* **So that** billing schedules are transparent, timely, and free of manual bookkeeping errors.

#### Acceptance Criteria
1. **Given** an active lease with a defined start date and end date (e.g., 12 months),  
   **When** the lease agreement is finalized,  
   **Then** exactly 12 individual `RentDue` entries are generated, each assigned to the 5th of each corresponding month.
2. **Given** each generated rent due entry,  
   **Then** it starts in `PENDING` status with the agreed monthly rent amount.
3. **Given** an overdue due date without payment,  
   **Then** the system marks the due status as `OVERDUE`.

---

### US-08: Tenant Lease Terms & Payment History (FR8)
* **As a** Tenant,  
* **I want to** view my active lease details, upcoming rent dues, and complete historical payment receipts,  
* **So that** I have clear visibility over my tenancy obligations and financial audit trail.

#### Acceptance Criteria
1. **Given** an authenticated Tenant,  
   **When** they access their dashboard,  
   **Then** they see their active lease terms (property address, monthly rent, deposit held, validity period).
2. **Given** upcoming or pending rent dues,  
   **When** the Tenant records a payment,  
   **Then** the corresponding `RentDue` status changes from `PENDING` to `PAID`.
3. **Given** a successful rent payment,  
   **Then** a permanent `Payment` receipt is generated with unique reference number, payment mode, amount, and timestamp.

---

## 4. Estimation & Story Points (Fibonacci Scale)

| User Story | Complexity & Scope Rationale | Story Points |
| :--- | :--- | :---: |
| **US-01 (Owner Listing CRUD)** | Form validation, status transitions, builder pattern | **5** |
| **US-02 (Admin Moderation)** | Queue management, FIFO processing, approval states | **3** |
| **US-03 (Dynamic Search)** | Multi-predicate filtering, Strategy pattern composition | **5** |
| **US-04 (Seeker Shortlist)** | Deduplication set, user-property mapping | **3** |
| **US-05 (Visits & Enquiries)** | Bidirectional interaction, state machine, notes | **5** |
| **US-06 (Lease Generation)** | Multi-entity validation, date calculations, property lock | **5** |
| **US-07 (Rent Due Engine)** | Date arithmetic (`java.time`), batch schedule generation | **5** |
| **US-08 (Tenant View & Receipts)** | Payment ledger, receipt generation, status reconciliation | **3** |
| **Total Sprint Backlog** | **Complete Core Functional Scope** | **34 SP** |

---

## 5. Definition of Ready (DoR)

A User Story is considered **Ready for Implementation** when:
- [x] Story is written in standard Persona format (*As a... I want to... So that...*).
- [x] Clear Acceptance Criteria are documented using Given-When-Then.
- [x] Domain business rules and validation constraints are explicitly identified.
- [x] Dependencies on other stories/entities are documented.
- [x] Story points have been estimated and agreed upon by the team.
- [x] Test scenarios and expected edge cases are defined.

---

## 6. Definition of Done (DoD)

A User Story is marked **Done** and accepted for release only when:
- [x] **Code Implementation**: Code written in clean, modern Java (Java 21 standards) adhering to SOLID principles.
- [x] **Compilation**: Code compiles cleanly with zero Maven warnings or errors (`mvn clean compile`).
- [x] **Automated Unit Testing**: Unit and integration tests written using JUnit 5; all tests passing (`mvn test`).
- [x] **Code Coverage**: Minimum 80% line and branch coverage across core business logic.
- [x] **Error Handling**: Custom exceptions thrown and handled gracefully with user-friendly recovery.
- [x] **Documentation**: Code-level Javadoc comments and architectural documentation updated.
- [x] **Code Review**: Verified against HCL rubric criteria and peer-reviewed with clean commit message.

---

## 7. Sprint Backlog & Kanban Board Workflow

```mermaid
kanban
  Backlog
    [US-07: Monthly Rent Due Engine (5 SP)]
    [US-08: Tenant View & Payment Receipts (3 SP)]
  Ready for Dev
    [US-05: Site Visits & Enquiries (5 SP)]
    [US-06: Lease Agreement Creation (5 SP)]
  In Progress
    [US-03: Dynamic Search & Filter (5 SP)]
    [US-04: Seeker Favourites (3 SP)]
  In Review
    [US-02: Admin Moderation Queue (3 SP)]
  Done
    [US-01: Owner Listing Management (5 SP)]
```

### GitHub Project Backlog Board Link
* **Repository:** [https://github.com/Suganth1795/HCL-project](https://github.com/Suganth1795/HCL-project)
* **Backlog Tracking:** Managed via repository Markdown specifications and GitHub Issues / Project Board.
