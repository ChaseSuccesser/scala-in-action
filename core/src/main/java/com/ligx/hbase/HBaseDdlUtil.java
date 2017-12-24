package com.ligx.hbase;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: ligongxing.
 * Date: 2017年08月28日.
 */
public class HBaseDdlUtil {

    private static final Logger HBASE_LOGGER = LoggerFactory.getLogger("hbase");


    /**
     * 创建表
     *
     * @param tableName      表名
     * @param columnFamilies 列族
     */
    public static void createTable(String tableName, String... columnFamilies) {
        try (Admin admin = HBaseInfo.getAdmin()) {
            if (admin.tableExists(TableName.valueOf(tableName))) {
                return;
            }

            HTableDescriptor htd = new HTableDescriptor(TableName.valueOf(tableName));
            if(columnFamilies != null && columnFamilies.length > 0){
                for (String columnFamily : columnFamilies) {
                    htd.addFamily(new HColumnDescriptor(columnFamily));
                }
            }

            admin.createTable(htd);
        } catch (Exception e) {
            HBASE_LOGGER.error("HBaseDdlUtil#createTable, tableName={}, columnFamilies={}", tableName, columnFamilies, e);
        }
    }

    /**
     * 修改表
     *
     * @param tableName      表名
     * @param columnFamilies 列族
     */
    public static void modifyTable(String tableName, String... columnFamilies) {
        try (Admin admin = HBaseInfo.getAdmin()) {
            if(!admin.tableExists(TableName.valueOf(tableName))){
                return;
            }

            HTableDescriptor htd = admin.getTableDescriptor(TableName.valueOf(tableName));
            for (String columnFamily : columnFamilies) {
                htd.addFamily(new HColumnDescriptor(columnFamily));
            }

            admin.disableTable(TableName.valueOf(tableName));
            admin.modifyTable(TableName.valueOf(tableName), htd);
            admin.enableTable(TableName.valueOf(tableName));
        } catch (Exception e) {
            HBASE_LOGGER.error("HBaseDdlUtil#modifyTable, tableName={}, columnFamilies={}", tableName, columnFamilies, e);
        }
    }

    /**
     * 删除表
     *
     * @param tableName 表名
     */
    public static void deleteTable(String tableName) {
        try (Admin admin = HBaseInfo.getAdmin()) {
            admin.disableTable(TableName.valueOf(tableName));
            admin.deleteTable(TableName.valueOf(tableName));
        } catch (Exception e) {
            HBASE_LOGGER.error("HBaseDdlUtil#deleteTable, tableName={}", tableName, e);
        }
    }
}
