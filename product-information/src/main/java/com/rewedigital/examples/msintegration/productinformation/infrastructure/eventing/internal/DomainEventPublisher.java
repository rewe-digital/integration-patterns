package com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing.internal;


import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class DomainEventPublisher implements ApplicationListener<DomainEvent.Message> {

    private static final Logger LOG = LoggerFactory.getLogger(DomainEventPublisher.class);

    private final KafkaGateway eventPublisher;
    private final EntityManager entityManager;

    @Inject
    public DomainEventPublisher(final KafkaGateway eventPublisher,
        final EntityManager entityManager) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.entityManager = Objects.requireNonNull(entityManager);
    }

    @Override
    @Transactional
    public void onApplicationEvent(final DomainEvent.Message event) {
        LOG.info("Recevied message to publish event for id {}", event.id());
        DomainEvent domainEvent = findEvent(event, 0);
        if (domainEvent != null) {
            sendEvent(domainEvent);
        } else {
            LOG.warn("Could not publish event with id {} due to: NOT_FOUND", event.id());
        }
    }

    private DomainEvent findEvent(final DomainEvent.Message event, int retryCount) {
        DomainEvent domainEvent = entityManager.find(DomainEvent.class, event.id(), LockModeType.PESSIMISTIC_WRITE);
        // FIXME fix this!
        if (domainEvent == null && retryCount < 3) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            domainEvent = findEvent(event, retryCount + 1);
        }
        return domainEvent;
    }

    // @Scheduled(fixedRate = 1000)
    @Transactional
    public void processNext() {
        findUnprocessedEvents().forEach(this::sendEvent);
    }

    public List<DomainEvent> findUnprocessedEvents() {
        @SuppressWarnings("unchecked")
        List<DomainEvent> resultList =
            (List<DomainEvent>) entityManager.createNativeQuery(
                "SELECT * FROM DOMAIN_EVENT WHERE key = (SELECT key FROM DOMAIN_EVENT ORDER BY time ASC LIMIT 1) ORDER BY version ASC LIMIT 1",
                DomainEvent.class).getResultList();
        return resultList;
    }

    private void sendEvent(final DomainEvent event) {
        if (event == null) {
            return;
        }

        obtainLastPublishedVersion(buildLastPublishedVersionId(event))
            .ifPresent(v -> publishIfNotOutdated(event, v));
    }

    private void publishIfNotOutdated(final DomainEvent event, LastPublishedVersion v) {
        try {
            if (v.getVersion() < event.getVersion()) {
                publish(event);
                v.setVersion(event.getVersion());
                entityManager.merge(v);
            }
            entityManager.remove(event);
        } catch (final Exception ex) {
            LOG.error("Error publishing event with id [{}] due to {}", event.getId(), ex.getMessage(), ex);
        }
    }

    private void publish(final DomainEvent event) throws InterruptedException, ExecutionException, TimeoutException {
        // need to block here so that following statements are executed inside transaction
        SendResult<String, String> sendResult = eventPublisher.publish(event).get(1, TimeUnit.SECONDS);
        LOG.debug("Published event to topic:partition {}:{} at {}", sendResult.getProducerRecord().topic(),
            sendResult.getProducerRecord().partition(), sendResult.getProducerRecord().timestamp());
    }

    private String buildLastPublishedVersionId(final DomainEvent event) {
        final String entityId = event.getKey();
        final String aggregateName = event.getAggregateName();
        return aggregateName + "-" + entityId;
    }

    private Optional<LastPublishedVersion> obtainLastPublishedVersion(final String lastPublishedVersionId) {
        return Optional.ofNullable(
            Optional
                .ofNullable(entityManager.find(LastPublishedVersion.class, lastPublishedVersionId,
                    LockModeType.PESSIMISTIC_WRITE))
                .orElseGet(() -> createLastPublishedVersion(lastPublishedVersionId)));
    }

    private LastPublishedVersion createLastPublishedVersion(final String lastPublishedVersionId) {
        try {
            final LastPublishedVersion v = LastPublishedVersion.of(lastPublishedVersionId);
            entityManager.persist(v);
            return v;
        } catch (final Exception ex) {
            LOG.error("Error while storing last published version with id {}", lastPublishedVersionId, ex);
            return null;
        }
    }
}
