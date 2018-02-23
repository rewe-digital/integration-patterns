package com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing;

public enum EventProcessingState {

    SUCCESS(true), UNEXPECTED_ERROR(false), TEMPORARY_ERROR(false), PERMANENT_ERROR(true);

    private final boolean finalState;

    EventProcessingState(final boolean finalState) {
        this.finalState = finalState;
    }

    public boolean isFinalState() {
        return finalState;
    }
}
