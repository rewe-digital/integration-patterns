package com.rewedigital.examples.msintegration.productinformation.helper.kafka;

import com.rewedigital.examples.msintegration.productinformation.helper.FreePortFinder;
import kafka.admin.AdminUtils;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class KafkaServer {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaServer.class);
    private static KafkaLocal kafkaLocal;
    private static ZooKeeperLocal zookeeper;
    private static Integer kafkaPort = FreePortFinder.getFreePort(FreePortFinder.portRange(32000,32100));
    private static Integer zookeeperPort = FreePortFinder.getFreePort(FreePortFinder.portRange(32101,32200));

    /**
     * Starts a local Kafka Server (inlcudes zookeper and kafka) , if it has not been started already.
     *
     * @return The port of the zookeeper server.
     */
    public static Integer startKafkaServer(String topicName) {
        if (null != kafkaLocal) {
            kafkaLocal = provideKafkaServer();
            createTopicWithThreePartitions(topicName);
        }

        return zookeeperPort;
    }

    /**
     * Shuts down the Kafka Server (includes zookeeper and kafka).
     * <p>
     * HINT: currently not used because the resource is freed after the end of all tests.
     */
    public static void stopKafkaServer() {
        kafkaLocal.stop();
        zookeeper.shutdown();
    }

    public static KafkaLocal provideKafkaServer() {
        Properties kafkaProperties = new Properties();
        kafkaProperties.put("zookeeper.connect", (String) "localhost:" + String.valueOf(zookeeperPort));
        kafkaProperties.put("port", (String) String.valueOf(kafkaPort));
        kafkaProperties.put("broker.id", (String) "0");
        kafkaProperties.put("host.name", (String) "localhost");
        kafkaProperties.put("num.partitions", (String) "1");
        kafkaProperties.put("default.replication.factor", (String) "1");
        kafkaProperties.put("zookeeper.connection.timeout.ms", (String) "10000");
        kafkaProperties.put("log.dirs", (String) "./target/data/kafka");
        kafkaProperties.put("auto.create.topics.enable", (String) "true");
        kafkaProperties.put("auto.commit.enable", (String) "false");

        try {
            FileUtils.deleteDirectory(new File((String) kafkaProperties.get("log.dirs")));

            ZooKeeperLocal zooKeeperLocal = provideZooKeeperLocal();
            KafkaLocal kafka = new KafkaLocal(kafkaProperties, zooKeeperLocal);
            kafka.start();
            Thread.sleep(2000);
            return ((KafkaLocal) (kafka));

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("KafkaServer could not start: " + e.getMessage(), e);
        }
    }

    public static ZooKeeperLocal provideZooKeeperLocal() {
        Properties zkProperties = new Properties();
        zkProperties.put("clientPort", (String) String.valueOf(zookeeperPort));
        zkProperties.put("maxClientCnxns", (String) "10");
        zkProperties.put("dataDir", (String) "./target/data/zookeeper");

        try {
            FileUtils.deleteDirectory(new File((String) zkProperties.get("dataDir")));

            zookeeper = new ZooKeeperLocal(zkProperties);
            zookeeper.start();
            return zookeeper;

        } catch (IOException e) {
            throw new RuntimeException("Zookeeper Server could not start: " + e.getMessage(), e);
        }
    }

    public static void createTopicWithThreePartitions(String topicName) {
        Integer sessionTimeoutMs = 10000;
        Integer connectionTimeoutMs = 10000;
        ZkConnection zkConnection = new ZkConnection("localhost:" + String.valueOf(zookeeperPort), sessionTimeoutMs);
        ZkClient zkClient = new ZkClient(zkConnection, connectionTimeoutMs, ZKStringSerializer$.MODULE$);
        ZkUtils zkUtils = new ZkUtils(zkClient, zkConnection, false);

        Integer numPartitions = 3;
        Integer replicationFactor = 1;
        Properties topicConfig = new Properties();

        LOG.info("Deleting topic " + topicName);
        AdminUtils.deleteTopic(zkUtils, topicName);
        LOG.info("Creating topic " + topicName);
        AdminUtils.createTopic(zkUtils, topicName, numPartitions, replicationFactor, topicConfig, null);

        zkClient.close();
    }

    public static KafkaLocal getKafkaLocal() {
        return kafkaLocal;
    }

    public static void setKafkaLocal(KafkaLocal kafkaLocal) {
        KafkaServer.kafkaLocal = kafkaLocal;
    }

    public static ZooKeeperLocal getZookeeper() {
        return zookeeper;
    }

    public static void setZookeeper(ZooKeeperLocal zookeeper) {
        KafkaServer.zookeeper = zookeeper;
    }

    public static Integer getKafkaPort() {
        return kafkaPort;
    }

    public static void setKafkaPort(Integer kafkaPort) {
        KafkaServer.kafkaPort = kafkaPort;
    }

    public static Integer getZookeeperPort() {
        return zookeeperPort;
    }

    public static void setZookeeperPort(Integer zookeeperPort) {
        KafkaServer.zookeeperPort = zookeeperPort;
    }

}
