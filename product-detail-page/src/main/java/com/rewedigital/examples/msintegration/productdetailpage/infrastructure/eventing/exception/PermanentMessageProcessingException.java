package com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.exception;

import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.MessageProcessingState;

public class PermanentMessageProcessingException extends MessageProcessingException {

    public PermanentMessageProcessingException(final String message, final Exception e) {
        super(MessageProcessingState.PERMANENT_ERROR, message, e);
    }
}
