package com.hcl.realestate.core.repository;

import com.hcl.realestate.core.model.BaseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Generic In-Memory Repository Tests")
class InMemoryGenericRepositoryTest {

    // Simple test domain entity extending BaseEntity
    static class DummyEntity extends BaseEntity {
        private String name;

        DummyEntity(Long id, String name) {
            super(id);
            this.name = name;
        }

        String getName() {
            return name;
        }

        void setName(String name) {
            this.name = name;
        }
    }

    private InMemoryGenericRepository<DummyEntity, Long> repository;
    private AtomicLong idGen;

    @BeforeEach
    void setUp() {
        idGen = new AtomicLong(100L);
        repository = new InMemoryGenericRepository<>(
                BaseEntity::getId,
                BaseEntity::setId,
                idGen::incrementAndGet
        );
    }

    @Test
    @DisplayName("save generates new ID when entity has null ID")
    void testSaveGeneratesIdWhenNull() {
        DummyEntity entity = new DummyEntity(null, "Test Item");
        DummyEntity saved = repository.save(entity);

        assertNotNull(saved.getId(), "Generated ID should not be null");
        assertEquals(101L, saved.getId());
        assertEquals(1, repository.count());
    }

    @Test
    @DisplayName("save preserves pre-existing ID")
    void testSavePreservesExistingId() {
        DummyEntity entity = new DummyEntity(42L, "Preset ID Item");
        DummyEntity saved = repository.save(entity);

        assertEquals(42L, saved.getId(), "Should preserve preset ID");
        assertEquals(1, repository.count());
    }

    @Test
    @DisplayName("save throws IllegalArgumentException for null entity")
    void testSaveThrowsForNullEntity() {
        assertThrows(IllegalArgumentException.class, () -> repository.save(null));
    }

    @Test
    @DisplayName("findById retrieves existing entity and empty for non-existent ID")
    void testFindById() {
        DummyEntity e1 = repository.save(new DummyEntity(null, "First"));
        DummyEntity e2 = repository.save(new DummyEntity(null, "Second"));

        Optional<DummyEntity> found = repository.findById(e1.getId());
        assertTrue(found.isPresent(), "Entity should be found");
        assertEquals("First", found.get().getName());

        Optional<DummyEntity> missing = repository.findById(999L);
        assertTrue(missing.isEmpty(), "Nonexistent ID should return empty Optional");

        Optional<DummyEntity> nullQuery = repository.findById(null);
        assertTrue(nullQuery.isEmpty(), "Null ID should return empty Optional");
    }

    @Test
    @DisplayName("findAll returns unmodifiable defensive copy of all stored entities")
    void testFindAll() {
        repository.save(new DummyEntity(null, "Item A"));
        repository.save(new DummyEntity(null, "Item B"));
        repository.save(new DummyEntity(null, "Item C"));

        List<DummyEntity> all = repository.findAll();
        assertEquals(3, all.size());

        assertThrows(UnsupportedOperationException.class, () -> all.add(new DummyEntity(null, "Illegal")));
    }

    @Test
    @DisplayName("deleteById removes entity and returns true; returns false if absent")
    void testDeleteById() {
        DummyEntity e = repository.save(new DummyEntity(null, "Deletable"));
        Long id = e.getId();

        assertTrue(repository.existsById(id));
        boolean removed = repository.deleteById(id);
        assertTrue(removed, "deleteById should return true for existing entity");
        assertFalse(repository.existsById(id));
        assertEquals(0, repository.count());

        assertFalse(repository.deleteById(id), "Second delete should return false");
        assertFalse(repository.deleteById(null), "Delete null ID should return false");
    }

    @Test
    @DisplayName("deleteAll removes all entries and resets count to zero")
    void testDeleteAll() {
        repository.save(new DummyEntity(null, "E1"));
        repository.save(new DummyEntity(null, "E2"));
        assertEquals(2, repository.count());

        repository.deleteAll();
        assertEquals(0, repository.count());
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    @DisplayName("Concurrent saves and reads are thread-safe")
    void testConcurrentThreadSafety() throws InterruptedException {
        int threadCount = 20;
        int itemsPerThread = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int threadIdx = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < itemsPerThread; j++) {
                        repository.save(new DummyEntity(null, "Thread-" + threadIdx + "-Item-" + j));
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(completed, "All threads should complete within timeout");
        assertEquals(threadCount * itemsPerThread, repository.count(), "All items should be safely persisted");
    }
}
