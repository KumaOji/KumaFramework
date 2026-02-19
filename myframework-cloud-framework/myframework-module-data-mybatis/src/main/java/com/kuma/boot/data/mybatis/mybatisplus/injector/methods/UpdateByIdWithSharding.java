/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.data.mybatis.mybatisplus.injector.methods;

import static java.util.stream.Collectors.joining;

import com.baomidou.mybatisplus.core.injector.methods.UpdateById;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import java.util.List;
import java.util.Objects;
import org.apache.ibatis.mapping.MappedStatement;

// 1.如果想编辑一个mybatisplus已有sql，比如分库分表系统，执行updateById操作时，虽然主键Id已确定，但目标表不确定，此时可能导致该sql在多张表上执行，造成资源浪费，并且分库分表字段不可修改，默认的updateById不能用，需要改造。以下以shardingsphere分库分表为例。
//
// 2.定义一个UpdateByIdWithSharding类，继承UpdateById类

//// 需注入到spring容器中
// @Component
// public class GyhSqlInjector extends DefaultSqlInjector {
//    /**
//     * shardingsphere 配置信息
//     */
//    @Autowired
//    private YamlShardingRuleConfiguration yamlShardingRuleConfiguration;
//
//    @Override
//    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
//        List<AbstractMethod> methodList = super.getMethodList(mapperClass);
//        // 添加 UpdateByIdWithSharding 对象，并注入分库分表信息
//        methodList.add(new UpdateByIdWithSharding(yamlShardingRuleConfiguration));
//        return methodList;
//    }
// }
/// **
// * 自定义的通用Mapper
// */
// public interface GyhBaseMapper<T> extends BaseMapper<T> {
//   int updateById(@Param(Constants.ENTITY) T entity);
// }
public class UpdateByIdWithSharding extends UpdateById {
    private String columnDot = "`";

    //	private YamlShardingRuleConfiguration yamlShardingRuleConfiguration;
    //
    //	// 注入shardingsphere的分库分表配置信息
    //	public UpdateByIdWithSharding(YamlShardingRuleConfiguration yamlShardingRuleConfiguration) {
    //		this.yamlShardingRuleConfiguration = yamlShardingRuleConfiguration;
    //	}

    @Override
    public MappedStatement injectMappedStatement(
            Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String tableName = tableInfo.getTableName();
        // shardingsphere 分库分表配置信息
        //		Map<String, YamlTableRuleConfiguration> tables =
        // yamlShardingRuleConfiguration.getTables();
        // 判断当前表是否设置了分表字段
        //		if (tables.containsKey(tableName)) {
        //			YamlTableRuleConfiguration tableRuleConfiguration = tables.get(tableName);
        //			// 获取分表字段
        //			String shardingColumn =
        // tableRuleConfiguration.getTableStrategy().getStandard().getShardingColumn();
        //			// 构建sql
        //			boolean logicDelete = tableInfo.isLogicDelete();
        //			SqlMethod sqlMethod = SqlMethod.UPDATE_BY_ID;
        //			// 增加分表字段判断
        //			String shardingAdditional = getShardingColumnWhere(tableInfo, shardingColumn);
        //			// 是否判断逻辑删除字段
        //			final String additional = optlockVersion() + tableInfo.getLogicDeleteSql(true, false);
        //			shardingAdditional = shardingAdditional + additional;
        //			String sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(),
        //				getSqlSet(logicDelete, tableInfo, shardingColumn),
        //				tableInfo.getKeyColumn(), ENTITY_DOT + tableInfo.getKeyProperty(),
        //				shardingAdditional);
        //			SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        //			return addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(),
        // sqlSource);
        //		} else {
        //			return super.injectMappedStatement(mapperClass, modelClass, tableInfo);
        //		}
        return null;
    }

    /**
     * where条件增加分表字段
     */
    private String getShardingColumnWhere(TableInfo tableInfo, String shardingColumn) {
        StringBuilder shardingWhere = new StringBuilder();
        shardingWhere.append(" AND ").append(shardingColumn).append("=#{");
        shardingWhere.append(ENTITY_DOT);
        TableFieldInfo fieldInfo =
                tableInfo.getFieldList().stream()
                        .filter(
                                f ->
                                        f.getColumn()
                                                .replaceAll(columnDot, EMPTY)
                                                .equals(shardingColumn))
                        .findFirst()
                        .get();
        shardingWhere.append(fieldInfo.getEl());
        shardingWhere.append("}");
        return shardingWhere.toString();
    }

    /**
     * set模块去掉分表字段
     */
    public String getSqlSet(
            boolean ignoreLogicDelFiled, TableInfo tableInfo, String shardingColumn) {
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        // 去掉分表字段的set设置，即不修改分表字段
        return fieldList.stream()
                .filter(
                        i ->
                                !ignoreLogicDelFiled
                                        || !(tableInfo.isWithLogicDelete() && i.isLogicDelete()))
                .filter(i -> !i.getColumn().equals(shardingColumn))
                .map(i -> i.getSqlSet(ENTITY_DOT))
                .filter(Objects::nonNull)
                .collect(joining(NEWLINE));
    }
}
