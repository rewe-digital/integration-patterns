package com.rewedigital.examples.msintegration.productdetailconsumer.infrastructure.eventing.exception;

import com.rewedigital.examples.msintegration.productdetailconsumer.infrastructure.eventing.MessageProcessingState;

public class MessageProcessingException extends Exception {

    private final MessageProcessingState state;

    public MessageProcessingException(final MessageProcessingState state, final String message,
                                      final Exception e) {
        super(message, e);
        this.state = state;
    }

    public MessageProcessingException(final MessageProcessingState state, final String message) {
        super(message);
        this.state = state;
    }

    public MessageProcessingState getState() {
        return state;
    }
}
