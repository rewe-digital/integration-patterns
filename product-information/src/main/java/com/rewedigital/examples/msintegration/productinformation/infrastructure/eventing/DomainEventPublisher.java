package com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;

@Component
public class DomainEventPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(DomainEventPublisher.class);

    private final LastPublishedVersionRepository lastPublishedVersionRepository;
    private final DomainEventRepository eventRepository;
    private final KafkaPublisher eventPublisher;

    @Inject
    public DomainEventPublisher(final LastPublishedVersionRepository lastPublishedVersionRepository,
        final DomainEventRepository eventRepository,
        final KafkaPublisher eventPublisher) {
        this.lastPublishedVersionRepository = Objects.requireNonNull(lastPublishedVersionRepository);
        this.eventRepository = Objects.requireNonNull(eventRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Transactional
    public void process(final String eventId) {
        sendEvent(eventRepository.findOne(eventId));
    }

    @Transactional
    public void processNext() {
        sendEvent(eventRepository.findFirstByTimeInSmallestVersion());
    }

    private void sendEvent(final DomainEvent event) {
        if (event == null) {
            return;
        }

        final String lastPublishedVersionId = getLastPublishedVersionId(event);
        obtainLastPublishedVersion(lastPublishedVersionId).ifPresent((LastPublishedVersion v) -> {
            try {
                if (v.getVersion() < event.getVersion()) {
                    eventPublisher.publish(event).get(); // need to block here so that following statements are
                                                         // executed inside transaction
                    v.setVersion(event.getVersion());
                    lastPublishedVersionRepository.save(v);
                }
                eventRepository.delete(event);
            } catch (final Exception ex) {
                LOG.error("error publishing event with id [{}] due to {}", event.getId(), ex.getMessage(), ex);
            }
        });
    }

    private String getLastPublishedVersionId(final DomainEvent event) {
        final String entityId = event.getKey();
        final String aggregateName = event.getAggregateName();
        return aggregateName + "-" + entityId;
    }

    private Optional<LastPublishedVersion> obtainLastPublishedVersion(final String lastPublishedVersionId) {
        LastPublishedVersion lastPublishedVersion = lastPublishedVersionRepository.findOne(lastPublishedVersionId);
        if (lastPublishedVersion == null) {
            try {
                lastPublishedVersion =
                    lastPublishedVersionRepository.saveAndFlush(LastPublishedVersion.of(lastPublishedVersionId));
            } catch (final Exception ex) {
                return Optional.empty();
            }
        }
        return Optional.of(lastPublishedVersion);
    }

}
