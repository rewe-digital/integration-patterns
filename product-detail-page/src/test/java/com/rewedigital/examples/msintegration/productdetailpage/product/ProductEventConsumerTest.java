package com.rewedigital.examples.msintegration.productdetailpage.product;

import com.rewedigital.examples.msintegration.productdetailpage.configuration.SimpleConsumerTopicConfig;
import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.EventProcessingState;
import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.exception.TemporaryKafkaProcessingError;
import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.unprocessable.UnprocessableEventService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.kafka.support.Acknowledgment;

import java.util.UUID;

import static com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.EventProcessingState.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

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
    public void testEmptyPayload() {//Event syntax Error -> should neither be processed nor stored as unprocessable
        productEventConsumer = new ProductEventConsumer(mockedProcessor(SUCCESS), unprocessableEventService);
        productEventConsumer.listen(CONSUMER_RECORD,ack);

        verify(ack).acknowledge();
        verify(unprocessableEventService, times(0)).save(anyObject());
    }

    @Test
    public void testUnexpectedError() {//Unexpected Error -> Store as unprocessable
        ProductEventProcessor processor = mockedProcessor(UNEXPECTED_ERROR);
        productEventConsumer = new ProductEventConsumer(processor, unprocessableEventService);
        productEventConsumer.listen(CONSUMER_RECORD,ack);

        verify(ack).acknowledge();
        verify(processor, times(1)).processConsumerRecord(anyObject());
        verify(unprocessableEventService, times(1)).save(anyObject());
    }

    @Test(expected = TemporaryKafkaProcessingError.class)
    public void testTemporaryError() {//Kafka processing Error -> Store as unprocessable
        ProductEventProcessor processor = mockedProcessor(TEMPORARY_ERROR);
        productEventConsumer = new ProductEventConsumer(processor, unprocessableEventService);
        productEventConsumer.listen(CONSUMER_RECORD,ack);

        verify(ack, times(0)).acknowledge();
        verify(processor, times(1)).processMessage(anyObject());
        verify(unprocessableEventService, times(1)).save(anyObject());
    }

    private ProductEventProcessor mockedProcessor(EventProcessingState expectedEventProcessingState) {
        ProductEventProcessor productEventProcessor = mock(ProductEventProcessor.class);
        when(productEventProcessor.getTopicConfig()).thenReturn(new SimpleConsumerTopicConfig());
        when(productEventProcessor.processConsumerRecord(CONSUMER_RECORD)).thenReturn(expectedEventProcessingState);
        return productEventProcessor;
    }

}