package com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;

/**
 * Saves un-processable messages for later processing.
 */
public interface ConsumerRecordStore {

    /**
     * @param consumerRecord the {@link ConsumerRecord} to be saved for later processing.
     */
    void save(ConsumerRecord<String, String> consumerRecord);

    /**
     * @return a {@link List} of all {@link ConsumerRecord}s stored.
     */
    List<ConsumerRecord<String, String>> getAll();

    /**
     * @param topic the topic of the desired {@link ConsumerRecord}s
     * @return a {@link List} of all {@link ConsumerRecord}s for the given topic.
     */
    List<ConsumerRecord<String, String>> getAllForTopic(String topic);

    /**
     * Deletes the {@link ConsumerRecord}s with the given topic, partition and offset.
     * @param topic the topic of the {@link ConsumerRecord}s which should be deleted.
     * @param partition the partition of the {@link ConsumerRecord}s which should be deleted.
     * @param offset this offset of the {@link ConsumerRecord}s which should be deleted.
     * @return the number of {@link ConsumerRecord}s which have been deleted.
     */
    int delete(String topic, int partition, long offset);

    /**
     * Deletes all stored {@link ConsumerRecord}s.
     */
    void deleteAll();

}
