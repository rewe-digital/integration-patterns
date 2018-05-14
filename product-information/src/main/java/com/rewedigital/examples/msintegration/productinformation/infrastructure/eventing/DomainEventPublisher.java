package com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing;


import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class DomainEventPublisher<P extends EventPayload, E extends DomainEvent<P>> {

    private static final Logger LOG = LoggerFactory.getLogger(DomainEventPublisher.class);

    private final LastPublishedVersionRepository lastPublishedVersionRepository;
    private final DomainEventRepository<P, E> eventRepository;
    private final KafkaPublisher<P, E> eventPublisher;

    @Inject
    public DomainEventPublisher(final LastPublishedVersionRepository lastPublishedVersionRepository,
        final DomainEventRepository<P, E> eventRepository,
        final KafkaPublisher<P, E> eventPublisher) {
        this.lastPublishedVersionRepository = Objects.requireNonNull(lastPublishedVersionRepository);
        this.eventRepository = Objects.requireNonNull(eventRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Transactional
    public void process(final String eventId) {
        eventRepository.findById(eventId).ifPresent(e -> sendEvent(e));
    }

    @Transactional
    public void processNext() {
        sendEvent(eventRepository.findFirstByTimeInSmallestVersion());
    }

    private void sendEvent(final E event) {
        if (event == null) {
            return;
        }

        final String lastPublishedVersionId = buildLastPublishedVersionId(event);
        obtainLastPublishedVersion(lastPublishedVersionId).ifPresent(v -> {

            try {
                if (v.getVersion() < event.getVersion()) {
                    // need to block here so that following statements are executed inside transaction
                    SendResult<String, String> sendResult = eventPublisher.publish(event).get(1, TimeUnit.SECONDS);
                    LOG.info("published event to {}:{} at {}", sendResult.getProducerRecord().topic(),
                        sendResult.getProducerRecord().partition(), sendResult.getProducerRecord().timestamp());
                    v.setVersion(event.getVersion());
                    lastPublishedVersionRepository.save(v);
                }
                eventRepository.delete(event);
            } catch (final Exception ex) {
                LOG.error("error publishing event with id [{}] due to {}", event.getId(), ex.getMessage(), ex);
            }
        });
    }

    private String buildLastPublishedVersionId(final E event) {
        final String entityId = event.getKey();
        final String aggregateName = event.getAggregateName();
        return aggregateName + "-" + entityId;
    }

    private Optional<LastPublishedVersion> obtainLastPublishedVersion(final String lastPublishedVersionId) {
        LastPublishedVersion lastPublishedVersion = lastPublishedVersionRepository
            .findById(lastPublishedVersionId).orElseGet(() -> {
                try {
                    LastPublishedVersion version =
                        lastPublishedVersionRepository.saveAndFlush(LastPublishedVersion.of(lastPublishedVersionId));
                    return version;
                } catch (final Exception ex) {
                    LOG.error("error while storing last published version with id {}", lastPublishedVersionId, ex);
                    return null;
                }
            });
        return Optional.ofNullable(lastPublishedVersion);
    }

}
