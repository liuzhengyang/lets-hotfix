package com.github.lzy.hotfix.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liuzhengyang
 */
public final class HostUtils {
    private static final Logger logger = LoggerFactory.getLogger(HostUtils.class);

    private HostUtils() {
    }

    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logger.error("Failed to get host name", e);
            return "";
        }
    }
}
