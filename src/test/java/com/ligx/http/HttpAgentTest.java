package com.ligx.http;

import org.junit.Test;

import static org.junit.Assert.*;

public class HttpAgentTest {

    @Test
    public void doGet() throws Exception {
        HttpAgent agent = HttpAgent.create();
        String result = agent.doGet("http://www.baidu.com");
        System.out.println(result);
    }
}