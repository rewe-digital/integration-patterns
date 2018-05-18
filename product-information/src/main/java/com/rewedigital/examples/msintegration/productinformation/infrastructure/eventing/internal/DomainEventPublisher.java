package com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing.internal;


import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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

    private final DomainEventRepository eventRepository;
    private final KafkaGateway<DomainEvent> eventPublisher;
    private final EntityManager entityManager;

    @Inject
    public DomainEventPublisher(final DomainEventRepository eventRepository,
        final KafkaGateway<DomainEvent> eventPublisher,
        final EntityManager entityManager) {
        this.eventRepository = Objects.requireNonNull(eventRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.entityManager = Objects.requireNonNull(entityManager);
    }

    @Override
    @Transactional
    public void onApplicationEvent(final DomainEvent.Message event) {
        eventRepository.findById(event.id()).ifPresent(e -> sendEvent(e));
    }

    // @Scheduled(fixedRate = 1000)
    @Transactional
    public void processNext() {
        sendEvent(eventRepository.findFirstByTimeInSmallestVersion());
    }

    private void sendEvent(final DomainEvent event) {
        if (event == null) {
            return;
        }

        final String lastPublishedVersionId = buildLastPublishedVersionId(event);
        obtainLastPublishedVersion(lastPublishedVersionId).ifPresent(v -> {

            try {
                if (v.getVersion() < event.getVersion()) {
                    // need to block here so that following statements are executed inside transaction
                    SendResult<String, String> sendResult = eventPublisher.publish(event).get(1, TimeUnit.SECONDS);
                    LOG.debug("Published event to topic:partition {}:{} at {}", sendResult.getProducerRecord().topic(),
                        sendResult.getProducerRecord().partition(), sendResult.getProducerRecord().timestamp());
                    v.setVersion(event.getVersion());
                    entityManager.merge(v);
                }
                eventRepository.delete(event);
            } catch (final Exception ex) {
                LOG.error("Error publishing event with id [{}] due to {}", event.getId(), ex.getMessage(), ex);
            }
        });
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
                .orElseGet(() -> {
                    try {
                        final LastPublishedVersion v = LastPublishedVersion.of(lastPublishedVersionId);
                        entityManager.persist(v);
                        return v;
                    } catch (final Exception ex) {
                        LOG.error("Error while storing last published version with id {}", lastPublishedVersionId, ex);
                        return null;
                    }
                }));
    }

}
