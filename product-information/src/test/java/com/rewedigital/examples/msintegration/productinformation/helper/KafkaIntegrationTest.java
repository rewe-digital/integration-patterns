package com.rewedigital.examples.msintegration.productinformation.helper;

import org.junit.ClassRule;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(initializers = KafkaContextInitializer.class)
public abstract class KafkaIntegrationTest extends AbstractIntegrationTest {

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, false, "products");
}

class KafkaContextInitializer implements
        ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext ac) {
        System.setProperty("eventing.brokers",
                KafkaIntegrationTest.embeddedKafka.getEmbeddedKafka().getBrokersAsString());
    }
}
