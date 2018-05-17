package com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EventPublishingEntityListener implements ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(EventPublishingEntityListener.class);
    private static volatile ApplicationContext applicationContext;

    private final EntityManager eventRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    public static class Listener {
        @PrePersist
        public void onCreate(EventSource entity) {
            publishEvent(entity, "created");
        }

        @PreUpdate
        public void onUpdate(EventSource entity) {
            publishEvent(entity, "updated");
        }

        @PreRemove
        public void onRemove(EventSource entity) {
            publishEvent(entity, "deleted");
        }


        private void publishEvent(EventSource entity, String action) {
            EventPublishingEntityListener eventGenerator = EventPublishingEntityListener.applicationContext.getBean(EventPublishingEntityListener.class);
            final DomainEvent event =
                toEvent(entity, entity.getAggregateName() + "-" + action, eventGenerator.objectMapper());
            eventGenerator.eventRepository().persist(event);
            eventGenerator.eventRepository().flush();
            eventGenerator.eventPublisher().publishEvent(event.message(this));
            LOG.debug("Published {} event for {} with id {}, version {}", action, entity.getClass(), entity.getId(),
                entity.getVersion());
        }

        private DomainEvent toEvent(final EventSource entity, final String eventType,
            final ObjectMapper objectMapper) {
            try {
                final DomainEvent result = new DomainEvent();
                result.setId(UUID.randomUUID().toString());
                result.setKey(entity.getId());
                result.setTime(ZonedDateTime.now(ZoneOffset.UTC));
                // incrementing version here due to running in @PreCreate, because @PostCreate leads to concurrent
                // modification errors
                result.setVersion(entity.getVersion() == null ? 0 : entity.getVersion() + 1);
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
    }

    @Autowired
    public EventPublishingEntityListener(EntityManager eventRepository, ApplicationEventPublisher eventPublisher,
        ObjectMapper objectMapper) {
        this.eventRepository = eventRepository;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    private ApplicationEventPublisher eventPublisher() {
        return eventPublisher;
    }

    private EntityManager eventRepository() {
        return eventRepository;
    }

    private ObjectMapper objectMapper() {
        return objectMapper;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

}
