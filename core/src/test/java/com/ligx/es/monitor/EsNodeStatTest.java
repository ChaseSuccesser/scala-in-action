package com.ligx.es.monitor;

import org.junit.Test;

public class EsNodeStatTest {

    @Test
    public void getAllNodesStatInfo() throws Exception {
        String response = EsNodeStat.getAllNodesStatInfo("10.4.88.68", "8419");
        System.out.println(response);
    }

    @Test
    public void getSpecifyNodeStatInfo() throws Exception {
        String response = EsNodeStat.getSpecifyNodeStatInfo("10.4.88.68", "8419", "10.4.84.155");
        System.out.println(response);
    }

    @Test
    public void getNodeInfo() throws Exception {
        String response = EsNodeStat.getNodeInfo("10.4.88.68", "8419", "10.4.84.155");
        System.out.println(response);
    }

}