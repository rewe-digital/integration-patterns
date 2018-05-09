package com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing;

import javax.inject.Inject;

import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DomainEventPublishScheduler implements ApplicationListener<DomainEvent.Message> {

    final DomainEventPublisher eventProcessor;

    @Inject
    public DomainEventPublishScheduler(final DomainEventPublisher eventProcessor) {
        this.eventProcessor = eventProcessor;
    }

    @Override
    public void onApplicationEvent(final DomainEvent.Message event) {
        eventProcessor.process(event.id());
    }

   // @Scheduled(fixedRate = 1000)
    public void processNextMessage() {
        eventProcessor.processNext();
    }
}
