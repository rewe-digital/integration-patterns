package com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import javax.inject.Inject;

@Component
public class KafkaPublisher<P extends EventPayload, E extends DomainEvent<P>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;

    // FIXME topic name
    @Inject
    public KafkaPublisher(final KafkaTemplate<String, String> kafkaTemplate, final ObjectMapper objectMapper,
        @Value("${eventing.topic.product}") final String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topic = topic;
    }

    public ListenableFuture<SendResult<String, String>> publish(final E event) {
        LOGGER.info("publishing event {} to topic {}", event.getId(), topic);
        return kafkaTemplate.send(topic, event.getKey(), toEventMessage(event));
    }

    private String toEventMessage(final E event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (final JsonProcessingException e) {
            LOGGER.error("Could not serialize event with id {}", event.getId(), e);
            // FIXME error handling?
            return "";
        }
    }
}
