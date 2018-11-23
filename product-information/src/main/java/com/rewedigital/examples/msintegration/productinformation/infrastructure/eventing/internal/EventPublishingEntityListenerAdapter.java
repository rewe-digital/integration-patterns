package com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing.EventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class EventPublishingEntityListenerAdapter implements ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(EventPublishingEntityListenerAdapter.class);
    private static volatile ApplicationContext applicationContext;
    private final EntityManager eventRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Autowired
    public EventPublishingEntityListenerAdapter(EntityManager eventRepository, ApplicationEventPublisher eventPublisher,
        ObjectMapper objectMapper) {
        this.eventRepository = eventRepository;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    public static EventPublishingEntityListenerAdapter lookup() {
        return applicationContext.getBean(EventPublishingEntityListenerAdapter.class);
    }

    @Transactional
    public void publishEvent(EventSource entity, String action) {
        final DomainEvent event =
            toEvent(entity, entity.getAggregateName() + "-" + action, objectMapper);
        eventRepository.persist(event);

        fireEvent(entity, event, action);
    }

    private void fireEvent(EventSource entity, DomainEvent event, String action) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                eventPublisher.publishEvent(event.message(EventPublishingEntityListenerAdapter.this));
                LOG.debug("Published {} event for {} with id {}, version {}", action, entity.getClass(), entity.getId(),
                        entity.getVersion());
            }
        });
    }

    private DomainEvent toEvent(final EventSource entity, final String eventType,
        final ObjectMapper objectMapper) {
        try {
            final DomainEvent result = new DomainEvent();
            result.setId(UUID.randomUUID().toString());
            result.setKey(entity.getId());
            result.setTime(ZonedDateTime.now(ZoneOffset.UTC));
            result.setVersion(entity.getVersion() == null ? 0 : entity.getVersion());
            result.setEntityType(entity.getClass());
            result.setType(eventType);
            result.setAggregateName(entity.getAggregateName());
            result.setPayload(objectMapper.writeValueAsBytes(entity));
            return result;
        } catch (final Exception ex) {
            LOG.error("Could not create domain event", ex);
            throw new RuntimeException("could not create DomainEvent from Entity ", ex);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

}