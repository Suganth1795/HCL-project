package com.hcl.realestate.core.repository;

import com.hcl.realestate.core.model.BaseEntity;
import com.hcl.realestate.core.model.User;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Concrete in-memory repository for User entities.
 */
public class InMemoryUserRepository extends InMemoryGenericRepository<User, Long> implements UserRepository {

    private final AtomicLong sequence;

    public InMemoryUserRepository() {
        this(new AtomicLong(10L));
    }

    public InMemoryUserRepository(AtomicLong sequence) {
        super(BaseEntity::getId, BaseEntity::setId, sequence::incrementAndGet);
        this.sequence = sequence;
    }

    @Override
    public User save(User entity) {
        User saved = super.save(entity);
        if (saved.getId() != null && saved.getId() >= sequence.get()) {
            sequence.set(saved.getId() + 1);
        }
        return saved;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return findAll().stream()
                .filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email.trim()))
                .findFirst();
    }

    @Override
    public List<User> findByRole(String role) {
        if (role == null) return List.of();
        return findAll().stream()
                .filter(u -> u.getRole() != null && u.getRole().equalsIgnoreCase(role.trim()))
                .toList();
    }
}
