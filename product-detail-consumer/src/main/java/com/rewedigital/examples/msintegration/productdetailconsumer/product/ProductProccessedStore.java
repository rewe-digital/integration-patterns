package com.rewedigital.examples.msintegration.productdetailconsumer.product;

import com.rewedigital.examples.msintegration.productdetailconsumer.infrastructure.eventing.ProcessedMessageStore;
import org.springframework.stereotype.Component;

@Component
public class ProductProccessedStore implements ProcessedMessageStore{
    @Override
    public int getLastProcessedVersion(String topic, String key) {
        return 0;
    }

    @Override
    public void updateLastProcessedVersion(String topic, String key, int version) {

    }
}
