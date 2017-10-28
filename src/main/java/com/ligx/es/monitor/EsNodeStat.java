package com.ligx.es.monitor;

import com.ligx.http.HttpAgent;

import java.io.IOException;

/**
 * Author: ligongxing.
 * Date: 2017年10月27日.
 */
public class EsNodeStat {

    // -------------------节点统计API
    /**
     * 获取所有节点的统计信息
     * http://<node-ip>:8419/_nodes/stats/indices,os,jvm,transport,http,fs
     *
     * @param ip
     * @param port
     */
    public static String getAllNodesStatInfo(String ip, String port) {
        try {
            String url = String.format("http://%s:%s/_nodes/stats/indices,os,jvm,transport,http,fs?pretty", ip, port);
            return HttpAgent.create().doGet(url);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取指定节点的统计信息
     * http://<node-ip>:8419/_nodes/<node-ip>/stats/indices,os,jvm,transport,http,fs
     *
     * @param ip
     * @param port
     * @param node
     */
    public static String getSpecifyNodeStatInfo(String ip, String port, String node) {
        try {
            String url = String.format("http://%s:%s/_nodes/%s/stats/indices,os,jvm,transport,http,fs?pretty", ip, port, node);
            return HttpAgent.create().doGet(url);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }


    // -------------------节点信息API

    /**
     * 获取指定节点的信息
     * http://<node-ip>:8419/_nodes/<node-ip>/os,jvm,network,http,plugins,settings
     *
     * @param ip
     * @param port
     * @param node
     */
    public static String getNodeInfo(String ip, String port, String node) {
        try {
            String url = String.format("http://%s:%s/_nodes/%s/os,jvm,network,http,plugins,settings?pretty", ip, port, node);
            return HttpAgent.create().doGet(url);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
