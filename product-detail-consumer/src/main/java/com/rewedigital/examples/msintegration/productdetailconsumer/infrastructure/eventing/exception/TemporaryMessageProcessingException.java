package com.rewedigital.examples.msintegration.productdetailconsumer.infrastructure.eventing.exception;

import com.rewedigital.examples.msintegration.productdetailconsumer.infrastructure.eventing.MessageProcessingState;

public class TemporaryMessageProcessingException extends MessageProcessingException {

    public TemporaryMessageProcessingException(final String message) {
        super(MessageProcessingState.TEMPORARY_ERROR, message);
    }

    public TemporaryMessageProcessingException(final String message, final Exception e) {
        super(MessageProcessingState.TEMPORARY_ERROR, message, e);
    }
}
