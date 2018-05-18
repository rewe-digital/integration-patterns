package com.rewedigital.examples.msintegration.productinformation.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.rewedigital.examples.msintegration.productinformation.helper.AbstractIntegrationTest;
import com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing.internal.LastPublishedVersion;

public class ProductRestControllerTest extends AbstractIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    public void testProductInsert() {

        final Product product = new Product();
        product.setName("Bla!");
        product.setProductNumber("1234");

        final Product response = restTemplate.postForObject("/products", product, Product.class);

        assertThat(response).isNotNull();
        assertThat(response.getVersion()).isNotNull();

        // wait until event is published to kafka
        assertThatLastPublishedVersionBecomes(response.getId(), 0L);
    }

    @Test
    public void testProductBadInsert() {

        final Product product = new Product();
        product.setId("108");

        final ResponseEntity<?> response = restTemplate.postForEntity("/products", product, Object.class);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }


    private void assertThatLastPublishedVersionBecomes(final String id, final long version) {
       LastPublishedVersion result = null;
        int tryCount = 0;
        while (result == null && tryCount <= 5) {
            result = entityManager.find(LastPublishedVersion.class, "product-" + id);
            if(result != null && result.getVersion() == version) {
                return;
            }
            try {
                Thread.sleep(100);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
            ++tryCount;
        }
        fail("expected last published version of [" + id + "] to become [" + version + "] but was [" + result + "]");
    }
}
