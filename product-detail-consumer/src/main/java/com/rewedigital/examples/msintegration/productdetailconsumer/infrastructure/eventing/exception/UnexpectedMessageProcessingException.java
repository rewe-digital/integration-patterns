package com.rewedigital.examples.msintegration.productdetailconsumer.infrastructure.eventing.exception;

import com.rewedigital.examples.msintegration.productdetailconsumer.infrastructure.eventing.MessageProcessingState;

public class UnexpectedMessageProcessingException extends MessageProcessingException {

    public UnexpectedMessageProcessingException(final String message, final Exception e) {
        super(MessageProcessingState.UNEXPECTED_ERROR, message, e);
    }

    public UnexpectedMessageProcessingException(final String message) {
        super(MessageProcessingState.UNEXPECTED_ERROR, message);
    }

}
