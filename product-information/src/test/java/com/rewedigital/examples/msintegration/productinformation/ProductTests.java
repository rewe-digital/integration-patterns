package com.rewedigital.examples.msintegration.productinformation;

import com.rewedigital.examples.msintegration.productinformation.product.Product;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ProductInformationApplication.class)
public class ProductTests {

    @Inject private TestRestTemplate restTemplate;

    @Test
    public void testProductInsert() {

        Product product = new Product();
        product.setId("108");
        product.setVersion(42L);
        product.setName("Bla!");

        Product response = restTemplate.postForObject("/products", product, Product.class);

        Assertions.assertThat(response).isNotNull();
    }
}
