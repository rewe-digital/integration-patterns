package com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing;

import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.configuration.ConsumerTopicConfig;
import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.exception.MessageProcessingException;
import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.processed.ProcessedEventService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;

import static java.util.Objects.requireNonNull;


public abstract class AbstractDomainEventProcessor<M extends DomainEvent> implements DomainEventProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDomainEventProcessor.class);

    protected final EventParser eventParser;
    private final Class<M> eventType;
    private final ConsumerTopicConfig topicConfig;
    private final ProcessedEventService processedEventService;

    public AbstractDomainEventProcessor(final Class<M> eventType, final ConsumerTopicConfig topicConfig,
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
            final M kafkaMessage = eventParser.parseMessage(consumerRecord.value(), eventType);
            final long messageVersion = kafkaMessage.getVersion();
            final String messageKey = kafkaMessage.getKey();
            final String messageTopic = consumerRecord.topic();
            if (skipMessage(messageTopic, messageKey, messageVersion)) {
                LOG.info("Skipping old {} message with key {} and version {}", messageTopic, messageKey,
                        messageVersion);
                return EventProcessingState.SUCCESS;
            }
            final EventProcessingState state = processMessage(kafkaMessage);
            if (state.isFinalState()) {
                processedEventService.updateLastProcessedVersion(messageTopic, messageKey, messageVersion);
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

    protected abstract EventProcessingState processMessage(M kafkaMessage);

    private boolean skipMessage(final String topic, final String key, final long version) {
        return processedEventService.getLastProcessedVersion(topic, key) > version;
    }

}
