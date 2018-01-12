package com.ligx.rocksdb;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.rocksdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Author: ligongxing.
 * Date: 2017年08月30日.
 */
public class CacheService {

    private static final Logger logger = LoggerFactory.getLogger("cache");

    private static final String DB_PATH = "/tmp/data";

    static {
        RocksDB.loadLibrary();
    }

    /**
     * 向默认列族中添加数据到
     *
     * @param key   键
     * @param value 值
     */
    public static void add(String key, String value) {
        add(new String(RocksDB.DEFAULT_COLUMN_FAMILY), key, value);
    }

    /**
     * 向默认列族中批量插入数据
     *
     * @param keyValues 多个键值对
     */
    public static void multiAdd(Map<String, String> keyValues) {
        multiAdd(new String(RocksDB.DEFAULT_COLUMN_FAMILY), keyValues);
    }

    /**
     * 从默认列族查询数据
     *
     * @param key 键值
     * @return
     */
    public static String get(String key) {
        return get(new String(RocksDB.DEFAULT_COLUMN_FAMILY), key);
    }

    /**
     * 从默认列族中获取多个key对应的值
     *
     * @param keys 多个键值
     * @return
     */
    public static Map<String, String> multiGet(List<String> keys) {
        return multiGet(new String(RocksDB.DEFAULT_COLUMN_FAMILY), keys);
    }

    /**
     * 从默认列族中获取[startKey,endKey]范围内的数据
     *
     * @param startKey 起始键
     * @param endKey   终止键
     */
    public static Map<String, String> iterator(String startKey, String endKey) {
        return iterator(new String(RocksDB.DEFAULT_COLUMN_FAMILY), startKey, endKey);
    }

    /**
     * 从默认列族中删除key对应的数据
     *
     * @param key 键
     */
    public static void delete(String key) {
        delete(new String(RocksDB.DEFAULT_COLUMN_FAMILY), key);
    }


    /**
     * 添加数据到指定的列族
     *
     * @param columnFamily 要插入的k-v所在的列族(若没有会自动新建)
     * @param key          插入的键
     * @param value        插入的值
     */
    public static void add(String columnFamily, String key, String value) {
        if (StringUtils.isBlank(columnFamily) || StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
            return;
        }

        // specify all Column Family Descriptors
        List<ColumnFamilyDescriptor> columnFamilyDescriptorList = new ArrayList<>();
        int columnFamilyIndex = loadAllColumnFamilies(columnFamily, columnFamilyDescriptorList);

        List<ColumnFamilyHandle> columnFamilyHandleList = new ArrayList<>();

        try (DBOptions dbOptions = new DBOptions()
                .setCreateIfMissing(true)
                .setCreateMissingColumnFamilies(true);
             RocksDB db = RocksDB.open(dbOptions, DB_PATH, columnFamilyDescriptorList, columnFamilyHandleList)) {

            try {
                db.put(columnFamilyHandleList.get(columnFamilyIndex), key.getBytes(), value.getBytes());
            } finally {
                // frees the column family handles before freeing the db
                columnFamilyHandleList.forEach(AbstractImmutableNativeReference::close);
            }
        } catch (Exception e) {
            logger.error("Exception adding data to rocksDB! columnFamily={}, key={}, value={}",
                    columnFamily, key, value, e);
        } // frees the db and the db options
    }


    /**
     * 向指定列族批量插入数据
     *
     * @param columnFamily 要插入的k-v所在的列族(若没有会自动新建)
     * @param keyValues    多个键值对
     */
    public static void multiAdd(String columnFamily, Map<String, String> keyValues) {
        if (MapUtils.isEmpty(keyValues) || StringUtils.isBlank(columnFamily)) {
            return;
        }
        // specify all Column Family Descriptors
        List<ColumnFamilyDescriptor> columnFamilyDescriptorList = new ArrayList<>();
        int columnFamilyIndex = loadAllColumnFamilies(columnFamily, columnFamilyDescriptorList);

        List<ColumnFamilyHandle> columnFamilyHandleList = new ArrayList<>();

        try (DBOptions dbOptions = new DBOptions()
                .setCreateIfMissing(true)
                .setCreateMissingColumnFamilies(true);
             RocksDB db = RocksDB.open(dbOptions, DB_PATH, columnFamilyDescriptorList, columnFamilyHandleList);
             WriteOptions writeOptions = new WriteOptions();
             WriteBatch batch = new WriteBatch()) {

            try {
                ColumnFamilyHandle columnFamilyHandle = columnFamilyHandleList.get(columnFamilyIndex);
                for (Map.Entry<String, String> entry : keyValues.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
                        logger.warn("Existing null field while multiAdding data to RocksDB, key={}, value={}",
                                key, value);
                        continue;
                    }
                    batch.put(columnFamilyHandle, key.getBytes(), value.getBytes());
                }
                db.write(writeOptions, batch);
            } finally {
                columnFamilyHandleList.forEach(AbstractImmutableNativeReference::close);
            }
        } catch (Exception e) {
            logger.error("Exception multiAdding data to RocksDB! columnFamily={}, keyValues={}",
                    columnFamily, keyValues, e);
        }
    }

    /**
     * 从给定列族中获取指定key对应的值
     *
     * @param columnFamily 要查询的key所在的列族
     * @param key          要查询的key
     * @return
     */
    public static String get(String columnFamily, String key) {
        if (StringUtils.isBlank(columnFamily) || StringUtils.isBlank(key)) {
            return null;
        }
        try (ColumnFamilyOptions cfOpts = new ColumnFamilyOptions()) {
            int columnFamilyIndex = 0;
            List<ColumnFamilyDescriptor> columnFamilyDescriptorList = new ArrayList<>();
            // have to open default column family, otherwise throw exception
            columnFamilyDescriptorList.add(new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, cfOpts));
            if (!columnFamily.equals(new String(RocksDB.DEFAULT_COLUMN_FAMILY))) {
                columnFamilyDescriptorList.add(new ColumnFamilyDescriptor(columnFamily.getBytes(), cfOpts));
                columnFamilyIndex = 1;
            }

            List<ColumnFamilyHandle> columnFamilyHandleList = new ArrayList<>();

            try (DBOptions dbOptions = new DBOptions()
                    .setCreateIfMissing(true)
                    .setCreateMissingColumnFamilies(true);
                 RocksDB db = RocksDB.openReadOnly(dbOptions, DB_PATH, columnFamilyDescriptorList,
                         columnFamilyHandleList)) {

                try {
                    byte[] value = db.get(columnFamilyHandleList.get(columnFamilyIndex), key.getBytes());
                    return (value == null || value.length == 0) ? null : new String(value);
                } finally {
                    columnFamilyHandleList.forEach(AbstractImmutableNativeReference::close);
                }
            }
        } catch (Exception e) {
            logger.error("Exception getting data from rocksDB! columnFamily={}, key={}", columnFamily, key, e);
        }
        return null;
    }


    /**
     * 批量从指定列族获中取多个key对应的值
     *
     * @param columnFamily 在哪一个列族上进行批量查询
     * @param keys         多个key
     * @return
     */
    public static Map<String, String> multiGet(String columnFamily, List<String> keys) {
        Map<String, String> keyValues = new HashMap<>();
        if (CollectionUtils.isEmpty(keys) || StringUtils.isBlank(columnFamily)) {
            return keyValues;
        }
        try (ColumnFamilyOptions cfOpts = new ColumnFamilyOptions()) {
            List<ColumnFamilyDescriptor> columnFamilyDescriptorList = new ArrayList<>();
            if (columnFamily.equals(new String(RocksDB.DEFAULT_COLUMN_FAMILY))) {
                for (int i = 0; i < keys.size(); i++) {
                    columnFamilyDescriptorList.add(new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, cfOpts));
                }
            } else {
                byte[] cfmByte = columnFamily.getBytes();
                for (int i = 0; i < keys.size(); i++) {
                    columnFamilyDescriptorList.add(new ColumnFamilyDescriptor(cfmByte, cfOpts));
                }
                columnFamilyDescriptorList.add(new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, cfOpts));
                keys.add("undefine");
            }

            List<ColumnFamilyHandle> columnFamilyHandleList = new ArrayList<>();

            try (DBOptions dbOptions = new DBOptions()
                    .setCreateIfMissing(true)
                    .setCreateMissingColumnFamilies(true);
                 RocksDB db = RocksDB.openReadOnly(dbOptions, DB_PATH, columnFamilyDescriptorList,
                         columnFamilyHandleList)) {

                try {
                    List<byte[]> keyBytes = keys.stream()
                            .map(String::getBytes)
                            .collect(Collectors.toList());
                    Map<byte[], byte[]> keyValueBytes = db.multiGet(columnFamilyHandleList, keyBytes);
                    keyValues = keyValueBytes.entrySet().stream()
                            .collect(Collectors.toMap(
                                    entry -> new String(entry.getKey()),
                                    entry -> new String(entry.getValue())));
                } finally {
                    columnFamilyHandleList.forEach(AbstractImmutableNativeReference::close);
                }
            }
        } catch (Exception e) {
            logger.error("Exception multiGetting data from RocksDB. columnFamily={}, keys={}",
                    columnFamily, keys, e);
        }
        return keyValues;
    }


    /**
     * 从指定列族中获取[startKey,endKey]范围内的数据
     *
     * @param columnFamily 起止键所在的列族
     * @param startKey     起始键
     * @param endKey       终止键
     * @return
     */
    public static Map<String, String> iterator(String columnFamily, String startKey, String endKey) {
        Map<String, String> keyValues = new HashMap<>();
        if (StringUtils.isBlank(columnFamily) || StringUtils.isBlank(startKey) || StringUtils.isBlank(endKey)) {
            return keyValues;
        }
        try (ColumnFamilyOptions cfOpts = new ColumnFamilyOptions()) {
            // specify part Column Family Descriptors
            int columnFamilyIndex = 0;
            List<ColumnFamilyDescriptor> columnFamilyDescriptorList = new ArrayList<>();
            columnFamilyDescriptorList.add(new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, cfOpts));
            if (!columnFamily.equals(new String(RocksDB.DEFAULT_COLUMN_FAMILY))) {
                columnFamilyDescriptorList.add(new ColumnFamilyDescriptor(columnFamily.getBytes(), cfOpts));
                columnFamilyIndex = columnFamilyDescriptorList.size() - 1;
            }

            List<ColumnFamilyHandle> columnFamilyHandleList = new ArrayList<>();

            try (DBOptions dbOptions = new DBOptions()
                    .setCreateIfMissing(true)
                    .setCreateMissingColumnFamilies(true);
                 RocksDB db = RocksDB.openReadOnly(dbOptions, DB_PATH, columnFamilyDescriptorList,
                         columnFamilyHandleList);
                 RocksIterator iterator = db.newIterator(columnFamilyHandleList.get(columnFamilyIndex))) {

                try {
                    for (iterator.seek(startKey.getBytes());
                         iterator.isValid() && new String(iterator.key()).compareTo(endKey) <= 0;
                         iterator.next()) {
                        byte[] key = iterator.key();
                        byte[] value = iterator.value();
                        if (ArrayUtils.isEmpty(key) || ArrayUtils.isEmpty(value)) {
                            continue;
                        }
                        keyValues.put(new String(key), new String(value));
                    }
                } finally {
                    columnFamilyHandleList.forEach(AbstractImmutableNativeReference::close);
                }
            }
        } catch (Exception e) {
            logger.error("Exception iterating data from RocksDB! columnFamily={}, startKey={}, endKey={}",
                    columnFamily, startKey, endKey, e);
        }
        return keyValues;
    }


    /**
     * 从指定列族中删除指定的key
     *
     * @param columnFamily 要删除的key所在的列族
     * @param key          要删除的key
     */
    public static void delete(String columnFamily, String key) {
        if (StringUtils.isBlank(columnFamily) || StringUtils.isBlank(key)) {
            return;
        }
        // specify all Column Family Descriptors
        List<ColumnFamilyDescriptor> columnFamilyDescriptorList = new ArrayList<>();
        int columnFamilyIndex = loadAllColumnFamilies(columnFamily, columnFamilyDescriptorList);

        List<ColumnFamilyHandle> columnFamilyHandleList = new ArrayList<>();

        try (DBOptions dbOptions = new DBOptions()
                .setCreateIfMissing(true)
                .setCreateMissingColumnFamilies(true);
             RocksDB db = RocksDB.open(dbOptions, DB_PATH, columnFamilyDescriptorList, columnFamilyHandleList)) {

            try {
                db.delete(columnFamilyHandleList.get(columnFamilyIndex), key.getBytes());
            } finally {
                columnFamilyHandleList.forEach(AbstractImmutableNativeReference::close);
            }
        } catch (Exception e) {
            logger.error("Exception deleting data from rocksDB! columnFamily={}, key={}",
                    columnFamily, key, e);
        }
    }

    /**
     * 获取所有的列族
     *
     * @return
     */
    public static List<String> listColumnFamilies() {
        try (Options options = new Options().setCreateIfMissing(true)) {
            List<byte[]> columnFamilies = RocksDB.listColumnFamilies(options, DB_PATH);
            return columnFamilies.stream().map(String::new).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Exception list all Column Families!", e);
        }
        return Collections.singletonList(new String(RocksDB.DEFAULT_COLUMN_FAMILY));
    }


    /**
     * 删除指定的列族
     *
     * @param columnFamily 要删除的列族
     */
    public static void deleteColumnFamily(String columnFamily) {
        if (StringUtils.isBlank(columnFamily)) {
            return;
        }
        // specify all Column Family Descriptors
        List<ColumnFamilyDescriptor> columnFamilyDescriptorList = new ArrayList<>();
        int columnFamilyIndex = loadAllColumnFamilies(columnFamily, columnFamilyDescriptorList);

        List<ColumnFamilyHandle> columnFamilyHandleList = new ArrayList<>();

        try (DBOptions dbOptions = new DBOptions()
                .setCreateIfMissing(true)
                .setCreateMissingColumnFamilies(true);
             RocksDB db = RocksDB.open(dbOptions, DB_PATH, columnFamilyDescriptorList, columnFamilyHandleList)) {

            try {
                db.dropColumnFamily(columnFamilyHandleList.get(columnFamilyIndex));
            } finally {
                columnFamilyHandleList.forEach(AbstractImmutableNativeReference::close);
            }
        } catch (Exception e) {
            logger.error("Exception deleting Column Family! columnFamily={}",
                    columnFamily, e);
        }
    }


    /**
     * 加载RocksDB中所有的列族，并返回给定列族所在的位置
     *
     * @param columnFamily               指定要read/write的列族
     * @param columnFamilyDescriptorList 存储RocksDB已经存在的所有列族对应的ColumnFamilyDescriptor
     * @return
     */
    private static int loadAllColumnFamilies(String columnFamily,
                                             List<ColumnFamilyDescriptor> columnFamilyDescriptorList) {
        int columnFamilyIndex = 0;
        try (ColumnFamilyOptions cfOpts = new ColumnFamilyOptions();
             Options options = new Options()) {
            // get all Column Families
            List<byte[]> allColumnFamilies = RocksDB.listColumnFamilies(options, DB_PATH);

            if (allColumnFamilies.size() > 0) {
                boolean isExists = false;
                for (int i = 0; i < allColumnFamilies.size(); i++) {
                    byte[] cfmByte = allColumnFamilies.get(i);
                    columnFamilyDescriptorList.add(new ColumnFamilyDescriptor(cfmByte, cfOpts));
                    if (columnFamily.equals(new String(cfmByte))) {
                        isExists = true;
                        columnFamilyIndex = i;
                    }
                }
                if (!isExists) {
                    columnFamilyDescriptorList.add(new ColumnFamilyDescriptor(columnFamily.getBytes(), cfOpts));
                    columnFamilyIndex = columnFamilyDescriptorList.size() - 1;
                }
            } else {
                // have to open default column family
                columnFamilyDescriptorList.add(new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, cfOpts));
                columnFamilyDescriptorList.add(new ColumnFamilyDescriptor(columnFamily.getBytes(), cfOpts));
                columnFamilyIndex = columnFamilyDescriptorList.size() - 1;
            }
        } catch (Exception e) {
            logger.error("");
        }
        return columnFamilyIndex;
    }
}
