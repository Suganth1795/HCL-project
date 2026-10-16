package com.hcl.realestate.core.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Abstract base class providing common audit fields and identity for domain entities.
 * Demonstrates:
 * - Abstraction (A1-OOP-09)
 * - Encapsulation (A1-OOP-02)
 * - Object identity (equals and hashCode based on ID)
 */
public abstract class BaseEntity {

    private Long id;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Default constructor: initializes creation and last updated timestamps.
     */
    protected BaseEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Parameterized constructor initializing ID and timestamps.
     *
     * @param id unique identifier of the entity
     */
    protected BaseEntity(Long id) {
        this();
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
