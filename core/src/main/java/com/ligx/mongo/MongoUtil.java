package com.ligx.mongo;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Author: ligongxing.
 * Date: 2018/08/02.
 * <p>
 * mongodb文档: http://mongodb.github.io/mongo-java-driver/3.8/driver/getting-started/quick-start/
 * <p>
 * Object -> json -> Document.parse(json) [即Bson]
 * <p>
 * Filters helper -> Bson
 * <p>
 * https://github.com/jasonsuzhou/MongodbUtil/blob/d4548d95e33b6b10114702809198f367f27c2e3a/src/com/mh/mongo/core/MongoTemplate.java
 */
public class MongoUtil {

    private static MongoClient client;

    private static MongoDatabase database;

    private static MongoCollection<Document> collection;

    private MongoUtil() {
    }

    // ------------------------------ insert -------------------------------
    public static void insertDocument(Document document) {
        collection.insertOne(document);
    }

    public static void insertEntity(Object entity) {
        collection.insertOne(Document.parse(JSON.toJSONString(entity)));
    }

    public static void insertDocumentList(List<Document> documentList) {
        collection.insertMany(documentList);
    }

    public static <T> void insertEntityList(List<T> entityList) {
        if (entityList == null || entityList.size() == 0) {
            return;
        }
        List<Document> documentList = entityList.stream()
                .map(entity -> Document.parse(JSON.toJSONString(entity)))
                .collect(Collectors.toList());
        collection.insertMany(documentList);
    }

    // ------------------------------ update -------------------------------
    // 不使用修改器
    public static long updateMany(Bson filter, Bson update) {
        UpdateResult updateResult = collection.updateMany(filter, update);
        return updateResult.getModifiedCount();
    }

    // 使用修改器
    public static long updateMany(String whereColumn, Object whereValue, String updateColumn, Object updateValue) {
        UpdateResult updateResult = collection.updateMany(
                Filters.eq(whereColumn, whereValue),
                new Document("$set", new Document(updateColumn, updateValue))
        );
        return updateResult.getModifiedCount();
    }

    // ------------------------------ delete -------------------------------
    public static long deleteOne(Bson filter) {
        DeleteResult deleteResult = collection.deleteOne(filter);
        return deleteResult.getDeletedCount();
    }

    public static long deleteMany(Bson filter) {
        DeleteResult deleteResult = collection.deleteMany(filter);
        return deleteResult.getDeletedCount();
    }

    // ------------------------------ query -------------------------------
    public static <T> Optional<List<T>> findMany(Bson filter, Class<T> clazz) {
        MongoCursor<Document> cursor = collection.find(filter).iterator();
        List<T> list = new ArrayList<>();
        while (cursor.hasNext()) {
            list.add(JSON.parseObject(cursor.next().toJson(), clazz));
        }
        return Optional.of(list);
    }

    public static Optional<List<Document>> findMany(Bson filter) {
        MongoCursor<Document> cursor = collection.find(filter).iterator();
        List<Document> list = new ArrayList<>();
        while (cursor.hasNext()) {
            list.add(cursor.next());
        }
        return Optional.of(list);
    }

    public static <T> Optional<T> findOne(Bson filter, Class<T> clazz) {
        Document document = collection.find(filter).first();
        if (document != null) {
            return Optional.ofNullable(JSON.parseObject(document.toJson(), clazz));
        }
        return Optional.empty();
    }

    public static Optional<Document> findOne(Bson filter) {
        Document document = collection.find(filter).first();
        return Optional.ofNullable(document);
    }

    public static Optional<List<Document>> queryByPage(Bson filter, int pageNum, int pageSize) {
        MongoCursor<Document> cursor = collection.find(filter).skip((pageNum - 1) * pageSize).limit(pageSize).iterator();
        List<Document> list = new ArrayList<>();
        while (cursor.hasNext()) {
            list.add(cursor.next());
        }
        return Optional.of(list);
    }

}
