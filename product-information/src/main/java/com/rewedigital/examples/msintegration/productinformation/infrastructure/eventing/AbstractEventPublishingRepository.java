package com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing;

import java.util.Objects;
import java.util.function.Function;

import javax.transaction.Transactional;

import org.springframework.context.ApplicationEventPublisher;

public abstract class AbstractEventPublishingRepository<T, P extends EventPayload, E extends DomainEvent<P>> {

    private final DomainEventRepository<P, E> eventRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    protected AbstractEventPublishingRepository(final DomainEventRepository<P, E> eventRepository,
        final ApplicationEventPublisher eventPublisher) {
        this.eventRepository = Objects.requireNonNull(eventRepository);
        this.applicationEventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Transactional
    public T save(final T entity, final Function<T, E> eventFactory) {
        final T persistedEntity = persist(entity);
        final E event = eventFactory.apply(persistedEntity);
        eventRepository.saveAndFlush(event);
        applicationEventPublisher.publishEvent(event.message(this));
        return persistedEntity;
    }

    protected abstract T persist(final T entity);
}
