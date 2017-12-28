package com.rewedigital.examples.msintegration.productdetailconsumer.product;

import com.rewedigital.examples.msintegration.productdetailconsumer.infrastructure.eventing.ConsumerTopicConfig;
import org.springframework.stereotype.Component;

@Component
public class ProductTopicConfig implements ConsumerTopicConfig{
    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isPayloadSensitive() {
        return false;
    }
}
