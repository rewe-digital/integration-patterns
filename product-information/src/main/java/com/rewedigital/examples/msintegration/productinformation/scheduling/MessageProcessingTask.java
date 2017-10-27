package com.rewedigital.examples.msintegration.productinformation.scheduling;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.rewedigital.examples.msintegration.productinformation.product.ProductEvent;
import com.rewedigital.examples.msintegration.productinformation.product.ProductEventPublisher;

@Component
public class MessageProcessingTask {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessingTask.class);

    final private ProductEventPublisher eventPublisher;

    @Inject
    public MessageProcessingTask(final ProductEventPublisher eventPublisher) {
        this.eventPublisher=eventPublisher;
    }


    @Scheduled(fixedRate = 1000)
    public void processNextMessageBatch() {
        LOG.debug("performing next message batch");

        pickUnprocessedMessages().stream().forEach(e -> publishEventAndDeleteFromDB(e));

    }

    private List<ProductEvent> pickUnprocessedMessages() {
        return Collections.emptyList();
    }

    @Transactional
    private void publishEventAndDeleteFromDB(final ProductEvent productEvent) {
        eventPublisher.publish(productEvent);
        //TODO delete
    }



}
