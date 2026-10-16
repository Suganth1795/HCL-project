package com.hcl.realestate.core.model;

import com.hcl.realestate.core.util.Constants;

import java.util.Objects;

/**
 * Represents a Property listing in the Real Estate platform.
 * Demonstrates:
 * - Inheritance (extends BaseEntity for identity and audit fields)
 * - Encapsulation (private fields, controlled access via getters and setters)
 * - Constructors (default and parameterized using super)
 * - Comparable interface (natural sorting by monthly rent)
 */
public class Property extends BaseEntity implements Comparable<Property> {

    private Long ownerId;
    private String title;
    private String description;
    private String propertyType;
    private String city;
    private String locality;
    private int bhk;
    private double monthlyRent;
    private double depositAmount;
    private String status;

    /**
     * Default no-arg constructor.
     */
    public Property() {
        super();
        this.status = Constants.STATUS_DRAFT;
    }

    /**
     * Parameterized constructor for complete property initialization.
     */
    public Property(Long id, Long ownerId, String title, String description,
                    String propertyType, String city, String locality,
                    int bhk, double monthlyRent, double depositAmount, String status) {
        super(id);
        this.ownerId = ownerId;
        this.title = title;
        this.description = description;
        this.propertyType = propertyType;
        this.city = city;
        this.locality = locality;
        this.bhk = bhk;
        this.monthlyRent = monthlyRent;
        this.depositAmount = depositAmount;
        this.status = (status != null) ? status : Constants.STATUS_DRAFT;
    }

    // --- Getters and Setters (Encapsulation) ---

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public int getBhk() {
        return bhk;
    }

    public void setBhk(int bhk) {
        this.bhk = bhk;
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
     * Natural ordering for Property objects: sorted by monthlyRent ascending.
     * If rent is identical, breaks tie by title.
     */
    @Override
    public int compareTo(Property other) {
        if (other == null) {
            return 1;
        }
        int rentComparison = Double.compare(this.monthlyRent, other.monthlyRent);
        if (rentComparison != 0) {
            return rentComparison;
        }
        if (this.title != null && other.title != null) {
            return this.title.compareToIgnoreCase(other.title);
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property property = (Property) o;
        if (getId() != null && property.getId() != null) {
            return Objects.equals(getId(), property.getId());
        }
        return Objects.equals(title, property.title) && Objects.equals(city, property.city);
    }

    @Override
    public int hashCode() {
        return getId() != null ? Objects.hash(getId()) : Objects.hash(title, city);
    }

    @Override
    public String toString() {
        return "Property{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", city='" + city + '\'' +
                ", locality='" + locality + '\'' +
                ", type='" + propertyType + '\'' +
                ", bhk=" + bhk +
                ", rent=INR " + String.format("%,.2f", monthlyRent) +
                ", deposit=INR " + String.format("%,.2f", depositAmount) +
                ", status='" + status + '\'' +
                '}';
    }
}