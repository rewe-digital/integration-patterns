package com.rewedigital.examples.msintegration.productinformation.scheduling;

import com.rewedigital.examples.msintegration.productinformation.product.ProductEvent;
import com.rewedigital.examples.msintegration.productinformation.product.ProductEventPublisher;
import com.rewedigital.examples.msintegration.productinformation.product.ProductEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Component
public class ProductEventProcessingTask {

    private static final Logger LOG = LoggerFactory.getLogger(ProductEventProcessingTask.class);

    final private ProductEventPublisher eventPublisher;
    final private ProductEventRepository productEventRepository;

    @Inject
    public ProductEventProcessingTask(final ProductEventPublisher eventPublisher, final ProductEventRepository productEventRepository) {
        this.eventPublisher=eventPublisher;
        this.productEventRepository = productEventRepository;
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
        eventPublisher.publish(productEvent);
        productEventRepository.delete(productEvent);
    }

}
