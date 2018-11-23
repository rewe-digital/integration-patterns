package com.rewedigital.examples.msintegration.productinformation.helper;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;

public abstract class KafkaIntegrationTest extends AbstractIntegrationTest{

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, false, "products");

    @BeforeClass
    public static void setup() {
        System.setProperty("eventing.brokers",
                embeddedKafka.getEmbeddedKafka().getBrokersAsString());
    }
}
