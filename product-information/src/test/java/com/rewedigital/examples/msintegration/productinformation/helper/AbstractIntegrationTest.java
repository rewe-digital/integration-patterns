package com.rewedigital.examples.msintegration.productinformation.helper;

import com.rewedigital.examples.msintegration.productinformation.ProductInformationApplication;
import com.rewedigital.examples.msintegration.productinformation.helper.kafka.KafkaServer;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

@RunWith(SpringRunner.class)
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ProductInformationApplication.class)
@TestPropertySource(value="classpath:/config/application-test.yml")
public abstract class AbstractIntegrationTest {

    @Inject protected TestRestTemplate restTemplate;

    @Value("${local.server.port}") protected int port;
    @Value("${productqueue.brokers}") private String bokers;

    @BeforeClass
    public static void initTest() {
        // TODO: Create Topics configured in application.yml and override the broker port there.
        // startKafkaServer returns the broker port
        KafkaServer.startKafkaServer("topic");
    }


}
