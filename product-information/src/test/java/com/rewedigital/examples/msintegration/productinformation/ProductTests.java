package com.rewedigital.examples.msintegration.productinformation;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.rewedigital.examples.msintegration.productinformation.product.Product;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ProductInformationApplication.class)
public class ProductTests {

    @Inject private TestRestTemplate restTemplate;

    @Test
    public void testProductInsert() {

        final Product product = new Product();
        product.setId("108");
        product.setVersion(42L);
        product.setName("Bla!");

        final Product response = restTemplate.postForObject("/products", product, Product.class);

        Assertions.assertThat(response).isNotNull();
    }
}
