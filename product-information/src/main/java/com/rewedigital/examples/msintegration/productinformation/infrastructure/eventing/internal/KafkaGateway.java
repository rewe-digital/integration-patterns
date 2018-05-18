package com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing.internal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
public class KafkaGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaGateway.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;

    // FIXME topic name
    @Inject
    public KafkaGateway(final KafkaTemplate<String, String> kafkaTemplate, final ObjectMapper objectMapper,
        @Value("${eventing.topic.product}") final String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topic = topic;
    }

    public ListenableFuture<SendResult<String, String>> publish(final DomainEvent event) {
        LOGGER.info("publishing event {} to topic {}", event.getId(), topic);
        return kafkaTemplate.send(topic, event.getKey(), toEventMessage(event));
    }

    private String toEventMessage(final DomainEvent event) {
        try {
            final Map<String, Object> message = new HashMap<String, Object>();
            message.put("id", event.getId());
            message.put("key", event.getKey());
            message.put("time", event.getTime());
            message.put("type", event.getType());
            message.put("version", event.getVersion());
            message.put("payload", objectMapper.readValue(event.getPayload(), event.getEntityType()));
            return objectMapper.writeValueAsString(message);
        } catch (final JsonProcessingException e) {
            LOGGER.error("Could not serialize event with id {}", event.getId(), e);
            // FIXME error handling?
            return "";
        } catch (IOException e) {
            LOGGER.error("Could not read payload for event with id {}", event.getId(), e);
            return "";
        }
    }
}
