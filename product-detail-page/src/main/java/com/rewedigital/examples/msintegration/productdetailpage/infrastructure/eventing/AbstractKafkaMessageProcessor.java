package com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing;

import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.exception.MessageProcessingException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;
import static java.util.Objects.requireNonNull;


public abstract class AbstractKafkaMessageProcessor<M extends KafkaMessage> implements KafkaMessageProcessor{

    private static final Logger LOG = LoggerFactory.getLogger(AbstractKafkaMessageProcessor.class);

    protected final MessageParser messageParser;
    private final Class<M> messageType;
    private final ConsumerTopicConfig topicConfig;
    private final ProcessedMessageStore processedMessageStore;

    public AbstractKafkaMessageProcessor(final Class<M> messageType, final ConsumerTopicConfig topicConfig,
                                         final MessageParser messageParser, final ProcessedMessageStore processedMessageStore) {
        this.messageType = requireNonNull(messageType, "messageType");
        this.topicConfig = requireNonNull(topicConfig, "topicConfig");
        this.messageParser = requireNonNull(messageParser, "messageParser");
        this.processedMessageStore = requireNonNull(processedMessageStore, "processedMessageStore");
    }

    @Transactional
    @Override
    public MessageProcessingState processConsumerRecord(final ConsumerRecord<String, String> consumerRecord) {
        try {
            final M kafkaMessage = messageParser.parseMessage(consumerRecord.value(), messageType);
            final long messageVersion = kafkaMessage.getVersion();
            final String messageKey = kafkaMessage.getKey();
            final String messageTopic = consumerRecord.topic();
            if (skipMessage(messageTopic, messageKey, messageVersion)) {
                LOG.info("Skipping old {} message with key {} and version {}", messageTopic, messageKey,
                        messageVersion);
                return MessageProcessingState.SUCCESS;
            }
            final MessageProcessingState state = processMessage(kafkaMessage);
            if (state.isFinalState()) {
                processedMessageStore.updateLastProcessedVersion(messageTopic, messageKey, messageVersion);
            }
            return state;
        } catch (final MessageProcessingException e) {
            LOG.warn("Failed to create valid {} object from {}", messageType.getSimpleName(),
                    ConsumerRecordLoggingHelper.toLogSafeString(consumerRecord, topicConfig.isPayloadSensitive()), e);
            return e.getState();
        }
    }

    @Override
    public ConsumerTopicConfig getTopicConfig() {
        return topicConfig;
    }

    protected abstract MessageProcessingState processMessage(M kafkaMessage);

    private boolean skipMessage(final String topic, final String key, final long version) {
        return processedMessageStore.getLastProcessedVersion(topic, key) > version;
    }

}
