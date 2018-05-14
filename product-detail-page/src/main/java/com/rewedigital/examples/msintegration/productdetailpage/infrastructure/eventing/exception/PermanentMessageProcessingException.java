package com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.exception;

import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.EventProcessingState;

public class PermanentMessageProcessingException extends MessageProcessingException {

    private static final long serialVersionUID = 1L;

    public PermanentMessageProcessingException(final String message, final Exception e) {
        super(EventProcessingState.PERMANENT_ERROR, message, e);
    }
}
