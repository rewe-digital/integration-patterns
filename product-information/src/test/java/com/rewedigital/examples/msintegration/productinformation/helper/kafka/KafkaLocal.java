package com.rewedigital.examples.msintegration.productinformation.helper.kafka;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServerStartable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

public class KafkaLocal  {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaLocal.class);

    public KafkaLocal(Properties kafkaProperties, ZooKeeperLocal zooKeeperLocal) throws IOException, InterruptedException {
        this.zooKeeperLocal = zooKeeperLocal;
        this.kafkaProperties = kafkaProperties;
    }

    public void start() {
        KafkaConfig kafkaConfig = new KafkaConfig(kafkaProperties);

        kafka = new KafkaServerStartable(kafkaConfig);
        LOG.info("starting local kafka broker...");
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

        //}


    }

    public Object stop() {
        LOG.info("stopping kafka...");
        kafka.shutdown();
        LOG.info("done");
        return null;
    }

    public KafkaServerStartable getKafka() {
        return kafka;
    }

    public void setKafka(KafkaServerStartable kafka) {
        this.kafka = kafka;
    }

    public Properties getKafkaProperties() {
        return kafkaProperties;
    }

    public void setKafkaProperties(Properties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    public ZooKeeperLocal getZooKeeperLocal() {
        return zooKeeperLocal;
    }

    public void setZooKeeperLocal(ZooKeeperLocal zooKeeperLocal) {
        this.zooKeeperLocal = zooKeeperLocal;
    }

    private KafkaServerStartable kafka;
    private Properties kafkaProperties;
    private ZooKeeperLocal zooKeeperLocal;
}
