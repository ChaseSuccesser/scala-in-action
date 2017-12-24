package com.ligx.consistent;

/**
 * Author: ligongxing.
 * Date: 2017年08月24日.
 */
public class ServerNode {

    private String ip;

    private String name;

    private String port;

    public ServerNode() {
    }

    public ServerNode(String ip, String name, String port) {
        this.ip = ip;
        this.name = name;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
