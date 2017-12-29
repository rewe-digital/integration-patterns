package com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing;

public interface ProcessedMessageStore {

    /**
     * @param topic the topic of the {@link AbstractKafkaConsumer} who received the processed message.
     * @param key the processed {@link KafkaMessage}'s key.
     *
     * @return the version of the last processed message or 0 if no message with the given key on the given topic has
     * been processed, yet.
     */
    long getLastProcessedVersion(String topic, String key);

    /**
     * @param topic the topic of the {@link AbstractKafkaConsumer} who received the processed message.
     * @param key the processed {@link KafkaMessage}'s key.
     * @param version the version of the newly processed message.
     */
    void updateLastProcessedVersion(String topic, String key, long version);

}