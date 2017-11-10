package com.rewedigital.examples.msintegration.productinformation.scheduling;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.rewedigital.examples.msintegration.productinformation.product.ProductEvent;
import com.rewedigital.examples.msintegration.productinformation.product.ProductEventPublisher;
import com.rewedigital.examples.msintegration.productinformation.product.ProductEventRepository;
import com.rewedigital.examples.msintegration.productinformation.product.ProductLastPublishedVersion;
import com.rewedigital.examples.msintegration.productinformation.product.ProductLastPublishedVersionRepository;

@Component
public class ProductEventProcessingTask implements ApplicationListener<ProductEvent.Message> {

    private static final Logger LOG = LoggerFactory.getLogger(ProductEventProcessingTask.class);

    final private ProductEventPublisher eventPublisher;
    final private ProductEventRepository productEventRepository;
    final private ProductLastPublishedVersionRepository lastPublishedVersionRepository;

    @Inject
    public ProductEventProcessingTask(final ProductEventPublisher eventPublisher,
        final ProductEventRepository productEventRepository,
        final ProductLastPublishedVersionRepository lastPublishedVersionRepository) {
        this.eventPublisher = eventPublisher;
        this.productEventRepository = productEventRepository;
        this.lastPublishedVersionRepository = lastPublishedVersionRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(final ProductEvent.Message event) {
        sendEvent(productEventRepository.findOne(event.id()));
    }

    @Scheduled(fixedRate = 1000)
    @Transactional
    public void processNextMessage() {
        sendEvent(productEventRepository.findFirstByOrderByTimeAsc());
    }

    private void sendEvent(final ProductEvent productEvent) {
        if (productEvent == null) {
            return;
        }

        final String productId = productEvent.getKey();
        try {
            final ProductLastPublishedVersion lastPublishedVersion = obtainLastPublishedVersion(productId);
            if (lastPublishedVersion.getVersion() < productEvent.getVersion()) {
                eventPublisher.publish(productEvent).get(); // need to block here so that following statements are
                                                            // executed inside transaction
                lastPublishedVersion.setVersion(productEvent.getVersion());
                lastPublishedVersionRepository.save(lastPublishedVersion);
            }
            productEventRepository.delete(productEvent);
        } catch (final Exception ex) {
            LOG.error("error publishing event with id [%s] due to %s", productEvent.getId(), ex.getMessage(), ex);
        }
    }

    private ProductLastPublishedVersion obtainLastPublishedVersion(final String productId) {
        ProductLastPublishedVersion lastPublishedVersion = lastPublishedVersionRepository.findOne(productId);
        if (lastPublishedVersion == null) {
            lastPublishedVersion =
                lastPublishedVersionRepository.saveAndFlush(ProductLastPublishedVersion.of(productId));
        }
        return lastPublishedVersion;
    }
}
