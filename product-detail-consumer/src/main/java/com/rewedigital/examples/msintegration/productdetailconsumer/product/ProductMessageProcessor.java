package com.rewedigital.examples.msintegration.productdetailconsumer.product;

import com.rewedigital.examples.msintegration.productdetailconsumer.infrastructure.eventing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class ProductMessageProcessor extends AbstractKafkaMessageProcessor<ProductMessage>{

    private static final Logger LOG = LoggerFactory.getLogger(AbstractKafkaMessageProcessor.class);

    private final ProductService productService;

    @Inject
    public ProductMessageProcessor(ConsumerTopicConfig productTopicConfig, MessageParser messageParser, ProcessedMessageStore processedMessageStore, ProductService productService) {
        super(ProductMessage.class, productTopicConfig, messageParser, processedMessageStore);
        this.productService = productService;
    }

    @Override
    protected MessageProcessingState processMessage(final ProductMessage kafkaMessage) {
        switch (kafkaMessage.getType()) {
            case "product-created":
            case "product-updated":
                productService.createOrUpdateProduct(kafkaMessage);
                break;
            default:
                LOG.warn("Unexpected type: '{}' of message with key '{}'", kafkaMessage.getType(),
                        kafkaMessage.getKey());
                return MessageProcessingState.UNEXPECTED_ERROR;
        }
        return MessageProcessingState.SUCCESS;
    }


}
