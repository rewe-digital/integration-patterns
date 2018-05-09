package com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.exception;

public class TemporaryKafkaProcessingError extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TemporaryKafkaProcessingError(final String message) {
        super(message);
    }
}
