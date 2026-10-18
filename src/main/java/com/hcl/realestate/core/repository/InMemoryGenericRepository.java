package com.hcl.realestate.core.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Reusable, thread-safe in-memory implementation of CrudRepository.
 * Uses ConcurrentHashMap for lock-free O(1) entity lookups, mutations, and removals.
 * Demonstrates:
 * - Generic type parameters &lt;T, ID&gt;
 * - Functional interfaces (Function, BiConsumer, Supplier) for entity property manipulation
 * - Thread-safe state management via ConcurrentHashMap
 * - Defensive copies to ensure immutability of returned query results
 *
 * @param <T>  the domain entity type
 * @param <ID> the entity identifier type
 */
public class InMemoryGenericRepository<T, ID> implements CrudRepository<T, ID> {

    protected final Map<ID, T> storage = new ConcurrentHashMap<>();
    private final Function<T, ID> idExtractor;
    private final BiConsumer<T, ID> idSetter;
    private final Supplier<ID> idGenerator;

    /**
     * Constructs a generic in-memory repository with lambda functional hooks.
     *
     * @param idExtractor function to extract ID from entity instance
     * @param idSetter    biconsumer to set generated ID onto entity instance
     * @param idGenerator supplier providing sequentially unique IDs
     */
    public InMemoryGenericRepository(Function<T, ID> idExtractor,
                                     BiConsumer<T, ID> idSetter,
                                     Supplier<ID> idGenerator) {
        this.idExtractor = Objects.requireNonNull(idExtractor, "idExtractor must not be null");
        this.idSetter = Objects.requireNonNull(idSetter, "idSetter must not be null");
        this.idGenerator = Objects.requireNonNull(idGenerator, "idGenerator must not be null");
    }

    @Override
    public T save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity to save cannot be null.");
        }

        ID id = idExtractor.apply(entity);
        if (id == null || (id instanceof Number num && num.longValue() <= 0)) {
            id = idGenerator.get();
            idSetter.accept(entity, id);
        }

        storage.put(id, entity);
        return entity;
    }

    @Override
    public Optional<T> findById(ID id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<T> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(storage.values()));
    }

    @Override
    public boolean deleteById(ID id) {
        if (id == null) {
            return false;
        }
        return storage.remove(id) != null;
    }

    @Override
    public boolean existsById(ID id) {
        if (id == null) {
            return false;
        }
        return storage.containsKey(id);
    }

    @Override
    public long count() {
        return storage.size();
    }

    @Override
    public void deleteAll() {
        storage.clear();
    }
}
