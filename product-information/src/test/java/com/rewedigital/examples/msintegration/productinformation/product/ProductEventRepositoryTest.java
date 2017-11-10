package com.rewedigital.examples.msintegration.productinformation.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.rewedigital.examples.msintegration.productinformation.helper.AbstractIntegrationTest;

public class ProductEventRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    ProductEventRepository productEventRepository;

    private ProductEvent p1;
    private ProductEvent p2;

    @Before
    public void setup() {
        p1 = createProductEvent("event1");
        productEventRepository.save(p1);

        p2 = createProductEvent("event2");
        productEventRepository.save(p2);
    }

    private ProductEvent createProductEvent(final String key) {
        final ProductEvent p = new ProductEvent();
        p.setId(UUID.randomUUID().toString());
        p.setKey(key);
        p.setPayload("{}");
        p.setTime(ZonedDateTime.now());
        p.setType("product.created");
        p.setVersion(1L);
        return p;
    }

    @Test
    public void testFindFirstQuery() {
        final ProductEvent foundEvent = productEventRepository.findFirstByOrderByTimeAsc();
        assertThat(foundEvent.getKey()).isEqualTo(p1.getKey());
    }

}
