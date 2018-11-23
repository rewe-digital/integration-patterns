package com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing;

import com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing.internal.EventPublishingEntityListenerAdapter;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

public class EventPublishingEntityListener {

    @PrePersist
    void onPersist(EventSource entity) { publishEvent(entity, "created"); }

    @PreUpdate
    public void onUpdate(EventSource entity) {
        publishEvent(entity, "updated");
    }

    @PreRemove
    public void onRemove(EventSource entity) {
        publishEvent(entity, "deleted");
    }

    private void publishEvent(EventSource entity, String action) {
        EventPublishingEntityListenerAdapter.lookup().publishEvent(entity, action);

    }
}
