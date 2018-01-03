package com.rewedigital.examples.msintegration.productinformation.product;

import com.rewedigital.examples.msintegration.productinformation.helper.AbstractIntegrationTest;
import com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing.DomainEvent;
import com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing.DomainEventRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductEventRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    DomainEventRepository productEventRepository;

    private DomainEvent p1;
    private DomainEvent p2;
    private DomainEvent p3;
    private DomainEvent p4;
    private DomainEvent p5;

    @Before
    public void setup() {
        /*
        Test Setup

        key   | time  | version || expected order
        event1  09:00   1           2
        event1  09:01   2           3
        event2  10:00   2           5
        event2  10:01   1           4
        event3  08:45   3           1
         */
        p1 = createProductEvent("event1", ZonedDateTime.parse("2017-01-01T09:00:00Z[GMT]"), 1L);
        productEventRepository.save(p1);

        p2 = createProductEvent("event1", ZonedDateTime.parse("2017-01-01T09:01:00Z[GMT]"), 2L);
        productEventRepository.save(p2);

        p3 = createProductEvent("event2", ZonedDateTime.parse("2017-01-01T10:00:00Z[GMT]"), 2L);
        productEventRepository.save(p3);

        p4 = createProductEvent("event2", ZonedDateTime.parse("2017-01-01T10:01:00Z[GMT]"), 1L);
        productEventRepository.save(p4);

        p5 = createProductEvent("event3", ZonedDateTime.parse("2017-01-01T08:45:00Z[GMT]"), 3L);
        productEventRepository.save(p5);
    }

    private ProductEvent createProductEvent(final String key, final ZonedDateTime time, final Long version) {
        final ProductEvent p = new ProductEvent();
        p.setId(UUID.randomUUID().toString());
        p.setKey(key);
        p.setPayload(new ProductPayload());
        p.setTime(time);
        p.setType("product.created");
        p.setVersion(version);
        return p;
    }

    @Test
    public void testFindFirstQuery() {
        final DomainEvent foundEvent = productEventRepository.findFirstByOrderByTimeAsc();
        assertThat(foundEvent.getKey()).isEqualTo(p5.getKey());
    }

    @Test
    public void testFindFirstByTimeInSmallestVersion() {
        final DomainEvent firstEvent = productEventRepository.findFirstByTimeInSmallestVersion();
        assertThat(firstEvent.getKey()).isEqualTo(p5.getKey());
        productEventRepository.delete(firstEvent.getId());

        final DomainEvent secondEvent = productEventRepository.findFirstByTimeInSmallestVersion();
        assertThat(secondEvent.getKey()).isEqualTo(p1.getKey());
        productEventRepository.delete(secondEvent.getId());

        final DomainEvent thirdEvent = productEventRepository.findFirstByTimeInSmallestVersion();
        assertThat(thirdEvent.getKey()).isEqualTo(p2.getKey());
        productEventRepository.delete(thirdEvent.getId());

        final DomainEvent fourthEvent = productEventRepository.findFirstByTimeInSmallestVersion();
        assertThat(fourthEvent.getKey()).isEqualTo(p4.getKey());
        productEventRepository.delete(fourthEvent.getId());

        final DomainEvent fifthEvent = productEventRepository.findFirstByTimeInSmallestVersion();
        assertThat(fifthEvent.getKey()).isEqualTo(p3.getKey());
        productEventRepository.delete(fifthEvent.getId());
    }

}