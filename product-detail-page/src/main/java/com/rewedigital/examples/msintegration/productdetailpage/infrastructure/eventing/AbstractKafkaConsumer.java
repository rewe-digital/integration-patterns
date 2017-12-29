package com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing;


import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.exception.TemporaryKafkaProcessingError;
import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.unprocessable.UnprocessableEventService;
import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.unprocessable.UnprocessedEventEntity;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.Acknowledgment;

import java.util.Set;

import static java.util.Objects.requireNonNull;


public abstract class AbstractKafkaConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractKafkaConsumer.class);

    private final boolean payloadSensitive;
    private final DomainEventProcessor domainEventProcessor;
    private final UnprocessableEventService unprocessableEventService;
    private final Set<Class<? extends RuntimeException>> temporaryExceptions;


    protected AbstractKafkaConsumer(final DomainEventProcessor domainEventProcessor,
                                    final UnprocessableEventService unprocessableEventService,
                                    final Set<Class<? extends RuntimeException>> temporaryExceptions) {
        this.domainEventProcessor = requireNonNull(domainEventProcessor, "domainEventProcessor");
        this.unprocessableEventService = requireNonNull(unprocessableEventService, "unprocessableEventService");
        this.temporaryExceptions = requireNonNull(temporaryExceptions, "temporaryExceptions");
        this.payloadSensitive = domainEventProcessor.getTopicConfig().isPayloadSensitive();
    }

    protected void handleConsumerRecord(final ConsumerRecord<String, String> consumerRecord, final Acknowledgment ack) {
        LOG.info("Received {}", ConsumerRecordLoggingHelper.toLogSafeString(consumerRecord, payloadSensitive));
        final EventProcessingState state = processAndMapExceptionsToState(consumerRecord);
        if (EventProcessingState.UNEXPECTED_ERROR == state) {
            unprocessableEventService.save(new UnprocessedEventEntity(consumerRecord));
        } else if (EventProcessingState.TEMPORARY_ERROR == state) {
            throw new TemporaryKafkaProcessingError("Message processing failed temporarily");
        }
        ack.acknowledge();
    }

    private EventProcessingState processAndMapExceptionsToState(final ConsumerRecord<String, String> consumerRecord) {
        try {
            return domainEventProcessor.processConsumerRecord(consumerRecord);
        } catch (final RuntimeException e) {
            if (temporaryExceptions.stream().anyMatch(temporaryException -> temporaryException.isInstance(e))) {
                LOG.error("Message processing failed temporarily for {}",
                        ConsumerRecordLoggingHelper.toLogSafeString(consumerRecord), e);
                return EventProcessingState.TEMPORARY_ERROR;
            }
            LOG.error("Message processing failed unexpectedly for {}",
                    ConsumerRecordLoggingHelper.toLogSafeString(consumerRecord, payloadSensitive), e);
            return EventProcessingState.UNEXPECTED_ERROR;
        }
    }

}
