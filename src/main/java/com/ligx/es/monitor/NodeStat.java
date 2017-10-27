package com.ligx.es.monitor;

/**
 * Author: ligongxing.
 * Date: 2017年10月27日.
 */
public class NodeStat {

    // -------------------节点统计API
    /**
     * 获取所有节点的统计信息
     */
    public void getAllNodesStatInfo() {
        // TODO http://<node-ip>:8419/_nodes/stats/indices,os,jvm,transport,http,fs
    }

    /**
     * 获取指定节点的统计信息
     */
    public void getSpecifyNodeStatInfo(String node) {
        // TODO http://<node-ip>:8419/_nodes/<node-ip>/stats/indices,os,jvm,transport,http,fs
    }


    // -------------------节点信息API
    public void getNodeInfo(String node) {
        // TODO http://<node-ip>:8419/_nodes/<node-ip>/os,jvm,network,http,plugins,settings
    }
}
