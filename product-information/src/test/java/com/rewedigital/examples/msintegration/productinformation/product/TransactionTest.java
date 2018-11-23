package com.rewedigital.examples.msintegration.productinformation.product;

import com.rewedigital.examples.msintegration.productinformation.helper.AbstractIntegrationTest;
import com.rewedigital.examples.msintegration.productinformation.helper.TestUtil;
import com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing.internal.DomainEvent;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

public class TransactionTest extends AbstractIntegrationTest {

    @SpyBean
    private EntityManager entityManager;

    @SpyBean
    private JpaProductRepository productRepository;

    @Test
    public void testInsertWithFailingEventPersistence() {
        doThrow(PersistenceException.class).when(entityManager).persist(any(DomainEvent.class));

        final ResponseEntity<Product> response = restTemplate.postForEntity("/products", TestUtil.getTestProduct(), Product.class);

        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(productRepository.findAll()).isEmpty();
        assertThat(getDomainEventsFromDb()).isEmpty();
    }

    @Test
    public void testInsertWithFailingProductPersistence() {
        doThrow(RuntimeException.class).when(productRepository).save(any(Product.class));

        final ResponseEntity<Product> response = restTemplate.postForEntity("/products", TestUtil.getTestProduct(), Product.class);

        assertThat(productRepository.findAll().size()).isEqualTo(0);
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(getDomainEventsFromDb()).isEmpty();
    }


    private List<DomainEvent> getDomainEventsFromDb() {
        return entityManager.createQuery("SELECT e FROM DomainEvent e").getResultList();
    }
}
