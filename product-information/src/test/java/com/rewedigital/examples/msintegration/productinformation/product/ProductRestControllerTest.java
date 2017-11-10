package com.rewedigital.examples.msintegration.productinformation.product;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.rewedigital.examples.msintegration.productinformation.helper.AbstractIntegrationTest;

public class ProductRestControllerTest extends AbstractIntegrationTest {

    @Test
    public void testProductInsert() {

        final Product product = new Product();
        product.setName("Bla!");

        final Product response = restTemplate.postForObject("/products", product, Product.class);

        assertThat(response).isNotNull();
        assertThat(response.getVersion()).isNotNull();
    }

    @Test
    public void testProductBadInsert() {

        final Product product = new Product();
        product.setId("108");

        final ResponseEntity<?> response = restTemplate.postForEntity("/products", product, Object.class);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }
}
