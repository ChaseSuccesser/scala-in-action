package com.ligx.consistent;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Author: ligongxing.
 * Date: 2017年08月24日.
 */
public class ConsistentHash {

    private IHash iHash;
    private int numOfReplica;
    private TreeMap<Long, ServerNode> circleMap = new TreeMap<>();

    /**
     * 构造函数
     *
     * @param iHash        接受不同的hash算法实现
     * @param numOfReplica 虚拟节点的个数
     * @param serverNodes  机器节点列表
     */
    public ConsistentHash(IHash iHash, int numOfReplica, List<ServerNode> serverNodes) {
        this.iHash = iHash;
        this.numOfReplica = numOfReplica;
        serverNodes.forEach(this::add);
    }

    /**
     * 添加机器节点
     *
     * @param serverNode 添加的机器节点
     */
    public void add(ServerNode serverNode) {
        for (int i = 0; i < numOfReplica; i++) {
            circleMap.put(iHash.hash(serverNode.getIp() + i), serverNode);
        }
    }

    /**
     * 删除机器节点
     *
     * @param serverNode 想要删除的机器节点
     */
    public void delete(ServerNode serverNode) {
        for (int i = 0; i < numOfReplica; i++) {
            circleMap.remove(iHash.hash(serverNode.getIp() + i));
        }
    }

    /**
     * 根据对象的key获取对应的机器节点
     *
     * @param key 对象的key
     * @return 机器节点
     */
    public ServerNode get(String key) {
        if (circleMap.isEmpty()) {
            return null;
        }

        long hash = iHash.hash(key);

        if (!circleMap.containsKey(hash)) {
            // 从对象的key出发，顺时针方向找到一个机器节点；若没有找到，则返回hash空间的第一个节点
            SortedMap<Long, ServerNode> tailMap = circleMap.tailMap(hash);
            return tailMap.isEmpty() ? circleMap.firstEntry().getValue() : tailMap.get(tailMap.firstKey());
        }
        return circleMap.get(hash);
    }

    /**
     * 返回hash空间中的机器列表
     *
     * @return
     */
    public Map<Long, ServerNode> getServers() {
        return circleMap;
    }
}
