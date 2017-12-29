package com.rewedigital.examples.msintegration.productdetailpage.product;

import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.UnprocessableEventStore;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductRecordStore implements UnprocessableEventStore {

    private static final Logger LOG = LoggerFactory.getLogger(ProductRecordStore.class);

    @Override
    public void save(ConsumerRecord<String, String> consumerRecord) {
        LOG.info("SAVE entity "+consumerRecord);
    }

    @Override
    public List<ConsumerRecord<String, String>> getAll() {
        return null;
    }

    @Override
    public List<ConsumerRecord<String, String>> getAllForTopic(String topic) {
        return null;
    }

    @Override
    public int delete(String topic, int partition, long offset) {
        return 0;
    }

    @Override
    public void deleteAll() {

    }
}
