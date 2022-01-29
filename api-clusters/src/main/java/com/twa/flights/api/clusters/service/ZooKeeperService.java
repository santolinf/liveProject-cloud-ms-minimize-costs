package com.twa.flights.api.clusters.service;

import com.twa.flights.api.clusters.configuration.ZooKeeperCuratorConfiguration;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class ZooKeeperService {

    private static final int MAX_CONNECTION_WAIT = 15_000;

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperService.class);

    private final ZooKeeperCuratorConfiguration zooKeeperConnection;

    public ZooKeeperService(ZooKeeperCuratorConfiguration zooKeeperConnection) {
        this.zooKeeperConnection = zooKeeperConnection;
    }

    public boolean checkIfBarrierExists(String barrierName) {
        boolean result = false;
        try {
            result = Objects.nonNull(zooKeeperConnection.getClient().checkExists().forPath(barrierName));
        } catch (Exception e) {
            LOGGER.error("Error checking if barrier ({}) exists: {}", barrierName, e.getMessage());
        }

        return result;
    }

    public boolean createBarrier(String barrierName) {
        try {
            getBarrier(barrierName).setBarrier();
        } catch (Exception e) {
            return true;
        }

        return false;
    }

    public void deleteBarrier(String barrierName) {
        if (!checkIfBarrierExists(barrierName)) {
            return;
        }

        try {
            zooKeeperConnection.getClient().delete().quietly().forPath(barrierName);
        } catch (Exception e) {
            LOGGER.error("Error deleting barrier ({}): {}", barrierName, e.getMessage());
        }
    }

    public void waitOnBarrier(String barrierName) {
        try {
            getBarrier(barrierName).waitOnBarrier(MAX_CONNECTION_WAIT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            LOGGER.error("Error waiting on barrier ({}): {}", barrierName, e.getMessage());
        }
    }

    public DistributedBarrier getBarrier(String path) {
        CuratorFramework client = zooKeeperConnection.getClient();
        return new DistributedBarrier(client, path) {
            @Override
            public synchronized void setBarrier() throws Exception {
                try {
                    client.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
                } catch (KeeperException.NodeExistsException nodeExistsException) {
                    LOGGER.debug("I'm {}: Node exists exception for path {}", getHostName(), path);
                    throw nodeExistsException;
                }
            }
        };
    }

    private String getHostName() {
        String hostName = "";

        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            LOGGER.error("There was an error to obtain the hostname");
        }

        return hostName;
    }
}
