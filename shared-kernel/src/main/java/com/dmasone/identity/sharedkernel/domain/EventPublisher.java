package com.dmasone.identity.sharedkernel.domain;

/**
 * Small publishing port used by application services to emit domain events
 * without coupling those services to Spring's concrete event infrastructure.
 */
public interface EventPublisher {

    /**
     * Publish a domain event inside the current application process.
     *
     * @param event immutable event fact
     */
    void publish(DomainEvent event);
}
