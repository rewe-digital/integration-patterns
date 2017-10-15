package com.rewedigital.examples.msintegration.simplekafka;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServerStartable;

// FIXME TV: describe source and purpose of this hacky stuff :-)

/**
 * Created by arunmohzi on 7/13/17.
 */
public class KafkaStarter {
    
    // FIXME TV useful tmp dir usage
    public static final String DEFAULT_KAFKA_LOG_DIR = "/tmp/test/kafka_embedded";
    public static final int BROKER_ID = 0;
    public static final int BROKER_PORT = 5000;
    public static final String LOCALHOST_BROKER = String.format("localhost:%d", BROKER_PORT);

    public static final String DEFAULT_ZOOKEEPER_LOG_DIR = "/tmp/test/zookeeper";
    public static final int ZOOKEEPER_PORT = 2000;
    public static final String ZOOKEEPER_HOST = String.format("localhost:%d", ZOOKEEPER_PORT);

    public static class ZooKeeperLocal {

        ZooKeeperServerMain zooKeeperServer;

        public ZooKeeperLocal(final Properties zkProperties) throws FileNotFoundException, IOException {
            final QuorumPeerConfig quorumConfiguration = new QuorumPeerConfig();
            try {
                quorumConfiguration.parseProperties(zkProperties);
            } catch (final Throwable throwable) {
                throw new RuntimeException(throwable);
            }

            zooKeeperServer = new ZooKeeperServerMain();
            final ServerConfig configuration = new ServerConfig();
            configuration.readFrom(quorumConfiguration);


            new Thread() {
                @Override
                public void run() {
                    try {
                        zooKeeperServer.runFromConfig(configuration);
                    } catch (final IOException e) {
                        System.out.println("ZooKeeper Failed");
                        e.printStackTrace(System.err);
                    }
                }
            }.start();
        }
    }

    public KafkaServerStartable kafka;
    public ZooKeeperLocal zookeeper;
    private static final String KAFKA_LOG_DIR_CONF = "log.dir";
    private static final String ZOOKEEPER_DIR_CONF = "dataDir";

    public static void main(final String[] args) throws Exception {
        start();
    }


    public static void start() throws Exception {
        // load properties
        final Properties kafkaProperties = getKafkaProperties(DEFAULT_KAFKA_LOG_DIR, BROKER_PORT, BROKER_ID);
        final Properties zkProperties = getZookeeperProperties(ZOOKEEPER_PORT, DEFAULT_ZOOKEEPER_LOG_DIR);

        // start kafkaLocalServer
        new KafkaStarter(kafkaProperties, zkProperties);
    }

    public KafkaStarter(final Properties kafkaProperties, final Properties zkProperties)
        throws IOException, InterruptedException {


        final String kafkaLogPath = kafkaProperties.getProperty(KAFKA_LOG_DIR_CONF);
        if (kafkaLogPath == null || kafkaLogPath.isEmpty())
            throw new IllegalArgumentException("Kafka Log directory is not properly set");

        final String zookeeperPath = zkProperties.getProperty(ZOOKEEPER_DIR_CONF);
        if (zookeeperPath == null || zookeeperPath.isEmpty())
            throw new IllegalArgumentException("Zookeeper path is not properly set");


        final File kafkaLogDir = new File(kafkaLogPath);
        if (kafkaLogDir.exists()) {
            deleteFolder(kafkaLogDir);
        }

        final File zookeeperDir = new File(zookeeperPath);
        if (zookeeperDir.exists()) {
            deleteFolder(zookeeperDir);
        }

        final String kafka_log_dir = kafkaProperties.getProperty(KAFKA_LOG_DIR_CONF);
        if (kafka_log_dir == null || kafka_log_dir.isEmpty())
            throw new IllegalArgumentException("Kafka Log directory is not properly set");

        final File kafkaDir = new File(kafka_log_dir);
        if (kafkaDir.exists()) {
            deleteFolder(kafkaDir);
        }


        final KafkaConfig kafkaConfig = new KafkaConfig(kafkaProperties);

        // start local zookeeper
        System.out.println("starting local zookeeper...");
        zookeeper = new ZooKeeperLocal(zkProperties);
        System.out.println("done");

        // start local kafka broker
        kafka = new KafkaServerStartable(kafkaConfig);
        System.out.println("starting local kafka broker...");
        kafka.startup();
        System.out.println("done");
    }


    public void stop() {
        // stop kafka broker
        System.out.println("stopping kafka...");
        kafka.shutdown();
        System.out.println("done");
    }

    public static void deleteFolder(final File folder) {
        final File[] files = folder.listFiles();
        if (files != null) { // some JVMs return null for empty dirs
            for (final File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    private static Properties getKafkaProperties(final String logDir, final int port, final int brokerId) {
        final Properties properties = new Properties();
        properties.put("port", port + "");
        properties.put("broker.id", brokerId + "");
        properties.put("log.dir", logDir);
        properties.put("zookeeper.connect", ZOOKEEPER_HOST);
        properties.put("default.replication.factor", "1");
        properties.put("delete.topic.enable", "true");
        properties.put("offsets.topic.replication.factor","1");
        return properties;
    }

    private static Properties getZookeeperProperties(final int port, final String zookeeperDir) {
        final Properties properties = new Properties();
        properties.put("clientPort", port + "");
        properties.put("dataDir", zookeeperDir);
        return properties;
    }
}
