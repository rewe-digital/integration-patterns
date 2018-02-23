package com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing;

import java.util.Objects;
import java.util.function.Function;

import javax.transaction.Transactional;

import org.springframework.context.ApplicationEventPublisher;

public abstract class AbstractEventPublishingRepository<P> {

    private final DomainEventRepository eventRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    protected AbstractEventPublishingRepository(final DomainEventRepository eventRepository,
        final ApplicationEventPublisher eventPublisher) {
        this.eventRepository = Objects.requireNonNull(eventRepository);
        this.applicationEventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Transactional
    public P save(final P entity, final Function<P, DomainEvent> eventFactory) {
        final P persistedEntity = persist(entity);
        final DomainEvent event = eventFactory.apply(persistedEntity);
        eventRepository.saveAndFlush(event);
        applicationEventPublisher.publishEvent(event.message(this));
        return persistedEntity;
    }

    protected abstract P persist(final P entity);
}
