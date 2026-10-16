package com.hcl.realestate.core.model;

import com.hcl.realestate.core.util.Constants;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/**
 * Represents a binding Rental Lease contract between an Owner and a Tenant.
 * Demonstrates:
 * - Inheritance (extends BaseEntity)
 * - Encapsulation (private fields, controlled getters and setters)
 * - Modern Java Date-Time API (java.time.LocalDate, Period)
 * - Constructors (default and parameterized using super)
 * - Object identity (equals and hashCode based on ID)
 */
public class Lease extends BaseEntity {

    private Long propertyId;
    private Long tenantId;
    private Long ownerId;
    private LocalDate startDate;
    private LocalDate endDate;
    private double monthlyRent;
    private double depositAmount;
    private String status;

    /**
     * Default constructor.
     */
    public Lease() {
        super();
        this.status = Constants.LEASE_ACTIVE;
    }

    /**
     * Parameterized constructor.
     */
    public Lease(Long id, Long propertyId, Long tenantId, Long ownerId,
                 LocalDate startDate, LocalDate endDate,
                 double monthlyRent, double depositAmount, String status) {
        super(id);
        this.propertyId = propertyId;
        this.tenantId = tenantId;
        this.ownerId = ownerId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.monthlyRent = monthlyRent;
        this.depositAmount = depositAmount;
        this.status = (status != null) ? status : Constants.LEASE_ACTIVE;
    }

    // --- Getters and Setters (Encapsulation) ---

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public double getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(double monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public double getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(double depositAmount) {
        this.depositAmount = depositAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Calculates the duration of the lease in months using java.time.Period.
     *
     * @return the total number of whole months in the tenancy period
     */
    public int getDurationInMonths() {
        if (startDate == null || endDate == null) {
            return 0;
        }
        Period period = Period.between(startDate, endDate);
        return period.getYears() * 12 + period.getMonths();
    }

    /**
     * Helper method to determine if the lease is currently active.
     */
    public boolean isActive() {
        return Constants.LEASE_ACTIVE.equalsIgnoreCase(this.status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lease lease = (Lease) o;
        if (getId() != null && lease.getId() != null) {
            return Objects.equals(getId(), lease.getId());
        }
        return Objects.equals(propertyId, lease.propertyId) &&
                Objects.equals(tenantId, lease.tenantId) &&
                Objects.equals(startDate, lease.startDate);
    }

    @Override
    public int hashCode() {
        return getId() != null ? Objects.hash(getId()) : Objects.hash(propertyId, tenantId, startDate);
    }

    @Override
    public String toString() {
        return "Lease{" +
                "id=" + getId() +
                ", propertyId=" + propertyId +
                ", tenantId=" + tenantId +
                ", ownerId=" + ownerId +
                ", tenure=" + startDate + " to " + endDate + " (" + getDurationInMonths() + " months)" +
                ", rent=INR " + String.format("%,.2f", monthlyRent) +
                ", deposit=INR " + String.format("%,.2f", depositAmount) +
                ", status='" + status + '\'' +
                '}';
    }
}