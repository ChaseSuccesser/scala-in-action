package com.ligx.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Author: ligongxing.
 * Date: 2018年08月19日.
 */
public class NetUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetUtil.class);

    public static String getIp() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            return address.getHostAddress();
        } catch (UnknownHostException e) {
            LOGGER.error("NetUtil#getIp, ", e);
            return "";
        }
    }

    public static long ipToLong(String strIp) {
        if (StringUtils.isBlank(strIp)) {
            return 0;
        }
        String[] ipArr = strIp.split("\\.");
        if (ipArr.length != 4) {
            return 0L;
        }
        long[] ip = new long[4];
        ip[0] = Long.parseLong(ipArr[0]);
        ip[1] = Long.parseLong(ipArr[1]);
        ip[2] = Long.parseLong(ipArr[2]);
        ip[3] = Long.parseLong(ipArr[3]);
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
    }

}