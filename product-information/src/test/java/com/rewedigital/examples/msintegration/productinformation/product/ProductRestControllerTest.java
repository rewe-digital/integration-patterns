package com.rewedigital.examples.msintegration.productinformation.product;

import com.rewedigital.examples.msintegration.productinformation.helper.AbstractIntegrationTest;
import com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing.LastPublishedVersion;
import com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing.LastPublishedVersionRepository;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class ProductRestControllerTest extends AbstractIntegrationTest {

    @Inject
    private LastPublishedVersionRepository lastPublishedVersionRepository;

    @Test
    public void testProductInsert() {

        final Product product = new Product();
        product.setName("Bla!");
        product.setProductNumber("1234");

        final Product response = restTemplate.postForObject("/products", product, Product.class);

        // wait until event is received from kafka

        assertThat(response).isNotNull();
        assertThat(response.getVersion()).isNotNull();

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
            result = lastPublishedVersionRepository.findById("product-" + id).orElse(null);
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
        fail("expected last published version of [" + id + "] to become [" + version + "]");
    }
}
