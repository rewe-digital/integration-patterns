package com.rewedigital.examples.msintegration.productdetailpage.helper.kafka;

import org.apache.zookeeper.server.ServerCnxnFactory;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

class ZooKeeperLocal {

    private static final Logger LOG = LoggerFactory.getLogger(ZooKeeperLocal.class);

    private ZooKeeperServerMain zooKeeperServer;
    private Properties zkProperties;

    ZooKeeperLocal(Properties zkProperties) {
        this.zkProperties = zkProperties;
    }

    void start() {
        final ServerConfig configuration = getConfiguration();
        zooKeeperServer = new ZooKeeperServerMain();

        Thread startThread = new Thread() {
            @Override
            public void run() {
                try {
                    zooKeeperServer.runFromConfig(configuration);
                } catch (IOException e) {
                    LOG.debug("ZooKeeper Failed", e);
                }

            }

        };

        startThread.start();

        boolean isRunning = false;
        while(!isRunning) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
                // ignored
            }
            ZooKeeperServer server = getZookeperServer(getServerCnxnFactory());
            if (null != server) {
                isRunning = server.isRunning();
            }
        }
    }

    public void shutdown() {
        getServerCnxnFactory().shutdown();
    }

    private ServerCnxnFactory getServerCnxnFactory() {
        Field cnxnFactoryField = ReflectionUtils.findField(ZooKeeperServerMain.class, "cnxnFactory");
        cnxnFactoryField.setAccessible(true);
        return (ServerCnxnFactory) ReflectionUtils.getField(cnxnFactoryField, zooKeeperServer);
    }

    private ZooKeeperServer getZookeperServer(ServerCnxnFactory cnxnFactory) {
        if (null == cnxnFactory) {
            return null;
        }
        Field zkServerField = ReflectionUtils.findField(ServerCnxnFactory.class, "zkServer");
        zkServerField.setAccessible(true);
        return (ZooKeeperServer) ReflectionUtils.getField(zkServerField, cnxnFactory);
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

}
