package com.rewedigital.examples.msintegration.productinformation.helper.kafka;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServerStartable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
        //log.invokeMethod("info", new Object[]{"starting local kafka broker..."});
        kafka.startup();
        //log.invokeMethod("info", new Object[]{"done"});

        // TODO: wait
        /*
        while (!kafka.server.startupComplete) {
            Thread.sleep(500);
        }
        */

    }

    public Object stop() {
        //log.invokeMethod("info", new Object[]{"stopping kafka..."});
        kafka.shutdown();
        //return log.invokeMethod("info", new Object[]{"done"});
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
