package com.rewedigital.examples.msintegration.productinformation.scheduling;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.rewedigital.examples.msintegration.productinformation.product.ProductEvent;
import com.rewedigital.examples.msintegration.productinformation.product.ProductEventPublisher;
import com.rewedigital.examples.msintegration.productinformation.product.ProductEventRepository;

@Component
public class ProductEventProcessingTask implements ApplicationListener<ProductEvent.Message> {

    private static final Logger LOG = LoggerFactory.getLogger(ProductEventProcessingTask.class);

    final private ProductEventPublisher eventPublisher;
    final private ProductEventRepository productEventRepository;

    @Inject
    public ProductEventProcessingTask(final ProductEventPublisher eventPublisher,
        final ProductEventRepository productEventRepository) {
        this.eventPublisher = eventPublisher;
        this.productEventRepository = productEventRepository;
    }

    @Override
    public void onApplicationEvent(final ProductEvent.Message event) {
        final ProductEvent productEvent = productEventRepository.getOne(event.id());
        publishEventAndDeleteFromDB(productEvent);
    }

    @Scheduled(fixedRate = 1000)
    public void processNextMessageBatch() {
        LOG.debug("performing next message batch");

        pickUnprocessedMessages().stream().forEach(e -> publishEventAndDeleteFromDB(e));
    }

    private List<ProductEvent> pickUnprocessedMessages() {
        return productEventRepository.findAll(new Sort(Sort.Direction.ASC, "time"));
    }

    private void publishEventAndDeleteFromDB(final ProductEvent productEvent) {
        if (productEvent == null) {
            return;
        }

        eventPublisher.publish(productEvent);
        productEventRepository.delete(productEvent);
    }
}
