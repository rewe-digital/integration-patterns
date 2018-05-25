package com.rewedigital.examples.msintegration.productinformation.product;

import static java.time.ZonedDateTime.parse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
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

    public List<DomainEvent> prepareEvents() {
        final List<DomainEvent> events = Arrays.asList(
            event("entity1", "2017-01-01T09:01:00Z[GMT]", 2L),
            event("entity2", "2017-01-01T10:01:00Z[GMT]", 1L),
            event("entity3", "2017-01-01T08:45:00Z[GMT]", 3L),
            event("entity1", "2017-01-01T09:00:00Z[GMT]", 1L),
            event("entity2", "2017-01-01T10:00:00Z[GMT]", 2L));

        events.forEach(e -> entityManager.persist(e));
        return events;
    }

    @Test
    @Transactional
    public void testFindFirstByTimeInSmallestVersion() {
        assertOrderEarlierVersionsFirst(findUnprocessedEvents(20));
    }

    private void assertOrderEarlierVersionsFirst(List<DomainEvent> events) {
        for (int i = 0; i < events.size(); i++) {
            final DomainEvent e = events.get(i);
            for (int j = 0; j < events.size() && j != i; j++) {
                DomainEvent f = events.get(j);
                if (!e.getKey().equals(f.getKey())) {
                    continue;
                }

                if (j < i) {
                    assertTrue("events for key " + e.getKey() + " in wrong order", f.getVersion() < e.getVersion());
                } else {
                    assertTrue("events for key " + e.getKey() + " in wrong order", f.getVersion() > e.getVersion());
                }
            }
        }
    }

    private List<DomainEvent> findUnprocessedEvents(int batchSize) {
        return eventPublisher.findUnprocessedEvents(batchSize);
    }

    private DomainEvent event(final String key, final String time, final Long version) {
        final DomainEvent p = new DomainEvent();
        p.setId(UUID.randomUUID().toString());
        p.setKey(key);
        p.setPayload(new byte[0]);
        p.setTime(parse(time));
        p.setType("entity.created");
        p.setVersion(version);
        return p;
    }

}
