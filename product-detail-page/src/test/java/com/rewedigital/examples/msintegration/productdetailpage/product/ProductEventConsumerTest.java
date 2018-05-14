package com.rewedigital.examples.msintegration.productdetailpage.product;

import static com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.EventProcessingState.SUCCESS;
import static com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.EventProcessingState.TEMPORARY_ERROR;
import static com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.EventProcessingState.UNEXPECTED_ERROR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.kafka.support.Acknowledgment;

import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.EventProcessingState;
import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.configuration.SimpleConsumerTopicConfig;
import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.exception.TemporaryKafkaProcessingError;
import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.unprocessable.UnprocessableEventService;

@RunWith(MockitoJUnitRunner.class)
public class ProductEventConsumerTest {

    private static final String TOPIC = "topic";
    private static final int PARTITION = 0;
    private static final int OFFSET = 0;
    private static final String KEY = UUID.randomUUID().toString();

    private static final ConsumerRecord<String, String> CONSUMER_RECORD = new ConsumerRecord<>(TOPIC, PARTITION,
            OFFSET, KEY, "{}");

    ProductEventConsumer productEventConsumer;

    @Mock
    UnprocessableEventService unprocessableEventService;

    @Mock
    Acknowledgment ack;

    public com.rewedigital.examples.msintegration.productdetailpage.product.ProductEventConsumer getProductEventConsumer() {
        return productEventConsumer;
    }

    @Test
    public void eventWithSyntaxErrorShouldNeitherBeProcessedNorStoredAsUnprocessable() {
        productEventConsumer = new ProductEventConsumer(mockedProcessor(SUCCESS), unprocessableEventService);
        productEventConsumer.listen(CONSUMER_RECORD,ack);

        verify(ack).acknowledge();
        verify(unprocessableEventService, never()).save(any());
    }

    @Test
    public void eventLeadingToUnexpectedErrorShouldBeStoredAsUnprocessable() {
        final ProductEventProcessor processor = mockedProcessor(UNEXPECTED_ERROR);
        productEventConsumer = new ProductEventConsumer(processor, unprocessableEventService);
        productEventConsumer.listen(CONSUMER_RECORD,ack);

        verify(ack).acknowledge();
        verify(processor).processConsumerRecord(any());
        verify(unprocessableEventService).save(any());
    }

    @Test(expected = TemporaryKafkaProcessingError.class)
    public void temporaryErrorShouldStoreEventAsUnprocessable() {
        final ProductEventProcessor processor = mockedProcessor(TEMPORARY_ERROR);
        productEventConsumer = new ProductEventConsumer(processor, unprocessableEventService);
        productEventConsumer.listen(CONSUMER_RECORD,ack);

        verify(ack, never()).acknowledge();
        verify(processor).processEvent(any());
        verify(unprocessableEventService).save(any());
    }

    private ProductEventProcessor mockedProcessor(final EventProcessingState expectedEventProcessingState) {
        final ProductEventProcessor productEventProcessor = mock(ProductEventProcessor.class);
        when(productEventProcessor.getTopicConfig()).thenReturn(new SimpleConsumerTopicConfig());
        when(productEventProcessor.processConsumerRecord(CONSUMER_RECORD)).thenReturn(expectedEventProcessingState);
        return productEventProcessor;
    }

}