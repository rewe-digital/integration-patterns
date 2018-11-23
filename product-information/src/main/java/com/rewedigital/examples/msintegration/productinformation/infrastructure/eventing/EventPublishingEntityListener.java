package com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing;

import com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing.internal.EventPublishingEntityListenerAdapter;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.PostPersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

public class EventPublishingEntityListener {

    @PostPersist
    void onPersist(EventSource entity) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                publishEvent(entity, "created");
            }
        });
    }

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
