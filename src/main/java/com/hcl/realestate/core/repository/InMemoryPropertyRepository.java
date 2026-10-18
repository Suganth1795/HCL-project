package com.hcl.realestate.core.repository;

import com.hcl.realestate.core.model.BaseEntity;
import com.hcl.realestate.core.model.Property;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Concrete in-memory repository for Property entities.
 * Inherits generic CRUD mechanisms from InMemoryGenericRepository and implements
 * specialized domain search operations using Java Streams.
 */
public class InMemoryPropertyRepository extends InMemoryGenericRepository<Property, Long> implements PropertyRepository {

    private final AtomicLong sequence;

    public InMemoryPropertyRepository() {
        this(new AtomicLong(100L));
    }

    public InMemoryPropertyRepository(AtomicLong sequence) {
        super(BaseEntity::getId, BaseEntity::setId, sequence::incrementAndGet);
        this.sequence = sequence;
    }

    @Override
    public Property save(Property entity) {
        Property saved = super.save(entity);
        if (saved.getId() != null && saved.getId() >= sequence.get()) {
            sequence.set(saved.getId() + 1);
        }
        return saved;
    }

    @Override
    public List<Property> findByCity(String city) {
        if (city == null) return List.of();
        return findAll().stream()
                .filter(p -> p.getCity() != null && p.getCity().equalsIgnoreCase(city.trim()))
                .toList();
    }

    @Override
    public List<Property> findByOwnerId(Long ownerId) {
        if (ownerId == null) return List.of();
        return findAll().stream()
                .filter(p -> p.getOwnerId() != null && p.getOwnerId().equals(ownerId))
                .toList();
    }

    @Override
    public List<Property> findByStatus(String status) {
        if (status == null) return List.of();
        return findAll().stream()
                .filter(p -> p.getStatus() != null && p.getStatus().equalsIgnoreCase(status.trim()))
                .toList();
    }

    @Override
    public List<Property> findByPriceRange(double minRent, double maxRent) {
        return findAll().stream()
                .filter(p -> p.getMonthlyRent() >= minRent && p.getMonthlyRent() <= maxRent)
                .toList();
    }
}
