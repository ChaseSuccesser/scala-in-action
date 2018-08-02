package com.ligx.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * Author: ligongxing.
 * Date: 2018/08/02.
 */
public class MongoManager {

    private MongoClient mongo;
    private MongoDatabase db;

    public void createCollection(String collectionName) {
        if (db != null) {
            db.createCollection(collectionName);
        }

    }
}
