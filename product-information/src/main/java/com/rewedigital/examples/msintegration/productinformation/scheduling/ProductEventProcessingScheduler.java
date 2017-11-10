package com.rewedigital.examples.msintegration.productinformation.scheduling;

import javax.inject.Inject;

import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.rewedigital.examples.msintegration.productinformation.product.ProductEvent;

@Component
public class ProductEventProcessingScheduler implements ApplicationListener<ProductEvent.Message> {

    final ProductEventProcessor eventProcessor;

    @Inject
    public ProductEventProcessingScheduler(final ProductEventProcessor eventProcessor) {
        this.eventProcessor = eventProcessor;
    }

    @Override
    public void onApplicationEvent(final ProductEvent.Message event) {
        eventProcessor.process(event.id());
    }

    @Scheduled(fixedRate = 1000)
    public void processNextMessage() {
        eventProcessor.processNext();
    }
}
