package com.rewedigital.examples.msintegration.productdetailconsumer.infrastructure.eventing.exception;

public class TemporaryKafkaProcessingError extends RuntimeException {

    public TemporaryKafkaProcessingError(final String message) {
        super(message);
    }
}
