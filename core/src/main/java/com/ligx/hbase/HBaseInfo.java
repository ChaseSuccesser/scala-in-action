package com.ligx.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Author: ligongxing.
 * Date: 2017年08月28日.
 */
public class HBaseInfo {

    private static final Logger HBASE_LOGGER = LoggerFactory.getLogger("hbase");

    private static Configuration conf = HBaseConfiguration.create();

    /**
     * 获取表实例
     *
     * @param tableName 表名
     * @return
     */
    public static Table getTable(String tableName) {
        try {
            Connection conn = ConnectionFactory.createConnection(conf);
            Table table = conn.getTable(TableName.valueOf(tableName));
            return table;
        } catch (IOException e) {
            HBASE_LOGGER.error("HBaseInfo#getTable, error to get Table, tableName={}!", tableName, e);
        }
        return null;
    }

    /**
     * 获取Admin实例
     *
     * @return
     */
    public static Admin getAdmin() {
        try {
            Connection conn = ConnectionFactory.createConnection(conf);
            return conn.getAdmin();
        } catch (Exception e) {
            HBASE_LOGGER.error("HBaseInfo#getAdmin.", e);
        }
        return null;
    }
}
