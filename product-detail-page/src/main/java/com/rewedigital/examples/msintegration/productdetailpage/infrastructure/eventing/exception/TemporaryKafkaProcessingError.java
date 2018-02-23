package com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.exception;

public class TemporaryKafkaProcessingError extends RuntimeException {

    public TemporaryKafkaProcessingError(final String message) {
        super(message);
    }
}
