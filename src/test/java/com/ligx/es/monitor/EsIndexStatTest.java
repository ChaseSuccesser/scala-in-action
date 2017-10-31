package com.ligx.es.monitor;

import org.junit.Test;

public class EsIndexStatTest {

    @Test
    public void getSpecifyIndexStatInfo() throws Exception {
        String response = EsIndexStat.getSpecifyIndexStatInfo("10.4.88.68", "8419", "agent_log");
        System.out.println(response);
    }

}