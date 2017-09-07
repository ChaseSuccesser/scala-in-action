package com.ligx.es;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

/**
 * Author: ligongxing.
 * Date: 2017年05月28日.
 */
public class EsClient {

    private static final Logger logger = LoggerFactory.getLogger("es");

    private static volatile TransportClient client;

    /**
     * 获取ES客户端
     * @return
     */
    public static TransportClient getClient() {
        if (client == null) {
            synchronized (EsClient.class) {
                try {
                    if (client == null) {
                        Settings settings = Settings.builder()
                                .put("client.transport.sniff", true)
                                .put("cluster.name", "iflight-logcenter")
                                .build();

                        client = new PreBuiltTransportClient(settings)
                                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.4.202.82"), 8418))
                                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.4.84.155"), 8418))
                                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.4.88.68"), 8418));
                    }
                } catch (UnknownHostException e) {
                    logger.error("Fail to initialized es client.", e);
                    throw new RuntimeException("Fail to initialized es client");
                }
            }
        }
        return client;
    }


    /**
     * 创建索引
     *
     * @param index    索引名
     * @param shards   分片数
     * @param replicas 副本数
     * @return
     */
    public static boolean createIndex(String index, int shards, int replicas) {
        TransportClient client = getClient();

        IndicesAdminClient adminClient = client.admin().indices();

        CreateIndexResponse response = adminClient.prepareCreate(index)
                .setSettings(Settings.builder()
                        .put("index.number_of_shards", shards)
                        .put("index.number_of_replicas", replicas))
                .get();
        return response.isShardsAcked();
    }

    /**
     * 为指定type创建mapping
     *
     * @param index   suoyinming
     * @param type    类型名
     * @param mapping 映射内容
     * @return
     */
    public static boolean addMapping(String index, String type, String mapping) {
        TransportClient client = getClient();

        IndicesAdminClient adminClient = client.admin().indices();

        CreateIndexResponse response = adminClient.prepareCreate(index).addMapping(type, mapping).get();
        return response.isAcknowledged();
    }

    /**
     * 添加文档
     *
     * @param index 索引名
     * @param type  类型名
     * @param doc   文档对象
     * @param <T>
     * @return
     */
    public static <T> long addDoc(String index, String type, T doc) {
        TransportClient client = getClient();

        long id = 0L; // TODO 需要用一个id生成器生成id
        IndexResponse response = client.prepareIndex(index, type, "" + id)
                .setSource(JSON.toJSONString(doc))
                .get();
        if (response.getResult() == DocWriteResponse.Result.CREATED) {
            return id;
        } else {
            logger.error("Fail to add doc. id={}, response={}, doc={}", id, response, doc);
            return -1;
        }
    }

    /**
     * 更新文档
     *
     * @param index
     * @param type
     * @param id
     * @param doc
     * @param <T>
     * @return
     */
    public static <T> boolean updateDoc(String index, String type, String id, T doc) {
        TransportClient client = getClient();

        UpdateResponse updateResponse = client.prepareUpdate(index, type, id)
                .setDoc(JSON.toJSONString(doc))
                .get();

        if(updateResponse.status() == RestStatus.OK){
            return true;
        } else {
            logger.error("Fail to update doc. index={}, type={}, id={}, UpdateResponse={}",
                    index, type, id, updateResponse);
            return false;
        }
    }

    /**
     * 查询文档
     *
     * @param index 索引
     * @param type  类型名
     * @param id    文档标识符
     * @param clazz 结果类型
     * @param <T>
     * @return
     */
    public static <T> T getDoc(String index, String type, String id, Class<T> clazz) {
        TransportClient client = getClient();

        GetResponse response = client.prepareGet(index, type, id).get();

        if (response.isExists()) {
            String source = response.getSourceAsString();
            return JSON.parseObject(source, clazz);
        } else {
            logger.error("Fail to get doc. index={}, type={}, id={}, response={}", index, type, id, response);
            return null;
        }
    }

    /**
     * 删除文档
     *
     * @param index 索引名
     * @param type  类型名
     * @param id    文档标识符
     * @return
     */
    public static boolean delDoc(String index, String type, String id) {
        TransportClient client = getClient();

        DeleteResponse response = client.prepareDelete(index, type, id).get();

        if (response.getResult() == DocWriteResponse.Result.CREATED) {
            return true;
        } else {
            logger.error("Fail to delete doc. index={}, type={}, id={}, response={}",
                    index, type, id, response);
            return false;
        }
    }

    /**
     * 批量添加文档
     *
     * @param index   索引名
     * @param type    类型名
     * @param docList 文档列表
     * @param <T>
     * @return
     */
    public static <T> int batchAddDoc(String index, String type, List<T> docList) {
        TransportClient client = getClient();

        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for (T doc : docList) {
            bulkRequest.add(client.prepareIndex(index, type, "" + 0L)  // TODO 需要用一个id生成器生成id
                    .setSource(JSON.toJSONString(doc)));
        }

        int total = docList.size();
        int failSize = 0;
        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            logger.error("Fail to bulk add doc, message: {}", bulkResponse.buildFailureMessage());
            // process failures by iterating through each bulk response item
            Iterator<BulkItemResponse> it = bulkResponse.iterator();
            while (it.hasNext()) {
                BulkItemResponse resp = it.next();
                if (resp.isFailed()) {
                    failSize++;
                    logger.error("Fail to bulk add doc, response is {}", resp.getResponse());
                }
            }
        }
        logger.info("batch add doc, total {}, fail {}, took {} ms", total, failSize, bulkResponse.getTookInMillis());
        return total - failSize;
    }
}
