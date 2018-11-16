package com.rewedigital.examples.msintegration.productdetailpage.product;

import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.AbstractDomainEventProcessor;
import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.EventParser;
import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.EventProcessingState;
import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.configuration.ConsumerTopicConfig;
import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.processed.ProcessedEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class ProductEventProcessor extends AbstractDomainEventProcessor<ProductPayload, ProductEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDomainEventProcessor.class);

    private final JpaProductRepository repository;;

    @Inject
    public ProductEventProcessor(final ConsumerTopicConfig productTopicConfig, final EventParser eventParser, final ProcessedEventService processedEventService, final JpaProductRepository repository) {
        super(ProductEvent.class, productTopicConfig, eventParser, processedEventService);
        this.repository = repository;
    }

    @Override
    protected EventProcessingState processEvent(final ProductEvent productEvent) {
        switch (productEvent.getType()) {
            case "product-created":
            case "product-updated":
                repository.save(toProduct(productEvent));
                break;
            default:
                LOG.warn("Unexpected type: '{}' of message with key '{}'", productEvent.getType(),
                        productEvent.getKey());
                return EventProcessingState.UNEXPECTED_ERROR;
        }
        return EventProcessingState.SUCCESS;
    }

    private Product toProduct(final ProductEvent productEvent) {
        final Product product = new Product();
        product.setId(productEvent.getPayload().getId());
        product.setDescription(productEvent.getPayload().getDescription());
        product.setImage(productEvent.getPayload().getImage());
        product.setPrice(productEvent.getPayload().getPrice());
        product.setProductNumber(productEvent.getPayload().getProductNumber());
        product.setVendor(productEvent.getPayload().getVendor());
        product.setName(productEvent.getPayload().getName());
        product.setVersion(productEvent.getVersion());

        return product;
    }
}
