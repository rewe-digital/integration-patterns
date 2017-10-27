package com.rewedigital.examples.msintegration.productinformation.product;

import com.rewedigital.examples.msintegration.productinformation.ProductInformationApplication;
import com.rewedigital.examples.msintegration.productinformation.product.Product;
import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ProductInformationApplication.class)
public class ProductRestControllerTest {

    @Inject private TestRestTemplate restTemplate;

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

        ResponseEntity response = restTemplate.postForEntity("/products", product, Object.class);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }
}
