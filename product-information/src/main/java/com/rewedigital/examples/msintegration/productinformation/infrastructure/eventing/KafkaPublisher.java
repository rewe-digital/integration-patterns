package com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class KafkaPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;

    // FIXME topic name
    @Inject
    public KafkaPublisher(final KafkaTemplate<String, String> kafkaTemplate, final ObjectMapper objectMapper,
        @Value("${productqueue.topic_name}") final String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topic = topic;
    }

    public ListenableFuture<SendResult<String, String>> publish(final DomainEvent event) {
        LOGGER.info("publishing event {} to topic {}", event.getId(), topic);
        return kafkaTemplate.send(topic, event.getKey(), toMessage(event));
    }

    private String toMessage(final DomainEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (final JsonProcessingException e) {
            LOGGER.error("Could not serialize event with id {}", event.getId(), e);
            // FIXME error handling?
            return "";
        }
    }
}