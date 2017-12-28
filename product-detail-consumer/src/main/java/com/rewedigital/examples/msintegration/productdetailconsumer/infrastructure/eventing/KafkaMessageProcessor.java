package com.rewedigital.examples.msintegration.productdetailconsumer.infrastructure.eventing;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface KafkaMessageProcessor {

    /**
     * will be called for each received kafka message
     *
     * When returning SUCCESS or PERMANENT_ERROR the record will be acknowledged.
     * When returning UNEXPECTED_ERROR the record will be acknowledged and backed.
     * When returning TEMPORARY_ERROR the processing of this record will be retried and all further record won't be
     * processed as long as this method returns a TEMPORARY_ERROR
     *
     * @param consumerRecord the received {@link ConsumerRecord}
     * @return the resulting {@link MessageProcessingState}
     */
    MessageProcessingState processConsumerRecord(ConsumerRecord<String, String> consumerRecord);

    /**
     * @return the {@link ConsumerTopicConfig} of the topic that should be processed by the implementing class
     */
    ConsumerTopicConfig getTopicConfig();

}
