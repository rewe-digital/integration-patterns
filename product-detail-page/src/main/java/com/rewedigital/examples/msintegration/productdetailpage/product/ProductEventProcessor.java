package com.rewedigital.examples.msintegration.productdetailpage.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;

@Component
public class ProductEventProcessor extends AbstractDomainEventProcessor<ProductEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDomainEventProcessor.class);

    private final ProductService productService;
    private final ObjectMapper objectMapper;

    @Inject
    public ProductEventProcessor(ConsumerTopicConfig productTopicConfig, EventParser eventParser, ProcessedEventStore processedEventStore, ProductService productService, ObjectMapper objectMapper) {
        super(ProductEvent.class, productTopicConfig, eventParser, processedEventStore);
        this.productService = productService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected EventProcessingState processMessage(final ProductEvent kafkaMessage) {
        switch (kafkaMessage.getType()) {
            case "product-created":
            case "product-updated":
                productService.createOrUpdateProduct(toProduct(kafkaMessage));
                break;
            default:
                LOG.warn("Unexpected type: '{}' of message with key '{}'", kafkaMessage.getType(),
                        kafkaMessage.getKey());
                return EventProcessingState.UNEXPECTED_ERROR;
        }
        return EventProcessingState.SUCCESS;
    }

    private Product toProduct(ProductEvent productEvent) {
        try {
            return objectMapper.readValue(productEvent.getPayload(), Product.class);
        } catch (IOException e) {
            throw new RuntimeException("Could not map product", e);
        }
    }


}
