package com.ligx.utils;

import org.junit.Test;

/**
 * Author: ligongxing.
 * Date: 2018年08月19日.
 */
public class NetUtilTest {

    @Test
    public void getHostAddress() {
        System.out.println(NetUtil.getIp());
    }

    @Test
    public void ipToLong() {
        System.out.println(NetUtil.ipToLong("192.168.0.255") & 0x3FFL);
        System.out.println(NetUtil.ipToLong("192.168.0.102") & 0x3FFL);
        System.out.println(NetUtil.ipToLong("192.168.0.103") & 0x3FFL);
    }
}