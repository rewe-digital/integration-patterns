package com.rewedigital.examples.msintegration.productinformation.product;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Component
public class ProductEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductEventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;

    @Inject
    public ProductEventPublisher(final KafkaTemplate<String, String> kafkaTemplate,
        @Value("${productqueue.topic_name}") final String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public ListenableFuture<SendResult<String, String>> publish(final ProductEvent productEvent) {
        LOGGER.debug("publishing event [%s] to topic [%s]");
        return kafkaTemplate.send(topic, productEvent.getKey(), productEvent.toMessage());
    }
}
