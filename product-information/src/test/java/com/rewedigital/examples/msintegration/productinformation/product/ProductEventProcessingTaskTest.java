package com.rewedigital.examples.msintegration.productinformation.product;

import com.rewedigital.examples.msintegration.productinformation.scheduling.ProductEventProcessingTask;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.concurrent.ListenableFuture;

import java.time.ZonedDateTime;

import static org.mockito.Mockito.*;

public class ProductEventProcessingTaskTest {

    @Autowired
    private ProductEventProcessingTask task;

    private ProductEventRepository repo;
    private ProductEventPublisher publisher;

    private ProductEvent p1;
    private ProductEvent p2;

    @Before
    public void setup() {
        repo = mock(ProductEventRepository.class);
        publisher = mock(ProductEventPublisher.class);

        p1 = setupProductEvent("1", 1L);
        p2 = setupProductEvent("2", 1L);
        
        when(publisher.publish(any(ProductEvent.class))).thenReturn(mock(ListenableFuture.class));

        task = new ProductEventProcessingTask(publisher, repo);
    }

    private ProductEvent setupProductEvent(String id, long version) {
        ProductEvent p = new ProductEvent();
        p.setId(id);
        p.setKey("event1");
        p.setPayload("{}");
        p.setTime(ZonedDateTime.now());
        p.setType("product.created");
        p.setVersion(version);
        repo.save(p);

        return p;
    }

    @Test
    public void testProcessingNextSingleBatch() {
        when(repo.findFirstByOrderByTimeAsc()).thenReturn(p1);
        task.processNextMessageBatch();

        when(repo.findFirstByOrderByTimeAsc()).thenReturn(p2);
        task.processNextMessageBatch();

        when(repo.findFirstByOrderByTimeAsc()).thenReturn(null);
        task.processNextMessageBatch();
        verify(publisher, times(2)).publish(any(ProductEvent.class));
    }
}
