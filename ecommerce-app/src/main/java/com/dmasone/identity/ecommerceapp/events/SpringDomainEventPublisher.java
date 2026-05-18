package com.dmasone.identity.ecommerceapp.events;

import com.dmasone.identity.sharedkernel.domain.DomainEvent;
import com.dmasone.identity.sharedkernel.domain.EventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Adapter from the shared event publishing port to Spring's in-process event
 * bus. This keeps domain modules free from a hard dependency on Spring events.
 */
@Component
public class SpringDomainEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public SpringDomainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
