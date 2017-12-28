package com.rewedigital.examples.msintegration.productdetailconsumer.infrastructure.eventing;


import com.rewedigital.examples.msintegration.productdetailconsumer.infrastructure.eventing.exception.TemporaryKafkaProcessingError;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.Acknowledgment;

import java.util.Set;

import static com.rewedigital.examples.msintegration.productdetailconsumer.infrastructure.eventing.MessageProcessingState.*;
import static java.util.Objects.requireNonNull;


public abstract class AbstractKafkaConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractKafkaConsumer.class);

    private final boolean payloadSensitive;
    private final KafkaMessageProcessor messageProcessor;
    private final ConsumerRecordStore consumerRecordStore;
    private final Set<Class<? extends RuntimeException>> temporaryExceptions;


    protected AbstractKafkaConsumer(final KafkaMessageProcessor messageProcessor,
                                    final ConsumerRecordStore consumerRecordStore,
                                    final Set<Class<? extends RuntimeException>> temporaryExceptions) {
        this.messageProcessor = requireNonNull(messageProcessor, "messageProcessor");
        this.consumerRecordStore = requireNonNull(consumerRecordStore, "consumerRecordStore");
        this.temporaryExceptions = requireNonNull(temporaryExceptions, "temporaryExceptions");
        this.payloadSensitive = messageProcessor.getTopicConfig().isPayloadSensitive();
    }

    protected void handleConsumerRecord(final ConsumerRecord<String, String> consumerRecord, final Acknowledgment ack) {
        LOG.info("Received {}", ConsumerRecordLoggingHelper.toLogSafeString(consumerRecord, payloadSensitive));
        final MessageProcessingState state = processAndMapExceptionsToState(consumerRecord);
        if (UNEXPECTED_ERROR == state) {
            consumerRecordStore.save(consumerRecord);
        } else if (TEMPORARY_ERROR == state) {
            throw new TemporaryKafkaProcessingError("Message processing failed temporarily");
        }
        ack.acknowledge();
    }

    private MessageProcessingState processAndMapExceptionsToState(final ConsumerRecord<String, String> consumerRecord) {
        try {
            return messageProcessor.processConsumerRecord(consumerRecord);
        } catch (final RuntimeException e) {
            if (temporaryExceptions.stream().anyMatch(temporaryException -> temporaryException.isInstance(e))) {
                LOG.error("Message processing failed temporarily for {}",
                        ConsumerRecordLoggingHelper.toLogSafeString(consumerRecord), e);
                return TEMPORARY_ERROR;
            }
            LOG.error("Message processing failed unexpectedly for {}",
                    ConsumerRecordLoggingHelper.toLogSafeString(consumerRecord, payloadSensitive), e);
            return UNEXPECTED_ERROR;
        }
    }

}
