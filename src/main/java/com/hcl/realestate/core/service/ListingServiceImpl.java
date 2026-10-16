package com.hcl.realestate.core.service;

import com.hcl.realestate.core.dto.SearchCriteria;
import com.hcl.realestate.core.exception.InvalidStateTransitionException;
import com.hcl.realestate.core.exception.ListingValidationException;
import com.hcl.realestate.core.exception.PropertyNotFoundException;
import com.hcl.realestate.core.model.Enquiry;
import com.hcl.realestate.core.model.Lease;
import com.hcl.realestate.core.model.Property;
import com.hcl.realestate.core.model.User;
import com.hcl.realestate.core.model.VisitRequest;
import com.hcl.realestate.core.strategy.CitySearchStrategy;
import com.hcl.realestate.core.strategy.PriceRangeSearchStrategy;
import com.hcl.realestate.core.strategy.SearchStrategy;
import com.hcl.realestate.core.util.Constants;
import com.hcl.realestate.core.util.PropertyComparators;
import com.hcl.realestate.core.validation.BusinessRuleEnforcer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Concrete implementation of ListingService managing FR1 through FR6.
 * Demonstrates:
 * - List (ArrayList) for property catalogs and search results
 * - Set (LinkedHashSet) for unique seeker favourites and available cities
 * - Map (HashMap, TreeMap) for city grouping, user favourites, inquiries, visits, and active leases
 * - Queue (LinkedBlockingQueue via ApprovalQueueService) for FIFO admin moderation
 * - Comparator (PropertyComparators) for multi-field and rent-based sorting
 */
public class ListingServiceImpl implements ListingService {

    private final List<Property> catalog = new ArrayList<>();
    private final Set<Property> favourites = new LinkedHashSet<>();
    private final Map<Long, Set<Property>> userFavourites = new HashMap<>();
    private final Map<Long, List<Enquiry>> enquiriesByProperty = new HashMap<>();
    private final Map<Long, List<VisitRequest>> visitsByProperty = new HashMap<>();
    private final Map<Long, Lease> activeLeasesByProperty = new HashMap<>();

    private final ApprovalQueueService approvalQueueService;
    private final SearchStrategy citySearchStrategy = new CitySearchStrategy();
    private final SearchStrategy priceRangeSearchStrategy = new PriceRangeSearchStrategy();

    private final AtomicLong propertyIdGen = new AtomicLong(100L);
    private final AtomicLong enquiryIdGen = new AtomicLong(500L);
    private final AtomicLong visitIdGen = new AtomicLong(600L);
    private final AtomicLong leaseIdGen = new AtomicLong(700L);

    public ListingServiceImpl() {
        this.approvalQueueService = new ApprovalQueueService();
    }

    public ListingServiceImpl(ApprovalQueueService approvalQueueService) {
        this.approvalQueueService = (approvalQueueService != null) ? approvalQueueService : new ApprovalQueueService();
    }

    // --- FR1: Owner Listing Management ---

    @Override
    public Property createListing(Property property) throws ListingValidationException {
        if (property == null) {
            throw new ListingValidationException("Property to create cannot be null.");
        }

        if (property.getId() == null || property.getId() <= 0) {
            property.setId(propertyIdGen.incrementAndGet());
        }

        BusinessRuleEnforcer.validateListingInvariants(property);

        property.setStatus(Constants.STATUS_PENDING_REVIEW);
        catalog.add(property);
        approvalQueueService.enqueue(property);

        return property;
    }

    @Override
    public List<Property> getListingsByOwner(Long ownerId) {
        if (ownerId == null) {
            return Collections.emptyList();
        }
        List<Property> ownerListings = new ArrayList<>();
        for (Property p : catalog) {
            if (p.getOwnerId() != null && p.getOwnerId().equals(ownerId)) {
                ownerListings.add(p);
            }
        }
        return ownerListings;
    }

    @Override
    public Property deactivateListing(Long propertyId, User actor) {
        Property property = findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException(propertyId));

        BusinessRuleEnforcer.verifyOwnershipAuthorization(property, actor);

        property.setStatus(Constants.STATUS_DEACTIVATED);
        return property;
    }

    // --- FR2: Admin Listing Moderation (Queue) ---

    @Override
    public Property approveListing(Long propertyId) {
        Property property = findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException(propertyId));

        Optional<Property> head = approvalQueueService.peekNext();
        if (head.isPresent() && head.get().getId().equals(propertyId)) {
            approvalQueueService.pollNext();
        }

        property.setStatus(Constants.STATUS_APPROVED);
        return property;
    }

    @Override
    public Property rejectListing(Long propertyId, String reason) {
        Property property = findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException(propertyId));

        Optional<Property> head = approvalQueueService.peekNext();
        if (head.isPresent() && head.get().getId().equals(propertyId)) {
            approvalQueueService.pollNext();
        }

        property.setStatus(Constants.STATUS_REJECTED);
        return property;
    }

    @Override
    public Optional<Property> getNextPendingForModeration() {
        return approvalQueueService.peekNext();
    }

    @Override
    public int getPendingQueueSize() {
        return approvalQueueService.getQueueSize();
    }

    // --- FR3: Seeker Dynamic Property Search & Favourites ---

    @Override
    public List<Property> search(SearchCriteria criteria) {
        List<Property> results = new ArrayList<>();
        for (Property p : catalog) {
            if (Constants.STATUS_APPROVED.equalsIgnoreCase(p.getStatus())) {
                if (citySearchStrategy.matches(p, criteria) && priceRangeSearchStrategy.matches(p, criteria)) {
                    results.add(p);
                }
            }
        }
        return results;
    }

    @Override
    public boolean addToFavourites(Property property) {
        if (property == null) return false;
        return favourites.add(property);
    }

    @Override
    public boolean removeFromFavourites(Long propertyId) {
        if (propertyId == null) return false;
        return favourites.removeIf(p -> p.getId() != null && p.getId().equals(propertyId));
    }

    @Override
    public Set<Property> getFavourites() {
        return Collections.unmodifiableSet(favourites);
    }

    @Override
    public Set<String> getAvailableCities() {
        Set<String> cities = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Property p : catalog) {
            if (Constants.STATUS_APPROVED.equalsIgnoreCase(p.getStatus()) && p.getCity() != null) {
                cities.add(p.getCity().trim());
            }
        }
        return cities;
    }

    @Override
    public Optional<Property> findById(Long id) {
        if (id == null) return Optional.empty();
        for (Property p : catalog) {
            if (p.getId() != null && p.getId().equals(id)) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Property> getAllListings() {
        return Collections.unmodifiableList(catalog);
    }

    public void seedApprovedProperty(Property property) {
        if (property != null) {
            if (property.getId() == null) {
                property.setId(propertyIdGen.incrementAndGet());
            }
            property.setStatus(Constants.STATUS_APPROVED);
            catalog.add(property);
        }
    }

    // --- FR4: Seeker Favourites & Shortlists (Map<Long, Set<Property>>) ---

    @Override
    public boolean addFavourite(Long userId, Property property) {
        if (userId == null || property == null) return false;
        Set<Property> userSet = userFavourites.computeIfAbsent(userId, k -> new LinkedHashSet<>());
        addToFavourites(property); // sync global
        return userSet.add(property);
    }

    @Override
    public boolean removeFavourite(Long userId, Long propertyId) {
        if (userId == null || propertyId == null) return false;
        Set<Property> userSet = userFavourites.get(userId);
        if (userSet != null) {
            return userSet.removeIf(p -> p.getId() != null && p.getId().equals(propertyId));
        }
        return false;
    }

    @Override
    public Set<Property> getFavourites(Long userId) {
        if (userId == null) return Collections.emptySet();
        return Collections.unmodifiableSet(userFavourites.getOrDefault(userId, Collections.emptySet()));
    }

    @Override
    public Map<Long, Set<Property>> getAllFavouritesByUser() {
        return Collections.unmodifiableMap(userFavourites);
    }

    // --- FR5: Site Visits & Inquiries (Maps) ---

    @Override
    public Enquiry sendEnquiry(Long propertyId, Long seekerId, String message) {
        Property property = findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException(propertyId));

        if (message == null || message.trim().isBlank()) {
            throw new IllegalArgumentException("Inquiry message cannot be blank.");
        }

        Enquiry enquiry = Enquiry.create(enquiryIdGen.incrementAndGet(), propertyId, seekerId, message.trim());
        enquiriesByProperty.computeIfAbsent(propertyId, k -> new ArrayList<>()).add(enquiry);
        return enquiry;
    }

    @Override
    public Enquiry respondToEnquiry(Long propertyId, Long enquiryId, String response) {
        List<Enquiry> list = enquiriesByProperty.get(propertyId);
        if (list == null) {
            throw new IllegalArgumentException("No inquiries found for property ID: " + propertyId);
        }

        for (int i = 0; i < list.size(); i++) {
            Enquiry e = list.get(i);
            if (e.id().equals(enquiryId)) {
                Enquiry updated = e.withResponse(response);
                list.set(i, updated);
                return updated;
            }
        }
        throw new IllegalArgumentException("Enquiry ID " + enquiryId + " not found for property ID " + propertyId);
    }

    @Override
    public List<Enquiry> getEnquiriesForProperty(Long propertyId) {
        return Collections.unmodifiableList(enquiriesByProperty.getOrDefault(propertyId, Collections.emptyList()));
    }

    @Override
    public VisitRequest requestSiteVisit(Long propertyId, Long seekerId, LocalDateTime preferredTime) {
        Property property = findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException(propertyId));

        LocalDateTime visitTime = (preferredTime != null) ? preferredTime : LocalDateTime.now().plusDays(2);
        VisitRequest request = VisitRequest.create(visitIdGen.incrementAndGet(), propertyId, seekerId, visitTime);
        visitsByProperty.computeIfAbsent(propertyId, k -> new ArrayList<>()).add(request);
        return request;
    }

    @Override
    public VisitRequest confirmSiteVisit(Long propertyId, Long visitId, String ownerNotes) {
        List<VisitRequest> list = visitsByProperty.get(propertyId);
        if (list == null) {
            throw new IllegalArgumentException("No visit requests found for property ID: " + propertyId);
        }

        for (int i = 0; i < list.size(); i++) {
            VisitRequest vr = list.get(i);
            if (vr.id().equals(visitId)) {
                VisitRequest confirmed = vr.confirm(ownerNotes);
                list.set(i, confirmed);
                return confirmed;
            }
        }
        throw new IllegalArgumentException("Visit request ID " + visitId + " not found for property ID " + propertyId);
    }

    @Override
    public List<VisitRequest> getVisitRequestsForProperty(Long propertyId) {
        return Collections.unmodifiableList(visitsByProperty.getOrDefault(propertyId, Collections.emptyList()));
    }

    // --- FR6: Lease Agreement Creation (Map<Long, Lease>) ---

    @Override
    public Lease createLease(Lease lease, User actor) throws ListingValidationException {
        if (lease == null) {
            throw new IllegalArgumentException("Lease contract cannot be null.");
        }

        Property property = findById(lease.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException(lease.getPropertyId()));

        // Enforce Business Rule 2A: Ownership Authorization
        BusinessRuleEnforcer.verifyOwnershipAuthorization(property, actor);

        // Enforce Business Rule 2B: State Transition check
        BusinessRuleEnforcer.verifyCanCreateLease(property);

        // Check if property is already leased
        if (activeLeasesByProperty.containsKey(property.getId())) {
            throw new InvalidStateTransitionException(
                    property.getStatus(),
                    Constants.STATUS_RENTED,
                    "Property already has an active lease agreement: Lease ID " + activeLeasesByProperty.get(property.getId()).getId());
        }

        // Validate financial invariants on lease
        if (lease.getMonthlyRent() < Constants.MIN_MONTHLY_RENT || lease.getMonthlyRent() > Constants.MAX_MONTHLY_RENT) {
            throw new ListingValidationException("monthlyRent",
                    "Lease rent must be within valid platform bounds [INR " + Constants.MIN_MONTHLY_RENT + ", INR " + Constants.MAX_MONTHLY_RENT + "]");
        }

        if (lease.getId() == null || lease.getId() <= 0) {
            lease.setId(leaseIdGen.incrementAndGet());
        }

        // Associate lease and transition property status
        activeLeasesByProperty.put(property.getId(), lease);
        property.setStatus(Constants.STATUS_RENTED);

        return lease;
    }

    @Override
    public Optional<Lease> getActiveLeaseForProperty(Long propertyId) {
        return Optional.ofNullable(activeLeasesByProperty.get(propertyId));
    }

    @Override
    public List<Lease> getLeasesForTenant(Long tenantId) {
        if (tenantId == null) return Collections.emptyList();
        List<Lease> result = new ArrayList<>();
        for (Lease l : activeLeasesByProperty.values()) {
            if (l.getTenantId() != null && l.getTenantId().equals(tenantId)) {
                result.add(l);
            }
        }
        return result;
    }

    @Override
    public Map<Long, Lease> getActiveLeases() {
        return Collections.unmodifiableMap(activeLeasesByProperty);
    }

    // --- Reports & Sorting (Map<String, List<Property>> & Comparator<Property>) ---

    @Override
    public Map<String, List<Property>> groupPropertiesByCity() {
        Map<String, List<Property>> grouped = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Property p : catalog) {
            if (Constants.STATUS_APPROVED.equalsIgnoreCase(p.getStatus()) && p.getCity() != null) {
                grouped.computeIfAbsent(p.getCity().trim(), k -> new ArrayList<>()).add(p);
            }
        }
        return grouped;
    }

    @Override
    public String generateSortedCityReport(Comparator<Property> comparator) {
        Comparator<Property> sortComp = (comparator != null) ? comparator : PropertyComparators.BY_RENT_ASC;
        Map<String, List<Property>> cityGroups = groupPropertiesByCity();

        StringBuilder sb = new StringBuilder();
        sb.append("=========================================================================================\n");
        sb.append("                 REAL ESTATE VERIFIED PROPERTIES REPORT (GROUPED BY CITY)               \n");
        sb.append("=========================================================================================\n");

        if (cityGroups.isEmpty()) {
            sb.append("No active approved properties found in catalog.\n");
            return sb.toString();
        }

        double overallRentSum = 0;
        int overallPropertyCount = 0;

        for (Map.Entry<String, List<Property>> entry : cityGroups.entrySet()) {
            String city = entry.getKey();
            List<Property> properties = new ArrayList<>(entry.getValue());
            properties.sort(sortComp);

            double cityRentSum = 0;
            double minRent = Double.MAX_VALUE;
            double maxRent = Double.MIN_VALUE;

            sb.append(String.format("\nCITY: %s (%d Listings)\n", city.toUpperCase(), properties.size()));
            sb.append(String.format(" %-6s | %-28s | %-12s | %-4s | %-16s | %-16s\n",
                    "ID", "Title", "Locality", "BHK", "Rent (INR)", "Deposit (INR)"));
            sb.append("-----------------------------------------------------------------------------------------\n");

            for (Property p : properties) {
                sb.append(String.format(" #%-5d | %-28s | %-12s | %-4d | INR %-12s | INR %-12s\n",
                        p.getId(),
                        truncate(p.getTitle(), 28),
                        truncate(p.getLocality() != null ? p.getLocality() : "N/A", 12),
                        p.getBhk(),
                        String.format("%,.2f", p.getMonthlyRent()),
                        String.format("%,.2f", p.getDepositAmount())));

                cityRentSum += p.getMonthlyRent();
                if (p.getMonthlyRent() < minRent) minRent = p.getMonthlyRent();
                if (p.getMonthlyRent() > maxRent) maxRent = p.getMonthlyRent();
            }

            double cityAvg = cityRentSum / properties.size();
            sb.append("-----------------------------------------------------------------------------------------\n");
            sb.append(String.format(" City Summary: Avg Rent: INR %,.2f | Min: INR %,.2f | Max: INR %,.2f\n",
                    cityAvg, minRent, maxRent));

            overallRentSum += cityRentSum;
            overallPropertyCount += properties.size();
        }

        double overallAvg = overallPropertyCount > 0 ? (overallRentSum / overallPropertyCount) : 0;
        sb.append("\n=========================================================================================\n");
        sb.append(String.format(" OVERALL PORTFOLIO: %d Verified Listings across %d Cities | Portfolio Avg Rent: INR %,.2f\n",
                overallPropertyCount, cityGroups.size(), overallAvg));
        sb.append("=========================================================================================\n");

        return sb.toString();
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        if (text.length() <= maxLen) return text;
        return text.substring(0, maxLen - 3) + "...";
    }
}
