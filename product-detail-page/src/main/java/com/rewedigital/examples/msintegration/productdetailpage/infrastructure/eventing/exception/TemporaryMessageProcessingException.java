package com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.exception;

import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.EventProcessingState;

public class TemporaryMessageProcessingException extends MessageProcessingException {

    public TemporaryMessageProcessingException(final String message) {
        super(EventProcessingState.TEMPORARY_ERROR, message);
    }

    public TemporaryMessageProcessingException(final String message, final Exception e) {
        super(EventProcessingState.TEMPORARY_ERROR, message, e);
    }
}
