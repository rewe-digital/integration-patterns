package com.rewedigital.examples.msintegration.productdetailpage.product;

import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.ProcessedMessageStore;
import org.springframework.stereotype.Component;

@Component
public class ProductProccessedStore implements ProcessedMessageStore{
    @Override
    public long getLastProcessedVersion(String topic, String key) {
        return 0;
    }

    @Override
    public void updateLastProcessedVersion(String topic, String key, long version) {

    }
}
