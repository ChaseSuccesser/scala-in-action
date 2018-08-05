package com.ligx.mongo;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Projections.*;

/**
 * Author: ligongxing.
 * Date: 2018/08/02.
 *
 * mongodb文档: http://mongodb.github.io/mongo-java-driver/3.8/driver/getting-started/quick-start/
 *
 * new Document(key1, value1).append(key2, value2) -> Document
 * Object -> json -> Document.parse(json) [即Bson]
 * Filters helper -> Bson
 *
 * https://github.com/jasonsuzhou/MongodbUtil/blob/d4548d95e33b6b10114702809198f367f27c2e3a/src/com/mh/mongo/core/MongoTemplate.java
 */
public class MongoUtil {

    /**
     *(since 3.7 release)
     * 一个MongoClient代表了一个连接到MongoDB的连接池，即使是在多线程中也仅仅只需要一个MongoClient实例。
     * 大多数情况下，应用程序中只需要创建一个MongoClient实例。
     */
    private static volatile MongoClient client;

    /**
     * MongoDatabase实例是immutable.
     *  If a database does not exist, MongoDB creates the database when you first store data for that database.
     */
    private static MongoDatabase database;

    /**
     * MongoClient实例是immutable.
     * If a collection does not exist, MongoDB creates the collection when you first store data for that collection
     */
    private static MongoCollection<Document> collection;

    private MongoUtil() {
    }

    private static void init(MongoConf mongoConf) {
        if (client == null) {
            synchronized (MongoUtil.class) {
                if (client == null) {
                    List<ServerAddress> serverAddressList = new ArrayList<>();
                    if (StringUtils.isNotBlank(mongoConf.getUrl())) {
                        List<String> hosts = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(mongoConf.getUrl());
                        for (String host : hosts) {
                            String[] split = host.split(":");
                            if (split.length == 2) {
                                serverAddressList.add(new ServerAddress(split[0], Integer.parseInt(split[1])));
                            }
                        }
                    }
                    MongoClientSettings.Builder b = MongoClientSettings.builder()
                            .applyToClusterSettings(builder -> builder.hosts(serverAddressList));

                    if (StringUtils.isNotBlank(mongoConf.getUsername())) {
                        MongoCredential credential = MongoCredential.createCredential(
                                mongoConf.getUsername(),
                                mongoConf.getAuthDatabase(),
                                mongoConf.getPassword().toCharArray());
                        b.credential(credential);
                    }

                    client = MongoClients.create(b.build());
                    database = client.getDatabase(mongoConf.getDatabase());
                }
            }
        }
    }


    // ------------------------------ collection -------------------------------
    /**
     * 获取给定集合
     *
     * @param collectionName
     * @return
     */
    public static MongoCollection<Document> collection(String collectionName) {
        // TODO
        MongoConf mongoConf = null;
        if (database == null) {
            init(mongoConf);
        }
        return database.getCollection(collectionName);
    }

    /**
     * 创建固定集合
     *
     * @param collectionName
     * @param collectionSize
     */
    public static void createCapperCollection(String collectionName, long collectionSize) {
        // TODO
        MongoConf mongoConf = null;
        if (database == null) {
            init(mongoConf);
        }
        database.createCollection(collectionName,
                new CreateCollectionOptions().capped(true).sizeInBytes(collectionSize));
    }

    /**
     * 获取数据库中集合列表
     *
     * @return
     */
    public static List<String> listCollection() {
        // TODO
        MongoConf mongoConf = null;
        if (database == null) {
            init(mongoConf);
        }
        List<String> collectionNameList = new ArrayList<>();
        MongoCursor<String> cursor = database.listCollectionNames().iterator();
        if (cursor.hasNext()) {
            collectionNameList.add(cursor.next());
        }
        return collectionNameList;
    }

    /**
     * 删除集合
     *
     * @param collectionName
     */
    public static void dropCollection(String collectionName) {
        // TODO
        MongoConf mongoConf = null;
        if (database == null) {
            init(mongoConf);
        }
        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.drop();
    }

    // ------------------------------ insert -------------------------------
    public static void insertDocument(Document document, String collectionName) {
        collection(collectionName).insertOne(document);
    }

    public static void insertEntity(Object entity, String collectionName) {
        collection(collectionName).insertOne(Document.parse(JSON.toJSONString(entity)));
    }

    public static void insertDocumentList(List<Document> documentList, String collectionName) {
        collection(collectionName).insertMany(documentList);
    }

    public static <T> void insertEntityList(List<T> entityList, String collectionName) {
        if (entityList == null || entityList.size() == 0) {
            return;
        }
        List<Document> documentList = entityList.stream()
                .map(entity -> Document.parse(JSON.toJSONString(entity)))
                .collect(Collectors.toList());
        collection(collectionName).insertMany(documentList);
    }

    // ------------------------------ update -------------------------------
    // 不使用修改器
    public static long updateMany(Bson filter, Bson update, String collectionName) {
        UpdateResult updateResult = collection(collectionName).updateMany(filter, update);
        return updateResult.getModifiedCount();
    }

    // 使用修改器
    public static long updateMany(String whereColumn, Object whereValue, String updateColumn, Object updateValue,
            String collectionName) {
        UpdateResult updateResult = collection(collectionName).updateMany(
                Filters.eq(whereColumn, whereValue),
                new Document("$set", new Document(updateColumn, updateValue))
        );
        return updateResult.getModifiedCount();
    }

    // ------------------------------ delete -------------------------------
    public static long deleteOne(Bson filter, String collectionName) {
        DeleteResult deleteResult = collection(collectionName).deleteOne(filter);
        return deleteResult.getDeletedCount();
    }

    public static long deleteMany(Bson filter, String collectionName) {
        DeleteResult deleteResult = collection(collectionName).deleteMany(filter);
        return deleteResult.getDeletedCount();
    }

    // ------------------------------ query -------------------------------
    private static FindIterable<Document> filter(String collectionName, Bson filter) {
        FindIterable<Document> findIterable = collection(collectionName).find(filter);
        return findIterable;
    }

//    private static FindIterable<Document> sort(FindIterable<Document> it, Map<String, Integer> sortFieldType) {
//        it.sort(Sorts.ascending())
//    }

    public static <T> Optional<List<T>> findMany(Bson filter, List<String> fields, Class<T> clazz, String collectionName) {
        FindIterable<Document> findIterable = filter(collectionName, filter);
        if (CollectionUtils.isNotEmpty(fields)) {
            findIterable.projection(Projections.fields(Projections.include(fields), Projections.excludeId()));
        }
        MongoCursor<Document> cursor = findIterable.iterator();
        List<T> list = new ArrayList<>();
        while (cursor.hasNext()) {
            list.add(JSON.parseObject(cursor.next().toJson(), clazz));
        }
        return Optional.of(list);
    }

    public static Optional<List<Document>> findMany(Bson filter, List<String> fields, String collectionName) {
        FindIterable<Document> findIterable = collection(collectionName).find(filter);
        if (CollectionUtils.isNotEmpty(fields)) {
            findIterable.projection(Projections.fields(Projections.include(fields), Projections.excludeId()));
        }
        MongoCursor<Document> cursor = findIterable.iterator();
        List<Document> list = new ArrayList<>();
        while (cursor.hasNext()) {
            list.add(cursor.next());
        }
        return Optional.of(list);
    }

    public static <T> Optional<T> findOne(Bson filter, List<String> fields, Class<T> clazz, String collectionName) {
        FindIterable<Document> findIterable = collection(collectionName).find(filter);
        if (CollectionUtils.isNotEmpty(fields)) {
            findIterable.projection(Projections.fields(Projections.include(fields), Projections.excludeId()));
        }
        Document document = findIterable.first();
        if (document != null) {
            return Optional.ofNullable(JSON.parseObject(document.toJson(), clazz));
        }
        return Optional.empty();
    }

    public static Optional<Document> findOne(Bson filter, List<String> fields, String collectionName) {
        FindIterable<Document> findIterable = collection(collectionName).find(filter);
        if (CollectionUtils.isNotEmpty(fields)) {
            findIterable.projection(Projections.fields(Projections.include(fields), Projections.excludeId()));
        }
        Document document = findIterable.first();
        return Optional.ofNullable(document);
    }

    public static Optional<List<Document>> queryByPage(Bson filter, List<String> fields, int pageNum, int pageSize, String collectionName) {
        FindIterable<Document> findIterable = collection(collectionName).find(filter);
        if (CollectionUtils.isNotEmpty(fields)) {
            findIterable.projection(Projections.fields(Projections.include(fields), Projections.excludeId()));
        }
        MongoCursor<Document> cursor = findIterable.skip((pageNum - 1) * pageSize).limit(pageSize).iterator();
        List<Document> list = new ArrayList<>();
        while (cursor.hasNext()) {
            list.add(cursor.next());
        }
        return Optional.of(list);
    }

    public static long countOfCollection(Bson filter, String collectionName) {
        if (filter == null) {
            return collection(collectionName).countDocuments();
        } else {
            return collection(collectionName).countDocuments(filter);
        }
    }

    // ------------------------------ index -------------------------------

    /**
     * 创建索引
     *
     * @param map
     * @param unique
     * @param collectionName
     */
    public static void createIndex(Map<String, Integer> map, boolean unique, String collectionName) {
        if (MapUtils.isEmpty(map)) {
            return;
        }
        List<String> fields = new ArrayList<>();
        Set<Integer> types = new HashSet<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            fields.add(entry.getKey());
            types.add(entry.getValue());
        }
        IndexOptions indexOptions = new IndexOptions().unique(unique);
        if (types.size() == 1) {
            int type = types.iterator().next();
            if (type == 1) {
                collection(collectionName).createIndex(Indexes.ascending(fields), indexOptions);
            } else if (type == -1) {
                collection(collectionName).createIndex(Indexes.descending(fields), indexOptions);
            }
        } else {
            List<Bson> bsonList = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String field = entry.getKey();
                int type = entry.getValue();
                if (type == 1) {
                    bsonList.add(Indexes.ascending(field));
                } else if (type == -1) {
                    bsonList.add(Indexes.descending(field));
                }
            }
            collection(collectionName).createIndex(Indexes.compoundIndex(bsonList), indexOptions);
        }
    }

    public static List<String> listIndexes(String collectionName) {
        MongoCursor<Document> cursor = collection(collectionName).listIndexes().iterator();
        List<String> list = new ArrayList<>();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            String indexName = doc.getString("name");
            list.add(indexName);
        }
        return list;
    }

}
