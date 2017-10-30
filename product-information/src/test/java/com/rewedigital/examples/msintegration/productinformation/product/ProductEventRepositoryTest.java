package com.rewedigital.examples.msintegration.productinformation.product;

import com.rewedigital.examples.msintegration.productinformation.helper.AbstractIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.UUID;

public class ProductEventRepositoryTest extends AbstractIntegrationTest{

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

    private ProductEvent createProductEvent(String key) {
        ProductEvent p = new ProductEvent();
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
        ProductEvent pFromRepo = productEventRepository.findFirstByOrderByTimeAsc();
        assert pFromRepo.getKey().equals(p1.getKey());
    }

}
