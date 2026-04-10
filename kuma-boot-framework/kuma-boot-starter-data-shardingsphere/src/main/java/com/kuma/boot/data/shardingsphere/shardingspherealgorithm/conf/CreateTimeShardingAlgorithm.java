package com.kuma.boot.data.shardingsphere.shardingspherealgorithm.conf;

import com.kuma.boot.common.utils.lang.StringUtils;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource;

public interface CreateTimeShardingAlgorithm {
   String buildNodesSuffix(LocalDate date);

   LocalDate buildNodesAfterDate(LocalDate date);

   LocalDate buildNodesBeforeDate(LocalDate date);

   default String buildNodes(String tableName, Integer count) {
      List<String> tableNameList = new ArrayList();
      LocalDate today = LocalDate.now();

      for(int i = 0; i < count; ++i) {
         tableNameList.add("db0." + tableName + "_${'" + this.buildNodesSuffix(today) + "'}");
         today = this.buildNodesBeforeDate(today);
      }

      return StringUtils.join(tableNameList, ",");
   }

   default void createTables(ShardingSphereDataSource dataSource, String tableName, Integer count) {
      try {
         Connection connection = dataSource.getConnection();

         try {
            Statement statement = connection.createStatement();

            try {
               LocalDate today = LocalDate.now();
               String oldTableName = "";
               String newTableName = "";

               for(int i = 0; i < count; ++i) {
                  oldTableName = tableName + "_" + this.buildNodesSuffix(today);
                  today = this.buildNodesAfterDate(today);
                  newTableName = tableName + "_" + this.buildNodesSuffix(today);
                  statement.execute("create table IF NOT EXISTS `" + newTableName + "` like  `" + oldTableName + "`");
               }
            } catch (Throwable var12) {
               if (statement != null) {
                  try {
                     statement.close();
                  } catch (Throwable var11) {
                     var12.addSuppressed(var11);
                  }
               }

               throw var12;
            }

            if (statement != null) {
               statement.close();
            }
         } catch (Throwable var13) {
            if (connection != null) {
               try {
                  connection.close();
               } catch (Throwable var10) {
                  var13.addSuppressed(var10);
               }
            }

            throw var13;
         }

         if (connection != null) {
            connection.close();
         }

      } catch (SQLException var14) {
         throw new RuntimeException("\u5efa\u8868\u5931\u8d25");
      }
   }
}
