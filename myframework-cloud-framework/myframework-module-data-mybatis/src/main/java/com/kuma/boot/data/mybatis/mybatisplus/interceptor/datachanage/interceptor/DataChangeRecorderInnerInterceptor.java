/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datachanage.interceptor;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.mybatis.mybatisplus.MpUtils;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datachanage.annotation.DataVersionLog;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datachanage.model.DataVersionLogData;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datachanage.service.DataVersionLogService;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.update.UpdateSet;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

/**
 * 数据变动记录插件, 基于MP官方进行修改
 */
public class DataChangeRecorderInnerInterceptor implements InnerInterceptor {

    @SuppressWarnings("unused")
    public static final String IGNORED_TABLE_COLUMN_PROPERTIES = "ignoredTableColumns";

    private final Map<String, Set<String>> ignoredTableColumns = new ConcurrentHashMap<>();
    // 全部表的这些字段名，INSERT/UPDATE都忽略，delete暂时保留
    private final Set<String> ignoreAllColumns = new HashSet<>();

    /**
     * 解析前操作
     *
     * @param sh StatementHandler(可能是代理对象)
     * @param connection Connection
     * @param transactionTimeout transactionTimeout
     */
    @Override
    public void beforePrepare(
            StatementHandler sh, Connection connection, Integer transactionTimeout ) {
        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpSh.mappedStatement();
        final BoundSql boundSql = mpSh.boundSql();
        SqlCommandType sct = ms.getSqlCommandType();
        if (sct == SqlCommandType.INSERT
                || sct == SqlCommandType.UPDATE
                || sct == SqlCommandType.DELETE) {
            PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
            OperationResult operationResult;
            long startTs = System.currentTimeMillis();
            DataVersionLog dataVersion;
            try {
                Statement statement = CCJSqlParserUtil.parse(mpBs.sql());
                // 判断是否需要进行处理
                dataVersion = this.getDataVersionAnno(statement);
                if (Objects.isNull(dataVersion)) {
                    return;
                }

                if (statement instanceof Insert) {
                    operationResult = processInsert((Insert) statement, mpSh.boundSql());
                } else if (statement instanceof Update) {
                    operationResult = processUpdate((Update) statement, ms, boundSql, connection);
                } else if (statement instanceof Delete) {
                    operationResult = processDelete((Delete) statement, ms, boundSql, connection);
                } else {
                    return;
                }
            } catch (Exception e) {
                LogUtils.error(
                        "Unexpected error for mappedStatement={}, sql={}",
                        ms.getId(),
                        mpBs.sql(),
                        e);
                return;
            }
            long costThis = System.currentTimeMillis() - startTs;
            if (operationResult != null) {
                operationResult.setCost(costThis);
                operationResult.setAnnotation(dataVersion);
                this.dealOperationResult(operationResult);
            }
        }
    }

    /**
     * 判断哪些SQL需要处理
     */
    protected DataVersionLog getDataVersionAnno( Statement statement ) {
        String tableName;
        if (statement instanceof Insert) {
            tableName = ( (Insert) statement ).getTable().getName();
        } else if (statement instanceof Update) {
            tableName = ( (Update) statement ).getTable().getName();
        } else if (statement instanceof Delete) {
            tableName = ( (Delete) statement ).getTable().getName();
        } else {
            return null;
        }

        TableInfo tableInfo = MpUtils.getTableInfo(tableName);
        if (Objects.isNull(tableInfo)) {
            return null;
        }
        return tableInfo.getEntityType().getAnnotation(DataVersionLog.class);
    }

    /**
     * 处理数据更新结果，默认打印
     */
    protected void dealOperationResult( OperationResult operationResult ) {
        DataVersionLog annotation = operationResult.getAnnotation();
        List<DataChangedRecord> changedRecords = operationResult.getChangedRecords();
        // 遍历条信息
        for (DataChangedRecord changedRecord : changedRecords) {
            // 原始数据
            List<DataColumnChangeResult> originalColumns =
                    Optional.ofNullable(changedRecord.getOriginalColumns())
                            .orElse(new ArrayList<>(0));
            Map<String, Object> dataRecord = new HashMap<>();
            Map<String, Object> updateRecord = new HashMap<>();
            // 遍历原始数据的所有字段
            for (DataColumnChangeResult originalColumn : originalColumns) {
                dataRecord.put(originalColumn.getColumnName(), originalColumn.getOriginalValue());
            }
            // 用新数据进行替换
            List<DataColumnChangeResult> updatedColumns =
                    Optional.ofNullable(changedRecord.getUpdatedColumns())
                            .orElse(new ArrayList<>(0));
            for (DataColumnChangeResult updatedColumn : updatedColumns) {
                // 更新记录列表记录变更前的数据
                updateRecord.put(
                        updatedColumn.getColumnName(),
                        dataRecord.get(updatedColumn.getColumnName()));
                dataRecord.put(updatedColumn.getColumnName(), updatedColumn.getUpdateValue());
            }
            Object pkColumnVal = changedRecord.getPkColumnVal();
            // insert手动获取下主键值
            if ("insert".equals(operationResult.getOperation())) {
                String keyProperty =
                        Optional.ofNullable(MpUtils.getTableInfo(operationResult.getTableName()))
                                .map(TableInfo::getKeyProperty)
                                .map(String::toUpperCase)
                                .orElse(null);
                pkColumnVal = dataRecord.get(keyProperty);
            }

            // 保存记录
            DataVersionLogData dataVersionLogData = new DataVersionLogData();
            dataVersionLogData.setTableName(operationResult.getTableName());
            dataVersionLogData.setDataName(annotation.title());
            dataVersionLogData.setDataId(pkColumnVal.toString());
            dataVersionLogData.setDataContent(dataRecord);
            dataVersionLogData.setChangeContent(updateRecord);
            dataVersionLogData.setVersion(0);

            List<DataVersionLogService> dataVersionLogServices =
                    ContextUtils.getBeansOfTypeWithList(DataVersionLogService.class);
            if (dataVersionLogServices != null && !dataVersionLogServices.isEmpty()) {
                dataVersionLogServices.forEach(service -> service.add(dataVersionLogData));
            }
        }
    }

    public OperationResult processInsert( Insert insertStmt, BoundSql boundSql ) {
        OperationResult result = new OperationResult();
        result.setOperation("insert");
        result.setTableName(insertStmt.getTable().getName());
        result.setRecordStatus(true);
        result.setChangedRecords(
                compareAndGetUpdatedColumnDatas(result.getTableName(), boundSql, insertStmt, null));
        return result;
    }

    public OperationResult processUpdate(
            Update updateStmt,
            MappedStatement mappedStatement,
            BoundSql boundSql,
            Connection connection ) {
        Expression where = updateStmt.getWhere();
        // Select selectStmt = new Select();
        PlainSelect selectBody = new PlainSelect();
        Table table = updateStmt.getTable();
        final Set<String> ignoredColumns = ignoredTableColumns.get(table.getName().toUpperCase());
        if (ignoredColumns != null) {
            if (ignoredColumns.stream().anyMatch("*"::equals)) {
                OperationResult result = new OperationResult();
                result.setOperation("update");
                result.setTableName(table.getName() + ":*");
                result.setRecordStatus(false);
                return result;
            }
        }
        selectBody.setFromItem(table);
        List<Column> updateColumns = new ArrayList<>();
        for (UpdateSet updateSet : updateStmt.getUpdateSets()) {
            updateColumns.addAll(updateSet.getColumns());
        }
        Columns2SelectItemsResult buildColumns2SelectItems =
                buildColumns2SelectItems(table.getName(), updateColumns);
        // selectBody.setSelectItems(buildColumns2SelectItems.getSelectItems());
        // selectBody.setWhere(where);
        // selectStmt.setSelectBody(selectBody);

        // BoundSql boundSql4Select = new BoundSql(
        //         mappedStatement.getConfiguration(),
        //         selectStmt.toString(),
        //         prepareParameterMapping4Select(boundSql.getParameterMappings(), updateStmt),
        //         boundSql.getParameterObject());
        // MetaObject metaObject = SystemMetaObject.forObject(boundSql);
        // @SuppressWarnings("unchecked")
        // Map<String, Object> additionalParameters = (Map<String, Object>)
        // metaObject.getValue("additionalParameters");
        // if (additionalParameters != null && !additionalParameters.isEmpty()) {
        //     for (Map.Entry<String, Object> ety : additionalParameters.entrySet()) {
        //         boundSql4Select.setAdditionalParameter(ety.getKey(), ety.getValue());
        //     }
        // }
        // OriginalDataObj originalData = buildOriginalObjectData(
        //         selectStmt, buildColumns2SelectItems.getPk(), mappedStatement, boundSql4Select,
        // connection);
        // OperationResult result = new OperationResult();
        // result.setOperation("update");
        // result.setTableName(table.getName());
        // result.setRecordStatus(true);
        // result.setChangedRecords(
        //         compareAndGetUpdatedColumnDatas(result.getTableName(), boundSql, updateStmt,
        // originalData));
        // return result;
        return null;
    }

    /**
     * 将update SET部分的jdbc参数去除
     *
     * @param originalMappingList 这里只会包含JdbcParameter参数
     */
    private List<ParameterMapping> prepareParameterMapping4Select(
            List<ParameterMapping> originalMappingList, Update updateStmt ) {
        List<Expression> updateValueExpressions = new ArrayList<>();
        for (UpdateSet updateSet : updateStmt.getUpdateSets()) {
            // updateValueExpressions.addAll(updateSet.getExpressions());
        }
        int removeParamCount = 0;
        for (Expression expression : updateValueExpressions) {
            if (expression instanceof JdbcParameter) {
                ++removeParamCount;
            }
        }
        return originalMappingList.subList(removeParamCount, originalMappingList.size());
    }

    /**
     *
     */
    private List<DataChangedRecord> compareAndGetUpdatedColumnDatas(
            String tableName,
            BoundSql updateSql,
            Statement statement,
            OriginalDataObj originalDataObj ) {
        Map<String, Object> columnNameValMap =
                new HashMap<>(updateSql.getParameterMappings().size());
        List<Column> selectItemsFromUpdateSql = new ArrayList<>();
        if (statement instanceof Update updateStmt) {
            for (UpdateSet updateSet : updateStmt.getUpdateSets()) {
                selectItemsFromUpdateSql.addAll(updateSet.getColumns());
                // final List<Expression> updateList = updateSet.getExpressions();
                // for (int i = 0; i < updateList.size(); ++i) {
                //     Expression updateExps = updateList.get(i);
                //     if (!(updateExps instanceof JdbcParameter)) {
                //         columnNameValMap.put(
                //                 updateSet.getColumns().get(i).getColumnName().toUpperCase(),
                // updateExps.toString());
                //     }
                // }
            }
        } else if (statement instanceof Insert) {
            selectItemsFromUpdateSql.addAll(( (Insert) statement ).getColumns());
        }
        Map<String, String> relatedColumnsUpperCaseWithoutUnderline =
                new HashMap<>(selectItemsFromUpdateSql.size(), 1);
        for (Column item : selectItemsFromUpdateSql) {
            // FIRSTNAME: FIRST_NAME/FIRST-NAME/FIRST$NAME/FIRST.NAME
            relatedColumnsUpperCaseWithoutUnderline.put(
                    item.toString().replaceAll("[._\\-$]", "").toUpperCase(),
                    item.toString().toUpperCase());
        }
        MetaObject metaObject = SystemMetaObject.forObject(updateSql.getParameterObject());

        for (ParameterMapping parameterMapping : updateSql.getParameterMappings()) {
            String propertyName = parameterMapping.getProperty();
            if (propertyName.startsWith("ew.paramNameValuePairs")) {
                continue;
            }
            String[] arr = propertyName.split("\\.");
            String propertyNameTrim = arr[arr.length - 1].replace("_", "").toUpperCase();
            if (relatedColumnsUpperCaseWithoutUnderline.containsKey(propertyNameTrim)) {
                columnNameValMap.put(
                        relatedColumnsUpperCaseWithoutUnderline.get(propertyNameTrim),
                        metaObject.getValue(propertyName));
            }
        }

        final Set<String> ignoredColumns = ignoredTableColumns.get(tableName.toUpperCase());
        if (originalDataObj == null || originalDataObj.isEmpty()) {
            DataChangedRecord oneRecord = new DataChangedRecord();
            List<DataColumnChangeResult> updateColumns = new ArrayList<>(columnNameValMap.size());
            for (Map.Entry<String, Object> ety : columnNameValMap.entrySet()) {
                String columnName = ety.getKey();
                if (( ignoredColumns == null || !ignoredColumns.contains(columnName) )
                        && !ignoreAllColumns.contains(columnName)) {
                    updateColumns.add(
                            DataColumnChangeResult.constrcutByUpdateVal(
                                    columnName, ety.getValue()));
                }
            }
            oneRecord.setUpdatedColumns(updateColumns);
            return Collections.singletonList(oneRecord);
        }
        List<DataChangedRecord> originalDataList = originalDataObj.getOriginalDataObj();
        List<DataChangedRecord> updateDataList = new ArrayList<>(originalDataList.size());
        for (DataChangedRecord originalData : originalDataList) {
            if (originalData.hasUpdate(columnNameValMap, ignoredColumns, ignoreAllColumns)) {
                updateDataList.add(originalData);
            }
        }
        return updateDataList;
    }

    private String buildOriginalData(
            Select selectStmt,
            MappedStatement mappedStatement,
            BoundSql boundSql,
            Connection connection ) {
        try (PreparedStatement statement = connection.prepareStatement(selectStmt.toString())) {
            DefaultParameterHandler parameterHandler =
                    new DefaultParameterHandler(
                            mappedStatement, boundSql.getParameterObject(), boundSql);
            parameterHandler.setParameters(statement);
            ResultSet resultSet = statement.executeQuery();
            final ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            StringBuilder sb = new StringBuilder("[");
            while (resultSet.next()) {
                sb.append("{");
                for (int i = 1; i <= columnCount; ++i) {
                    sb.append("\"").append(metaData.getColumnName(i)).append("\":\"");
                    Object res = resultSet.getObject(i);
                    if (res instanceof Clob) {
                        sb.append(DataColumnChangeResult.convertClob((Clob) res));
                    } else {
                        sb.append(res);
                    }
                    sb.append("\",");
                }
                sb.replace(sb.length() - 1, sb.length(), "}");
            }
            sb.append("]");
            resultSet.close();
            return sb.toString();
        } catch (Exception e) {
            LogUtils.error("try to get record tobe deleted for selectStmt={}", selectStmt, e);
            return "failed to get original data";
        }
    }

    /**
     * 构建原始对象数据
     */
    private OriginalDataObj buildOriginalObjectData(
            Select selectStmt,
            Column pk,
            MappedStatement mappedStatement,
            BoundSql boundSql,
            Connection connection ) {
        try (PreparedStatement statement = connection.prepareStatement(selectStmt.toString())) {

            DefaultParameterHandler parameterHandler =
                    new DefaultParameterHandler(
                            mappedStatement, boundSql.getParameterObject(), boundSql);
            parameterHandler.setParameters(statement);

            ResultSet resultSet = statement.executeQuery();
            List<DataChangedRecord> originalObjectDatas = new LinkedList<>();
            while (resultSet.next()) {
                originalObjectDatas.add(prepareOriginalDataObj(resultSet, pk));
            }
            OriginalDataObj result = new OriginalDataObj();
            result.setOriginalDataObj(originalObjectDatas);
            resultSet.close();
            return result;
        } catch (Exception e) {
            LogUtils.error("try to get record tobe updated for selectStmt={}", selectStmt, e);
            return new OriginalDataObj();
        }
    }

    /**
     * 获取记录：在数据库中包含具有原始数据的相关列
     */
    private DataChangedRecord prepareOriginalDataObj( ResultSet resultSet, Column pk )
            throws SQLException {
        final ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<DataColumnChangeResult> originalColumnDatas = new LinkedList<>();
        DataColumnChangeResult pkval = null;
        for (int i = 1; i <= columnCount; ++i) {
            String columnName = metaData.getColumnName(i).toUpperCase();
            DataColumnChangeResult col =
                    DataColumnChangeResult.constrcutByOriginalVal(
                            columnName, resultSet.getObject(i));
            if (pk != null && columnName.equalsIgnoreCase(pk.getColumnName())) {
                pkval = col;
            } else {
                originalColumnDatas.add(col);
            }
        }
        DataChangedRecord changedRecord = new DataChangedRecord();
        changedRecord.setOriginalColumns(originalColumnDatas);
        if (pkval != null) {
            changedRecord.setPkColumnName(pkval.getColumnName());
            changedRecord.setPkColumnVal(pkval.getOriginalValue());
        }
        return changedRecord;
    }

    private Columns2SelectItemsResult buildColumns2SelectItems(
            String tableName, List<Column> columns ) {
        if (columns == null || columns.isEmpty()) {
            // return Columns2SelectItemsResult.build(Collections.singletonList(new AllColumns()),
            // 0);
        }
        List<SelectItem> selectItems = new ArrayList<>(columns.size());
        for (Column column : columns) {
            // selectItems.add(new SelectExpressionItem(column));
        }
        for (TableInfo tableInfo : TableInfoHelper.getTableInfos()) {
            // if (tableName.equalsIgnoreCase(tableInfo.getTableName())) {
            //     Column pk = new Column(tableInfo.getKeyColumn());
            //     selectItems.add(new SelectExpressionItem(pk));
            //     Columns2SelectItemsResult result = Columns2SelectItemsResult.build(selectItems,
            // 1);
            //     result.setPk(pk);
            //     return result;
            // }
        }
        return Columns2SelectItemsResult.build(selectItems, 0);
    }

    public OperationResult processDelete(
            Delete deleteStmt,
            MappedStatement mappedStatement,
            BoundSql boundSql,
            Connection connection ) {
        Table table = deleteStmt.getTable();
        Expression where = deleteStmt.getWhere();
        // Select selectStmt = new Select();
        // PlainSelect selectBody = new PlainSelect();
        // selectBody.setFromItem(table);
        // selectBody.setSelectItems(Collections.singletonList(new AllColumns()));
        // selectBody.setWhere(where);
        // selectStmt.setSelectBody(selectBody);
        // String originalData = buildOriginalData(selectStmt, mappedStatement, boundSql,
        // connection);
        // OperationResult result = new OperationResult();
        // result.setOperation("delete");
        // result.setTableName(table.getName());
        // result.setRecordStatus(originalData.startsWith("["));
        // result.setChangedData(originalData);
        // return result;
        return null;
    }

    /**
     * ignoredColumns = TABLE_NAME1.COLUMN1,COLUMN2; TABLE2.COLUMN1,COLUMN2; TABLE3.*; *.COLUMN1,COLUMN2 多个表用分号分隔
     * TABLE_NAME1.COLUMN1,COLUMN2 : 表示忽略这个表的这2个字段 TABLE3.*: 表示忽略这张表的INSERT/UPDATE，delete暂时还保留
     * *.COLUMN1,COLUMN2:表示所有表的这个2个字段名都忽略
     */
    @Override
    public void setProperties( Properties properties ) {
        String ignoredTableColumns = properties.getProperty("ignoredTableColumns");
        if (ignoredTableColumns == null || ignoredTableColumns.trim().isEmpty()) {
            return;
        }
        String[] array = ignoredTableColumns.split(";");
        for (String table : array) {
            int index = table.indexOf(".");
            if (index == -1) {
                LogUtils.warn(
                        "invalid data={} for ignoredColumns, format should be TABLE_NAME1.COLUMN1,COLUMN2; TABLE2.COLUMN1,COLUMN2;",
                        table);
                continue;
            }
            String tableName = table.substring(0, index).trim().toUpperCase();
            String[] columnArray = table.substring(index + 1).split(",");
            Set<String> columnSet = new HashSet<>(columnArray.length);
            for (String column : columnArray) {
                column = column.trim().toUpperCase();
                if (column.isEmpty()) {
                    continue;
                }
                columnSet.add(column);
            }
            if ("*".equals(tableName)) {
                ignoreAllColumns.addAll(columnSet);
            } else {
                this.ignoredTableColumns.put(tableName, columnSet);
            }
        }
    }

    /**
     * 操作结果
     */
    public static class OperationResult {

        /**
         * sql类型
         */
        private String operation;

        /**
         * 记录状态
         */
        private boolean recordStatus;

        /**
         * 表名称
         */
        private String tableName;

        @Deprecated
        private String changedData;

        /**
         * 数据变动记录
         */
        private List<DataChangedRecord> changedRecords;

        private DataVersionLog annotation;

        /**
         * 耗时(毫秒)
         */
        private long cost;

        public String getOperation() {
            return operation;
        }

        public void setOperation( String operation ) {
            this.operation = operation;
        }

        public boolean isRecordStatus() {
            return recordStatus;
        }

        public void setRecordStatus( boolean recordStatus ) {
            this.recordStatus = recordStatus;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName( String tableName ) {
            this.tableName = tableName;
        }

        public String getChangedData() {
            return changedData;
        }

        public void setChangedData( String changedData ) {
            this.changedData = changedData;
        }

        public List<DataChangedRecord> getChangedRecords() {
            return changedRecords;
        }

        public void setChangedRecords( List<DataChangedRecord> changedRecords ) {
            this.changedRecords = changedRecords;
        }

        public DataVersionLog getAnnotation() {
            return annotation;
        }

        public void setAnnotation( DataVersionLog annotation ) {
            this.annotation = annotation;
        }

        public long getCost() {
            return cost;
        }

        public void setCost( long cost ) {
            this.cost = cost;
        }
    }

    /**
     * Columns2SelectItemsResult
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static class Columns2SelectItemsResult {

        private Column pk;

        /**
         * all column with additional columns: ID, etc.
         */
        private List<SelectItem> selectItems;

        /**
         * newly added column count from meta data.
         */
        private int additionalItemCount;

        public Column getPk() {
            return pk;
        }

        public void setPk( Column pk ) {
            this.pk = pk;
        }

        public List<SelectItem> getSelectItems() {
            return selectItems;
        }

        public void setSelectItems( List<SelectItem> selectItems ) {
            this.selectItems = selectItems;
        }

        public int getAdditionalItemCount() {
            return additionalItemCount;
        }

        public void setAdditionalItemCount( int additionalItemCount ) {
            this.additionalItemCount = additionalItemCount;
        }

        public static Columns2SelectItemsResult build(
                List<SelectItem> selectItems, int additionalItemCount ) {
            Columns2SelectItemsResult result = new Columns2SelectItemsResult();
            result.setSelectItems(selectItems);
            result.setAdditionalItemCount(additionalItemCount);
            return result;
        }
    }

    /**
     * OriginalDataObj
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static class OriginalDataObj {

        private List<DataChangedRecord> originalDataObj;

        public List<DataChangedRecord> getOriginalDataObj() {
            return originalDataObj;
        }

        public void setOriginalDataObj( List<DataChangedRecord> originalDataObj ) {
            this.originalDataObj = originalDataObj;
        }

        public boolean isEmpty() {
            return originalDataObj == null || originalDataObj.isEmpty();
        }
    }

    /**
     * 数据列更改结果
     */
    public static class DataColumnChangeResult {

        /**
         * 列名称
         */
        private String columnName;

        /**
         * 原始值
         */
        private Object originalValue;

        /**
         * 更新后的值
         */
        private Object updateValue;

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName( String columnName ) {
            this.columnName = columnName;
        }

        public Object getOriginalValue() {
            return originalValue;
        }

        public void setOriginalValue( Object originalValue ) {
            this.originalValue = originalValue;
        }

        public Object getUpdateValue() {
            return updateValue;
        }

        public void setUpdateValue( Object updateValue ) {
            this.updateValue = updateValue;
        }

        @SuppressWarnings("rawtypes")
        public boolean isDataChanged( Object updateValue ) {
            if (!Objects.equals(originalValue, updateValue)) {
                if (updateValue instanceof Number && originalValue instanceof Number) {
                    BigDecimal update = new BigDecimal(updateValue.toString());
                    BigDecimal original = new BigDecimal(originalValue.toString());
                    return update.compareTo(original) != 0;
                }
                if (updateValue instanceof Date && originalValue instanceof Date) {
                    Date update = (Date) updateValue;
                    Date original = (Date) originalValue;
                    return update.compareTo(original) != 0;
                }
                if (originalValue instanceof Clob) {
                    String originalStr = convertClob((Clob) originalValue);
                    setOriginalValue(originalStr);
                    return !originalStr.equals(updateValue);
                }
                return true;
            }
            if (originalValue instanceof Comparable original) {
                Comparable update = (Comparable) updateValue;
                return original.compareTo(update) != 0;
            }
            return false;
        }

        public static String convertClob( Clob clobObj ) {
            try {
                return clobObj.getSubString(0, (int) clobObj.length());
            } catch (Exception e) {
                try (Reader is = clobObj.getCharacterStream()) {
                    char[] chars = new char[64];
                    int readChars;
                    StringBuilder sb = new StringBuilder();
                    while (( readChars = is.read(chars) ) != -1) {
                        sb.append(chars, 0, readChars);
                    }
                    return sb.toString();
                } catch (Exception e2) {
                    // ignored
                    return "unknown clobObj";
                }
            }
        }

        public static DataColumnChangeResult constrcutByUpdateVal(
                String columnName, Object updateValue ) {
            DataColumnChangeResult res = new DataColumnChangeResult();
            res.setColumnName(columnName);
            res.setUpdateValue(updateValue);
            return res;
        }

        public static DataColumnChangeResult constrcutByOriginalVal(
                String columnName, Object originalValue ) {
            DataColumnChangeResult res = new DataColumnChangeResult();
            res.setColumnName(columnName);
            res.setOriginalValue(originalValue);
            return res;
        }

        public String generateDataStr() {
            StringBuilder sb = new StringBuilder();
            sb.append("\"")
                    .append(columnName)
                    .append("\"")
                    .append(":")
                    .append("\"")
                    .append(convertDoubleQuotes(originalValue))
                    .append("->")
                    .append(convertDoubleQuotes(updateValue))
                    .append("\"")
                    .append(",");
            return sb.toString();
        }

        public String convertDoubleQuotes( Object obj ) {
            if (obj == null) {
                return null;
            }
            return obj.toString().replace("\"", "\\\"");
        }
    }

    /**
     * 数据变动记录
     */
    public static class DataChangedRecord {

        /**
         * 主键名称
         */
        private String pkColumnName;

        /**
         * 主键值
         */
        private Object pkColumnVal;

        /**
         * 原始的数据列
         */
        private List<DataColumnChangeResult> originalColumns;

        /**
         * 更改的数据列
         */
        private List<DataColumnChangeResult> updatedColumns;

        public String getPkColumnName() {
            return pkColumnName;
        }

        public void setPkColumnName( String pkColumnName ) {
            this.pkColumnName = pkColumnName;
        }

        public Object getPkColumnVal() {
            return pkColumnVal;
        }

        public void setPkColumnVal( Object pkColumnVal ) {
            this.pkColumnVal = pkColumnVal;
        }

        public List<DataColumnChangeResult> getOriginalColumns() {
            return originalColumns;
        }

        public void setOriginalColumns( List<DataColumnChangeResult> originalColumns ) {
            this.originalColumns = originalColumns;
        }

        public List<DataColumnChangeResult> getUpdatedColumns() {
            return updatedColumns;
        }

        public void setUpdatedColumns( List<DataColumnChangeResult> updatedColumns ) {
            this.updatedColumns = updatedColumns;
        }

        public boolean hasUpdate(
                Map<String, Object> columnNameValMap,
                Set<String> ignoredColumns,
                Set<String> ignoreAllColumns ) {
            if (originalColumns == null) {
                return true;
            }
            boolean hasUpdate = false;
            updatedColumns = new ArrayList<>(originalColumns.size());
            for (DataColumnChangeResult originalColumn : originalColumns) {
                final String columnName = originalColumn.getColumnName().toUpperCase();
                if (ignoredColumns != null && ignoredColumns.contains(columnName)
                        || ignoreAllColumns.contains(columnName)) {
                    continue;
                }
                Object updatedValue = columnNameValMap.get(columnName);
                if (originalColumn.isDataChanged(updatedValue)) {
                    hasUpdate = true;
                    originalColumn.setUpdateValue(updatedValue);
                    updatedColumns.add(originalColumn);
                }
            }
            return hasUpdate;
        }

        public String generateUpdatedDataStr() {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            if (pkColumnName != null) {
                sb.append("\"")
                        .append(pkColumnName)
                        .append("\"")
                        .append(":")
                        .append("\"")
                        .append(convertDoubleQuotes(pkColumnVal))
                        .append("\"")
                        .append(",");
            }
            for (DataColumnChangeResult update : updatedColumns) {
                sb.append(update.generateDataStr());
            }
            sb.replace(sb.length() - 1, sb.length(), "}");
            return sb.toString();
        }

        public String convertDoubleQuotes( Object obj ) {
            if (obj == null) {
                return null;
            }
            return obj.toString().replace("\"", "\\\"");
        }
    }
}
