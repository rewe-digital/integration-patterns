package com.rewedigital.examples.msintegration.productdetailpage.product.processed;

import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.ProcessedEventStore;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProductProccessedStore implements ProcessedEventStore {

    private final JpaProductProcessedRepository repository;

    public ProductProccessedStore(final JpaProductProcessedRepository repository) {
        this.repository=repository;
    }

    @Override
    public long getLastProcessedVersion(String topic, String key) {
        return Optional.ofNullable(repository.findByTopicAndKey(topic, key))
                .map(ProcessedProductEntity::getVersion)
                .orElse(0L);
    }

    @Override
    public void updateLastProcessedVersion(String topic, String key, long version) {
        repository.save(new ProcessedProductEntity(key, topic, version));
    }
}
