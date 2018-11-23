package com.rewedigital.examples.msintegration.productinformation.helper;

import com.rewedigital.examples.msintegration.productinformation.ProductInformationApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

@RunWith(SpringRunner.class)
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ProductInformationApplication.class)
@TestPropertySource(value= "classpath:/config/application-test.properties")
public abstract class AbstractIntegrationTest {

    @Inject protected TestRestTemplate restTemplate;

}
