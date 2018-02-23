package com.rewedigital.examples.msintegration.productinformation.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.Objects;

public class FreePortFinder {

    private static final Logger LOG = LoggerFactory.getLogger(FreePortFinder.class);

    private FreePortFinder() {
        // Util class
    }

    /**
     * Get the next free port from {@code portRange}
     *
     * @return The port as {@link Integer}.
     */
    public static Integer getFreePort(PortRange portRange) {
        LOG.debug("Searching for free port ..." + portRange);
        for (Integer port = portRange.getFrom(); port <= portRange.getTo(); port++) {
            ServerSocket ss = null;
            DatagramSocket ds = null;
            try {
                ss = new ServerSocket(port);
                ss.setReuseAddress(true);
                ds = new DatagramSocket(port);
                ds.setReuseAddress(true);
                LOG.debug("... found port " +  port);
                return port;
            } catch (Exception ignored) {
                // port already in use
            } finally {
                if (ds != null) {
                    ds.close();
                }

                if (ss != null) {
                    try {
                        ss.close();
                    } catch (IOException e) {
                        // should not be thrown
                    }

                }

            }

        }
        throw new RuntimeException("No free port available in given Port range: " + portRange);
    }

    public static Integer getFreePort(Integer from, Integer to) {
        return getFreePort(new PortRange(from, to));
    }

    public static PortRange portRange(Integer from, Integer to) {
        PortRange range = new PortRange(from, to);
        return range;
    }

    public static class PortRange  {

        public String toString() {
            return ((String) ("from " + from + " to " + to));
        }

        public Integer getFrom() {
            return from;
        }

        public void setFrom(Integer from) {
            this.from = Objects.requireNonNull(from);
        }

        public Integer getTo() {
            return to;
        }

        public void setTo(Integer to) {
            this.to = Objects.requireNonNull(to);
        }

        public PortRange(Integer from, Integer to) {
            this.from = Objects.requireNonNull(from);
            this.to = Objects.requireNonNull(to);
        }

        private Integer from;
        private Integer to;
    }
}
