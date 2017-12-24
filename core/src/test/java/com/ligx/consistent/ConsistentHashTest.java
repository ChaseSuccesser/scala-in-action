package com.ligx.consistent;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Author: ligongxing.
 * Date: 2017年08月24日.
 */
public class ConsistentHashTest {


    @Test
    public void consistentTest(){
        List<ServerNode> serverNodes = new ArrayList<ServerNode>() {{
            add(new ServerNode("127.0.0.1", "dev", "8080"));
            add(new ServerNode("127.0.0.2", "test", "8080"));
            add(new ServerNode("127.0.0.3", "staging", "8080"));
            add(new ServerNode("127.0.0.4", "prod", "8080"));
        }};

        ConsistentHash consistentHash = new ConsistentHash(new HashImpl(), 2, serverNodes);

        System.out.println("----- 原始情况 ----");
        consistentHash.getServers().entrySet()
                .stream()
                .forEach(entry -> System.out.println(entry.getValue().getIp()));
        String key = "aaa";
        String ip = consistentHash.get(key).getIp();
        System.out.println(String.format("对象:%s ip:%s", key, ip));

        System.out.println("----- 测试删除机器节点的情况 ----");
        String deletedIp = "127.0.0.4";
        consistentHash.delete(new ServerNode(deletedIp, "dev", "8080"));
        System.out.println(String.format("删除节点:%s", deletedIp));
        consistentHash.getServers().entrySet()
                .stream()
                .forEach(entry -> System.out.println(entry.getValue().getIp()));
        ip = consistentHash.get(key).getIp();
        System.out.println(String.format("对象:%s ip:%s", key, ip));

        System.out.println("----- 测试添加机器节点的情况 ----");
        String addedIp = "127.0.0.5";
        consistentHash.add(new ServerNode(addedIp, "dev", "8080"));
        System.out.println(String.format("添加节点:%s", addedIp));
        consistentHash.getServers().entrySet()
                .stream()
                .forEach(entry -> System.out.println(entry.getValue().getIp()));
        ip = consistentHash.get(key).getIp();
        System.out.println(String.format("对象:%s ip:%s", key, ip));
    }
}
