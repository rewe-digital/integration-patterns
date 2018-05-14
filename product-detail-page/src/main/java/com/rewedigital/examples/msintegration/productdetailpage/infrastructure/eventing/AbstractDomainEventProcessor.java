package com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing;

import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.configuration.ConsumerTopicConfig;
import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.exception.MessageProcessingException;
import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.processed.ProcessedEventService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;

import static java.util.Objects.requireNonNull;


public abstract class AbstractDomainEventProcessor<P extends EventPayload, E extends DomainEvent<P>> implements DomainEventProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDomainEventProcessor.class);

    protected final EventParser eventParser;
    private final Class<E> eventType;
    private final ConsumerTopicConfig topicConfig;
    private final ProcessedEventService processedEventService;

    public AbstractDomainEventProcessor(final Class<E> eventType, final ConsumerTopicConfig topicConfig,
                                        final EventParser eventParser, final ProcessedEventService processedEventService) {
        this.eventType = requireNonNull(eventType, "eventType");
        this.topicConfig = requireNonNull(topicConfig, "topicConfig");
        this.eventParser = requireNonNull(eventParser, "messageParser");
        this.processedEventService = requireNonNull(processedEventService, "processedEventService");
    }

    @Transactional
    @Override
    public EventProcessingState processConsumerRecord(final ConsumerRecord<String, String> consumerRecord) {
        try {
            final E eventMessage = eventParser.parseMessage(consumerRecord.value(), eventType);
            final long version = eventMessage.getVersion();
            final String key = eventMessage.getKey();
            final String topic = consumerRecord.topic();
            if (skipMessage(topic, key, version)) {
                LOG.info("Skipping old {} message with key {} and version {}", topic, key,
                        version);
                return EventProcessingState.SUCCESS;
            }
            final EventProcessingState state = processEvent(eventMessage);
            if (state.isFinalState()) {
                processedEventService.updateLastProcessedVersion(topic, key, version);
            }
            return state;
        } catch (final MessageProcessingException e) {
            LOG.warn("Failed to create valid {} object from {}", eventType.getSimpleName(),
                    ConsumerRecordLoggingHelper.toLogSafeString(consumerRecord, topicConfig.isPayloadSensitive()), e);
            return e.getState();
        }
    }

    @Override
    public ConsumerTopicConfig getTopicConfig() {
        return topicConfig;
    }

    protected abstract EventProcessingState processEvent(E domainEvent);

    private boolean skipMessage(final String topic, final String key, final long version) {
        return processedEventService.getLastProcessedVersion(topic, key) > version;
    }

}
