package com.ligx.es.monitor;

import org.junit.Test;

public class EsClusterHealthTest {

    /*
    查询集群的健康信息
     */
    @Test
    public void getClusterHealthInfo() throws Exception {
        String response = EsClusterHealth.getClusterHealthInfo("10.4.88.68", "8419");
        System.out.println(response);
    }

    /*
    使用level参数查询集群健康的细节信息
     */
    @Test
    public void getClusterHealthInfoWithLevel() throws Exception {
        String response = EsClusterHealth.getClusterHealthInfoWithLevel("10.4.88.68", "8419");
        System.out.println(response);
    }

    /*
    查询索引的健康信息
     */
    @Test
    public void getIndexHealthInfo() throws Exception {
        String response = EsClusterHealth.getIndexHealthInfo("10.4.88.68", "8419", "agent_log");
        System.out.println(response);
    }

}