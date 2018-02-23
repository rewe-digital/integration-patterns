package com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.processed;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProcessedEventRepository extends JpaRepository<ProcessedEventEntity, String> {

    ProcessedEventEntity findByTopicAndKey(String topic, String key);

}
