package com.rewedigital.examples.msintegration.productdetailpage.product;

import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.ConsumerTopicConfig;
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
