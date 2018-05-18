package com.rewedigital.examples.msintegration.productinformation.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.rewedigital.examples.msintegration.productinformation.helper.AbstractIntegrationTest;
import com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing.internal.DomainEvent;
import com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing.internal.DomainEventPublisher;

public class DomainEventRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private DomainEventPublisher eventPublisher;
    
    private DomainEvent p1;
    private DomainEvent p2;
    private DomainEvent p3;
    private DomainEvent p4;
    private DomainEvent p5;

    //@Before
    public void insertEvents() {
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
        entityManager.persist(p1);

        p2 = createProductEvent("event1", ZonedDateTime.parse("2017-01-01T09:01:00Z[GMT]"), 2L);
        entityManager.persist(p2);

        p3 = createProductEvent("event2", ZonedDateTime.parse("2017-01-01T10:00:00Z[GMT]"), 2L);
        entityManager.persist(p3);

        p4 = createProductEvent("event2", ZonedDateTime.parse("2017-01-01T10:01:00Z[GMT]"), 1L);
        entityManager.persist(p4);

        p5 = createProductEvent("event3", ZonedDateTime.parse("2017-01-01T08:45:00Z[GMT]"), 3L);
        entityManager.persist(p5);
    }

    private DomainEvent createProductEvent(final String key, final ZonedDateTime time, final Long version) {
        final DomainEvent p = new DomainEvent();
        p.setId(UUID.randomUUID().toString());
        p.setKey(key);
        p.setPayload(new byte[0]);
        p.setTime(time);
        p.setType("product.created");
        p.setVersion(version);
        return p;
    }

    @Test
    @Transactional
    public void testFindFirstByTimeInSmallestVersion() {
        insertEvents();
        
        final DomainEvent firstEvent = eventPublisher.findUnprocessedEvents().get(0);
        assertThat(firstEvent.getKey()).isEqualTo(p5.getKey());
        entityManager.remove(firstEvent);

        final DomainEvent secondEvent = eventPublisher.findUnprocessedEvents().get(0);
        assertThat(secondEvent.getKey()).isEqualTo(p1.getKey());
        entityManager.remove(secondEvent);

        final DomainEvent thirdEvent = eventPublisher.findUnprocessedEvents().get(0);
        assertThat(thirdEvent.getKey()).isEqualTo(p2.getKey());
        entityManager.remove(thirdEvent);

        final DomainEvent fourthEvent = eventPublisher.findUnprocessedEvents().get(0);
        assertThat(fourthEvent.getKey()).isEqualTo(p4.getKey());
        entityManager.remove(fourthEvent);

        final DomainEvent fifthEvent = eventPublisher.findUnprocessedEvents().get(0);
        assertThat(fifthEvent.getKey()).isEqualTo(p3.getKey());
        entityManager.remove(fifthEvent);
    }

}