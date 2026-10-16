package com.hcl.realestate.core.service;

import com.hcl.realestate.core.model.Property;
import com.hcl.realestate.core.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Service demonstrating Queue collection (A1-COL-04) for FIFO moderation of pending listings.
 * Administrators review and process submissions in strictly the order they arrived.
 */
public class ApprovalQueueService {

    private final Queue<Property> pendingQueue = new LinkedBlockingQueue<>();

    /**
     * Enqueues a property listing for administrator review.
     * Sets status to PENDING_REVIEW if not already set.
     *
     * @param property the listing to enqueue
     */
    public void enqueue(Property property) {
        if (property != null) {
            property.setStatus(Constants.STATUS_PENDING_REVIEW);
            pendingQueue.offer(property);
        }
    }

    /**
     * Retrieves and removes the oldest pending property from the head of the queue (FIFO).
     *
     * @return Optional containing the next property, or empty if queue is empty
     */
    public Optional<Property> pollNext() {
        return Optional.ofNullable(pendingQueue.poll());
    }

    /**
     * Inspects the next pending property without removing it from the queue.
     *
     * @return Optional containing the head property, or empty if queue is empty
     */
    public Optional<Property> peekNext() {
        return Optional.ofNullable(pendingQueue.peek());
    }

    /**
     * Returns the current number of submissions waiting in the moderation queue.
     *
     * @return pending queue size
     */
    public int getQueueSize() {
        return pendingQueue.size();
    }

    /**
     * Checks if the moderation queue has no pending items.
     *
     * @return true if empty; false otherwise
     */
    public boolean isEmpty() {
        return pendingQueue.isEmpty();
    }

    /**
     * Returns an unmodifiable snapshot list of items currently in the queue.
     *
     * @return snapshot List of queued properties
     */
    public List<Property> getQueueSnapshot() {
        return new ArrayList<>(pendingQueue);
    }
}
