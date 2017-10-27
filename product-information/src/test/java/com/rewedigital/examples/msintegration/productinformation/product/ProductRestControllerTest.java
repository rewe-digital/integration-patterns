package com.rewedigital.examples.msintegration.productinformation.product;

import com.rewedigital.examples.msintegration.productinformation.helper.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductRestControllerTest extends AbstractIntegrationTest {

    @Test
    public void testProductInsert() {

        Product product = new Product();
        product.setName("Bla!");

        Product response = restTemplate.postForObject("/products", product, Product.class);

        assertThat(response).isNotNull();
    }

    @Test
    public void testProductBadInsert() {

        Product product = new Product();
        product.setId("108");

        ResponseEntity<?> response = restTemplate.postForEntity("/products", product, Object.class);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }
}
