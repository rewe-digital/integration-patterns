package com.rewedigital.examples.msintegration.productinformation.product;

import org.springframework.kafka.core.KafkaTemplate;

public class ProductEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public ProductEventPublisher(final KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    public void publish(final ProductEvent productEvent) {
        
    }
}
