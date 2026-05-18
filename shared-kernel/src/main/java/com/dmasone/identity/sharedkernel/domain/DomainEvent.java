package com.dmasone.identity.sharedkernel.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Marker contract for events that describe something already decided by a
 * module. Events are intentionally small immutable facts so other modules can
 * react without reaching into the publisher's persistence model.
 */
public interface DomainEvent extends Serializable {

    /**
     * Stable event identifier useful for logging, tracing, and future
     * idempotency concerns.
     *
     * @return unique event identifier
     */
    UUID eventId();

    /**
     * Time at which the event fact was created.
     *
     * @return event creation timestamp
     */
    Instant occurredAt();
}
