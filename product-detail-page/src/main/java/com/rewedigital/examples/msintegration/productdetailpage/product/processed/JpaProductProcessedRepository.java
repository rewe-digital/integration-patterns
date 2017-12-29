package com.rewedigital.examples.msintegration.productdetailpage.product.processed;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProductProcessedRepository extends JpaRepository<ProcessedProductEntity, String> {

    ProcessedProductEntity findByTopicAndKey(String topic, String key);
}
