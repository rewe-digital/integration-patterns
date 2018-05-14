package com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing;

import javax.inject.Inject;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class DomainEventPublishScheduler<P extends EventPayload, E extends DomainEvent<P>> implements ApplicationListener<DomainEvent.Message> {

    final DomainEventPublisher<P, E> eventProcessor;

    @Inject
    public DomainEventPublishScheduler(final DomainEventPublisher<P, E> eventProcessor) {
        this.eventProcessor = eventProcessor;
    }

    @Override
    public void onApplicationEvent(final E.Message event) {
        eventProcessor.process(event.id());
    }

   // @Scheduled(fixedRate = 1000)
    public void processNextMessage() {
        eventProcessor.processNext();
    }
}
