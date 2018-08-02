package com.ligx.mongo;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: ligongxing.
 * Date: 2018/08/02.
 *
 * http://mongodb.github.io/mongo-java-driver/3.8/driver/getting-started/quick-start/
 *
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
    public static void update() {

    }

    // ------------------------------ query -------------------------------
    public static <T> List<T> findByCondition(Bson filter, Class<T> clazz) {
        MongoCursor<Document> cursor = collection.find(filter).iterator();
        List<T> list = new ArrayList<>();
        while (cursor.hasNext()) {
            list.add(JSON.parseObject(cursor.next().toJson(), clazz));
        }
        return list;
    }
}
