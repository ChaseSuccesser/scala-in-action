package com.ligx.hbase;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: ligongxing.
 * Date: 2017年08月28日.
 */
public class HBaseDmlUtil {

    private static final Logger HBASE_LOGGER = LoggerFactory.getLogger("hbase");

    /**
     * 插入数据
     *
     * @param tableName        表名
     * @param rowKey           行键
     * @param columnFamily     列族
     * @param columnIdentifier 列限定符
     * @param value            值
     * @throws IOException
     */
    public static void insert(String tableName, String rowKey, String columnFamily, String columnIdentifier, String value) {
        try (Table table = HBaseInfo.getTable(tableName)) {
            Put put = new Put(Bytes.toBytes(rowKey));
            put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columnIdentifier), Bytes.toBytes(value));

            table.put(put);
        } catch (Exception e) {
            HBASE_LOGGER.error("HBaseDmlUtil#insert, tableName={}, rowKey={}, columnFamily={}, columnIdentifier={}, value={}",
                    tableName, rowKey, columnFamily, columnIdentifier, value, e);
        }
    }


    /**
     * 检索数据
     *
     * @param tableName        表名
     * @param rowKey           行键
     * @param columnFamily     列族
     * @param columnIdentifier 列限定符
     * @param minTimestamp     起始时间戳
     * @param maxTimestamp     结束时间戳
     * @param maxVersion       最大版本
     * @return
     */
    public static Result get(String tableName, String rowKey, String columnFamily, String columnIdentifier,
                             long minTimestamp, long maxTimestamp, int maxVersion) {
        try (Table table = HBaseInfo.getTable(tableName)) {
            Get get = new Get(Bytes.toBytes(rowKey));
            if (StringUtils.isNotBlank(columnFamily) && StringUtils.isNotBlank(columnIdentifier)) {
                get.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columnIdentifier));
            } else if (StringUtils.isNotBlank(columnFamily) && StringUtils.isBlank(columnIdentifier)) {
                get.addFamily(Bytes.toBytes(columnFamily));
            }
            if (minTimestamp > 0L && maxTimestamp > 0L) {
                get.setTimeRange(minTimestamp, maxTimestamp);
            }
            if (maxVersion > 1) {
                get.setMaxVersions(maxVersion);
            }

            Result result = table.get(get);
            return result;
        } catch (Exception e) {
            HBASE_LOGGER.error("HBaseDmlUtil#get, tableName={}, rowKey={}, cloumnFamily={}, columnIdentifier={}," +
                            "minTimestamp={}, maxTimestamp={}, maxVersion={}", tableName, rowKey, columnFamily, columnIdentifier,
                    minTimestamp, maxTimestamp, maxVersion, e);
        }
        return null;
    }

    public static Result get(String tableName, String rowKey, String columnFamily) {
        return get(tableName, rowKey, columnFamily, null, 0L, 0L, 0);
    }

    public static Result get(String tableName, String rowKey, String columnFamily, String columnIdentifier) {
        return get(tableName, rowKey, columnFamily, columnIdentifier, 0L, 0L, 0);
    }

    /**
     * 检查数据是否存在
     *
     * @param tableName        表名
     * @param rowKey           行键
     * @param columnFamily     列族
     * @param columnIdentifier 列限定符
     * @return
     */
    public static boolean exists(String tableName, String rowKey, String columnFamily, String columnIdentifier) {
        try (Table table = HBaseInfo.getTable(tableName)) {
            Get get = new Get(Bytes.toBytes(rowKey));
            if (StringUtils.isNotBlank(columnFamily) && StringUtils.isNotBlank(columnIdentifier)) {
                get.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columnIdentifier));
            } else if (StringUtils.isNotBlank(columnFamily) && StringUtils.isBlank(columnIdentifier)) {
                get.addFamily(Bytes.toBytes(columnFamily));
            }

            return table.exists(get);
        } catch (Exception e) {
            HBASE_LOGGER.error("HBaseDmlUtil#exists, tableName={}, rowKey={}, columnFamily={}, columnIdentifier={}",
                    tableName, rowKey, columnFamily, columnIdentifier, e);
        }
        return false;
    }


    // ----------------------------- DELETE OPERATION -----------------------------

    /**
     * 删除整行
     *
     * @param tableName
     * @param rowKey
     */
    public static void deleteEntireRow(String tableName, String rowKey) {
        try (Table table = HBaseInfo.getTable(tableName)) {
            Delete delete = new Delete(Bytes.toBytes(rowKey));
            table.delete(delete);
        } catch (Exception e) {
            HBASE_LOGGER.error("HBaseDmlUtil#deleteEntireRow, tableName={}, rowKey={}", e);
        }
    }

    /**
     * 删除给定列族的所有版本
     *
     * @param tableName
     * @param rowKey
     * @param columnFamily
     */
    public static void deleteFamily(String tableName, String rowKey, String columnFamily) {
        try (Table table = HBaseInfo.getTable(tableName)) {
            Delete delete = new Delete(Bytes.toBytes(rowKey));
            delete.addFamily(Bytes.toBytes(columnFamily));
            table.delete(delete);
        } catch (Exception e) {
            HBASE_LOGGER.error("HBaseDmlUtil#deleteFamily, tableName={}, rowKey={}, columnFamily={}", e);
        }
    }

    /**
     * 删除给定列的所有版本
     *
     * @param tableName
     * @param rowKey
     * @param columnFamily
     * @param columnIdentifier
     */
    public static void deleteColumns(String tableName, String rowKey, String columnFamily, String columnIdentifier) {
        try (Table table = HBaseInfo.getTable(tableName)) {
            Delete delete = new Delete(Bytes.toBytes(rowKey));
            delete.addColumns(Bytes.toBytes(columnFamily), Bytes.toBytes(columnIdentifier));
            table.delete(delete);
        } catch (Exception e) {
            HBASE_LOGGER.error("HBaseDmlUtil#deleteColumns, tableName={}, rowKey={}, columnFamily={}, columnIdentifier={}",
                    tableName, rowKey, columnFamily, columnIdentifier, e);
        }
    }

    /**
     * 删除给定列的最新版本
     *
     * @param tableName
     * @param rowKey
     * @param columnFamily
     * @param columnIdentifier
     */
    public static void deleteColumn(String tableName, String rowKey, String columnFamily, String columnIdentifier) {
        try (Table table = HBaseInfo.getTable(tableName)) {
            Delete delete = new Delete(Bytes.toBytes(rowKey));
            delete.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columnIdentifier));
            table.delete(delete);
        } catch (Exception e) {
            HBASE_LOGGER.error("HBaseDmlUtil#deleteColumn, tableName={}, rowKey={}, columnFamily={}, columnIdentifier={}",
                    tableName, rowKey, columnFamily, columnIdentifier, e);
        }
    }

    /**
     * 扫描表
     *
     * @param tableName   表名
     * @param startRowKey 起始行键
     * @param endRowKey   终止行键
     * @return
     */
    public static List<Result> scan(String tableName, String startRowKey, String endRowKey) {
        List<Result> resultList = new ArrayList<>();
        try (Table table = HBaseInfo.getTable(tableName)) {
            Scan scan = new Scan(Bytes.toBytes(startRowKey), Bytes.toBytes(endRowKey));

            try (ResultScanner resultScanner = table.getScanner(scan)) {
                for (Result result : resultScanner) {
                    resultList.add(result);
                }
            }
        } catch (Exception e) {
            HBASE_LOGGER.error("HBaseDmlUtil#scan, startRowKey={}, endRowKey={}", startRowKey, endRowKey, e);
        }
        return resultList;
    }
}
