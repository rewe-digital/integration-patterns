package com.rewedigital.examples.msintegration.productinformation.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.concurrent.ListenableFuture;

import com.rewedigital.examples.msintegration.productinformation.scheduling.ProductEventProcessingTask;

public class ProductEventProcessingTaskTest {

    @Autowired
    private ProductEventProcessingTask task;

    private ProductEventRepository repo;
    private ProductEventPublisher publisher;

    private ProductEvent p1;
    private ProductEvent p2;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        repo = mock(ProductEventRepository.class);
        publisher = mock(ProductEventPublisher.class);

        p1 = mock(ProductEvent.class);
        p2 = mock(ProductEvent.class);

        when(publisher.publish(any(ProductEvent.class))).thenReturn(mock(ListenableFuture.class));

        task = new ProductEventProcessingTask(publisher, repo);
    }

    @Test
    public void testProcessingNextSingleBatch() {
        when(repo.findFirstByOrderByTimeAsc()).thenReturn(p1);
        assertThat(task.processNextMessage()).isPresent();

        when(repo.findFirstByOrderByTimeAsc()).thenReturn(p2);
        assertThat(task.processNextMessage()).isPresent();

        when(repo.findFirstByOrderByTimeAsc()).thenReturn(null);
        assertThat(task.processNextMessage()).isEmpty();

        verify(publisher, times(2)).publish(any(ProductEvent.class));
    }
}
