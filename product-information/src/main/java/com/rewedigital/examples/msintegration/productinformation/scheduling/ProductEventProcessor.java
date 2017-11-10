package com.rewedigital.examples.msintegration.productinformation.scheduling;

import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.rewedigital.examples.msintegration.productinformation.product.ProductEvent;
import com.rewedigital.examples.msintegration.productinformation.product.ProductEventPublisher;
import com.rewedigital.examples.msintegration.productinformation.product.ProductEventRepository;
import com.rewedigital.examples.msintegration.productinformation.product.ProductLastPublishedVersion;
import com.rewedigital.examples.msintegration.productinformation.product.ProductLastPublishedVersionRepository;

@Component
public class ProductEventProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(ProductEventProcessor.class);

    final private ProductEventPublisher eventPublisher;
    final private ProductEventRepository productEventRepository;
    final private ProductLastPublishedVersionRepository lastPublishedVersionRepository;

    @Inject
    public ProductEventProcessor(final ProductEventPublisher eventPublisher,
        final ProductEventRepository productEventRepository,
        final ProductLastPublishedVersionRepository lastPublishedVersionRepository) {
        this.eventPublisher = eventPublisher;
        this.productEventRepository = productEventRepository;
        this.lastPublishedVersionRepository = lastPublishedVersionRepository;
    }

    @Transactional
    public void process(final String eventId) {
        sendEvent(productEventRepository.findOne(eventId));
    }

    @Transactional
    public void processNext() {
        sendEvent(productEventRepository.findFirstByOrderByTimeAsc());
    }

    private void sendEvent(final ProductEvent productEvent) {
        if (productEvent == null) {
            return;
        }

        final String productId = productEvent.getKey();
        final Optional<ProductLastPublishedVersion> lastPublishedVersion = obtainLastPublishedVersion(productId);
        lastPublishedVersion.ifPresent(v -> {
            try {
                if (v.getVersion() < productEvent.getVersion()) {
                    eventPublisher.publish(productEvent).get(); // need to block here so that following statements are
                                                                // executed inside transaction
                    v.setVersion(productEvent.getVersion());
                    lastPublishedVersionRepository.save(v);
                }
                productEventRepository.delete(productEvent);
            } catch (final Exception ex) {
                LOG.error("error publishing event with id [{}] due to {}", productEvent.getId(), ex.getMessage(), ex);
            }
        });
    }

    private Optional<ProductLastPublishedVersion> obtainLastPublishedVersion(final String productId) {
        ProductLastPublishedVersion lastPublishedVersion = lastPublishedVersionRepository.findOne(productId);
        if (lastPublishedVersion == null) {
            try {
                lastPublishedVersion =
                    lastPublishedVersionRepository.saveAndFlush(ProductLastPublishedVersion.of(productId));
            } catch (final Exception ex) {
                return Optional.empty();
            }
        }
        return Optional.of(lastPublishedVersion);
    }
}
