package com.rewedigital.examples.msintegration.productinformation.helper.kafka;

import org.apache.zookeeper.server.ServerCnxnFactory;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

public class ZooKeeperLocal {

    private static final Logger LOG = LoggerFactory.getLogger(ZooKeeperLocal.class);

    private ZooKeeperServerMain zooKeeperServer;
    private Properties zkProperties;

    public ZooKeeperLocal(Properties zkProperties) throws FileNotFoundException, IOException {
        this.zkProperties = zkProperties;
    }

    public void start() {
        final ServerConfig configuration = getConfiguration();
        zooKeeperServer = new ZooKeeperServerMain();

        Thread startThread = new Thread() {
            @Override
            public void run() {
                try {
                    getZooKeeperServer().runFromConfig(configuration);
                } catch (IOException e) {
                    LOG.debug("ZooKeeper Failed", e);
                }

            }

        };

        startThread.start();



        // TODO: wait

        /*
        final ZooKeeperServerMain server1 = zooKeeperServer;
        final ServerCnxnFactory factory = (server1 == null ? null : server1.cnxnFactory);
        final ZooKeeperServer server = (factory == null ? null : factory.zkServer);
        while (!(server == null ? null : server.isRunning())) {
            Thread.sleep(500);
        }
        */

    }

    public void shutdown() {
        Field cnxnFactoryField = ReflectionUtils.findField(ZooKeeperServerMain.class, "cnxnFactory");
        ServerCnxnFactory cnxnFactory = (ServerCnxnFactory) ReflectionUtils.getField(cnxnFactoryField, zooKeeperServer);

        cnxnFactory.shutdown();
    }

    private ServerConfig getConfiguration() {
        QuorumPeerConfig quorumConfiguration = new QuorumPeerConfig();
        try {
            quorumConfiguration.parseProperties(zkProperties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        final ServerConfig configuration = new ServerConfig();
        configuration.readFrom(quorumConfiguration);
        return configuration;
    }

    public ZooKeeperServerMain getZooKeeperServer() {
        return zooKeeperServer;
    }

    public void setZooKeeperServer(ZooKeeperServerMain zooKeeperServer) {
        this.zooKeeperServer = zooKeeperServer;
    }


}
