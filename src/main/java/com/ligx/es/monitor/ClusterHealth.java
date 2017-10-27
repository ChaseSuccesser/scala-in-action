package com.ligx.es.monitor;

/**
 * Author: ligongxing.
 * Date: 2017年10月27日.
 */
public class ClusterHealth {

    /**
     * 查询集群的健康信息
     */
    public void getClusterHealthInfo() {
        // TODO http://<node-ip>:8419/_cluster/health
    }

    /**
     * 使用level参数查询集群健康的细节信息
     * level参数值可以指定为:cluster(默认), indices, shards
     */
    public void getClusterHealthInfoWithLevel() {
        // TODO http://<node-ip>:8419/_cluster/health?level=indices
    }

    /**
     * 查询索引的健康信息
     *
     * @param indices 多个索引
     */
    public void getIndexHealthInfo(String... indices) {
        // TODO http://<node-ip>:8419/_cluster/health/<index...>
    }


    class HealthData {
        /**
         * 集群名称
         */
        private String cluster_name;

        /**
         * green:所有的主分片和副本分片都已分配。你的集群是 100% 可用的。
         * yellow:所有的主分片已经分片了，但至少还有一个副本是缺失的。不会有数据丢失，所以搜索结果依然是完整的。
         * red:至少一个主分片（以及它的全部副本）都在缺失中。这意味着你在缺少数据
         */
        private String status;

        private String timed_out;

        private int number_of_nodes;
        private int number_of_data_nodes;

        // 下面两个字段是查询索引健康信息时返回的
        private int number_of_shards;
        private int number_of_replicas;

        /**
         * 主分片数量
         */
        private int active_primary_shards;

        /**
         * 所有索引的所有分片的汇总值，即包括副本分片
         */
        private int active_shards;

        /**
         * 当前正在从一个节点迁往其他节点的分片的数量
         * 通常来说应该是 0，不过在 Elasticsearch 发现集群不太均衡时，该值会上涨。
         */
        private int relocating_shards;

        /**
         * 刚刚创建的分片的个数
         */
        private int initializing_shards;

        /**
         * 已经在集群状态中存在的分片，但是实际在集群里又找不着.
         * 如果你的集群是 red 状态，也会长期保有未分配分片（因为缺少主分片）
         */
        private int unassigned_shards;
    }
}
