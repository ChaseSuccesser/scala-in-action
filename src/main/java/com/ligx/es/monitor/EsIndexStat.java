package com.ligx.es.monitor;

import com.ligx.http.HttpAgent;

import java.io.IOException;

/**
 * Author: ligongxing.
 * Date: 2017年10月27日.
 */
public class EsIndexStat {

    /**
     * 获取指定索引的统计信息
     * http://<node-ip>:8419/<index>/_stats
     *
     * @param ip    es节点ip
     * @param port  es http port
     * @param index 索引名
     * @return
     */
    public static String getSpecifyIndexStatInfo(String ip, String port, String index) {
        try {
            String url = String.format("http://%s:%s/%s/_stats?pretty", ip, port, index);
            return HttpAgent.create().doGet(url);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
