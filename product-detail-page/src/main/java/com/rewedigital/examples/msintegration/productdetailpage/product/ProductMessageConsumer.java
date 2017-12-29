package com.rewedigital.examples.msintegration.productdetailpage.product;

import com.google.common.collect.ImmutableSet;
import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.AbstractKafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.dao.UncategorizedDataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;

import javax.inject.Inject;

@Component
public class ProductMessageConsumer extends AbstractKafkaConsumer {

    @Inject
    protected ProductMessageConsumer(ProductMessageProcessor messageProcessor, ProductRecordStore productRecordStore) {
        super(messageProcessor, productRecordStore,
                ImmutableSet.of(UncategorizedDataAccessException.class, TransientDataAccessException.class,
                        CannotCreateTransactionException.class));
    }

    @KafkaListener(topics = "${productqueue.topic_name}")
    public void listen(final ConsumerRecord<String, String> consumerRecord, final Acknowledgment ack) {
        super.handleConsumerRecord(consumerRecord, ack);
    }
}
