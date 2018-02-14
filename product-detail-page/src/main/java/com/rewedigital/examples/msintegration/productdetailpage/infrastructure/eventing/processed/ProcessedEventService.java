package com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.processed;

import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;

@Component
public class ProcessedEventService {

    private final JpaProcessedEventRepository repository;

    @Inject
    public ProcessedEventService(final JpaProcessedEventRepository repository) {
        this.repository=repository;
    }

    public long getLastProcessedVersion(String topic, String key) {
        return Optional.ofNullable(repository.findByTopicAndKey(topic, key))
                .map(ProcessedEventEntity::getVersion)
                .orElse(0L);
    }

    public void updateLastProcessedVersion(String topic, String key, long version) {
        repository.save(new ProcessedEventEntity(key, topic, version));
    }
}
