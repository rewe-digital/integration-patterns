package com.rewedigital.examples.msintegration.productdetailpage.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;

@Component
public class ProductMessageProcessor extends AbstractKafkaMessageProcessor<ProductMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractKafkaMessageProcessor.class);

    private final ProductService productService;
    private final ObjectMapper objectMapper;

    @Inject
    public ProductMessageProcessor(ConsumerTopicConfig productTopicConfig, MessageParser messageParser, ProcessedMessageStore processedMessageStore, ProductService productService, ObjectMapper objectMapper) {
        super(ProductMessage.class, productTopicConfig, messageParser, processedMessageStore);
        this.productService = productService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected MessageProcessingState processMessage(final ProductMessage kafkaMessage) {
        switch (kafkaMessage.getType()) {
            case "product-created":
            case "product-updated":
                productService.createOrUpdateProduct(toProduct(kafkaMessage));
                break;
            default:
                LOG.warn("Unexpected type: '{}' of message with key '{}'", kafkaMessage.getType(),
                        kafkaMessage.getKey());
                return MessageProcessingState.UNEXPECTED_ERROR;
        }
        return MessageProcessingState.SUCCESS;
    }

    private Product toProduct(ProductMessage productMessage) {
        try {
            return objectMapper.readValue(productMessage.getPayload(), Product.class);
        } catch (IOException e) {
            throw new RuntimeException("Could not map product", e);
        }
    }


}
