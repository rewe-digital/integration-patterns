package com.rewedigital.examples.msintegration.productinformation.product;

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
public class ProductEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductEventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;

    @Inject
    public ProductEventPublisher(final KafkaTemplate<String, String> kafkaTemplate, final ObjectMapper objectMapper,
        @Value("${productqueue.topic_name}") final String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topic = topic;
    }

    public ListenableFuture<SendResult<String, String>> publish(final ProductEvent productEvent) {
        LOGGER.debug("publishing event [%s] to topic [%s]", productEvent.getId(), topic);
        return kafkaTemplate.send(topic, productEvent.getKey(), toMessage(productEvent));
    }

    private String toMessage(final ProductEvent productEvent) {
        try {
            return objectMapper.writeValueAsString(productEvent);
        } catch (final JsonProcessingException e) {
            LOGGER.error("Could not serialize event [%s]", productEvent, e);
            // FIXME error handling?
            return "";
        }
    }
}
