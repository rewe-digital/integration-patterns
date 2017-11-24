package com.rewedigital.examples.msintegration.productinformation.helper.kafka;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServerStartable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Properties;

class KafkaLocal  {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaLocal.class);

    private KafkaServerStartable kafka;
    private Properties kafkaProperties;
    private ZooKeeperLocal zooKeeperLocal;

    public KafkaLocal(Properties kafkaProperties, ZooKeeperLocal zooKeeperLocal) {
        this.zooKeeperLocal = Objects.requireNonNull(zooKeeperLocal);
        this.kafkaProperties = Objects.requireNonNull(kafkaProperties);
    }

    void start() {
        KafkaConfig kafkaConfig = new KafkaConfig(kafkaProperties);

        kafka = new KafkaServerStartable(kafkaConfig);
        LOG.info("Starting local kafka broker...");
        kafka.startup();
        LOG.info("done");

        Field serverField = ReflectionUtils.findField(KafkaServerStartable.class, "server");
        serverField.setAccessible(true);
        kafka.server.KafkaServer kafkaServer = (kafka.server.KafkaServer) ReflectionUtils.getField(serverField, kafka);


        while (kafkaServer.brokerState().currentState() !=  3) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
                // ignored
            }
        }
    }

    Object stop() {
        LOG.info("stopping kafka...");
        kafka.shutdown();
        LOG.info("done");
        return null;
    }

}
