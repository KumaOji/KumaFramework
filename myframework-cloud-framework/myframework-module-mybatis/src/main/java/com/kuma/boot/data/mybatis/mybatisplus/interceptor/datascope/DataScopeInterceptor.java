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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.kuma.boot.common.holder.UserContextHolder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

/**
 * Mybatis 拦截器 主要用于数据权限拦截
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-04 07:40:24
 */
public class DataScopeInterceptor implements InnerInterceptor {

    public DataScopeInterceptor() {}

    /**
     * 操作前置处理 改改sql啥的
     * @param sh 可能是代理对象
     * @param connection 连接
     * @param transactionTimeout 事务超时时间
     */
    @Override
    public void beforePrepare(
            StatementHandler sh, Connection connection, Integer transactionTimeout) {
        InnerInterceptor.super.beforePrepare(sh, connection, transactionTimeout);
    }

    /**
     * 操作前置处理 改改sql啥的
     * @param executor
     * @param ms
     * @param parameter
     * @throws SQLException
     */
    @Override
    public void beforeUpdate(Executor executor, MappedStatement ms, Object parameter)
            throws SQLException {
        InnerInterceptor.super.beforeUpdate(executor, ms, parameter);
    }

    /**
     * 判断是否执行 如果不执行update操作,则影响行数的值为
     * @param executor
     * @param ms
     * @param parameter
     * @return
     * @throws SQLException
     */
    @Override
    public boolean willDoUpdate(Executor executor, MappedStatement ms, Object parameter)
            throws SQLException {
        return InnerInterceptor.super.willDoUpdate(executor, ms, parameter);
    }

    /**
     *  如果不执行query操作,则返回 {@link Collections#emptyList()}
     * @param executor
     * @param ms
     * @param parameter
     * @param rowBounds
     * @param resultHandler
     * @param boundSql
     * @return
     * @throws SQLException
     */
    @Override
    public boolean willDoQuery(
            Executor executor,
            MappedStatement ms,
            Object parameter,
            RowBounds rowBounds,
            ResultHandler resultHandler,
            BoundSql boundSql)
            throws SQLException {
        return InnerInterceptor.super.willDoQuery(
                executor, ms, parameter, rowBounds, resultHandler, boundSql);
    }

    @Override
    public void beforeGetBoundSql(StatementHandler sh) {
        InnerInterceptor.super.beforeGetBoundSql(sh);
    }

    @Override
    public void beforeQuery(
            Executor executor,
            MappedStatement ms,
            Object parameter,
            RowBounds rowBounds,
            ResultHandler resultHandler,
            BoundSql boundSql) {
        com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.DataScope dataScope = findDataScope(parameter).orElse(null);
        if (dataScope == null) {
            return;
        }

        String originalSql = boundSql.getSql();

        String scopeName = dataScope.getScopeName();
        String selfScopeName = dataScope.getSelfScopeName();
        Long userId =
                dataScope.getUserId() == null
                        ? UserContextHolder.getUserId()
                        : dataScope.getUserId();
        List<Long> orgIds = dataScope.getOrgIds();
        com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.DataScopeTypeEnum dsType = com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.DataScopeTypeEnum.SELF;
        if (CollUtil.isEmpty(orgIds)) {
            // 查询当前用户的 角色 最小权限
            // userId

            // dsType orgIds
            // Map<String, Object> result = function.apply(userId);
            Map<String, Object> result = null;
            if (result == null) {
                return;
            }

            Integer type = (Integer) result.get("dsType");
            dsType = com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.DataScopeTypeEnum.get(type);
            orgIds = (List<Long>) result.get("orgIds");
        }

        // 查全部
        if (com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.DataScopeTypeEnum.ALL.eq(dsType)) {
            return;
        }
        // 查个人
        if (com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.DataScopeTypeEnum.SELF.eq(dsType)) {
            originalSql =
                    "select * from ("
                            + originalSql
                            + ") temp_data_scope where temp_data_scope."
                            + selfScopeName
                            + " = "
                            + userId;
        }
        // 查其他
        else if (StrUtil.isNotBlank(scopeName) && CollUtil.isNotEmpty(orgIds)) {
            String join = CollUtil.join(orgIds, ",");
            originalSql =
                    "select * from ("
                            + originalSql
                            + ") temp_data_scope where temp_data_scope."
                            + scopeName
                            + " in ("
                            + join
                            + ")";
        }

        PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
        mpBoundSql.sql(originalSql);
    }

    //	@Override
    //    public Object intercept(Invocation invocation) throws Throwable {
    //        if (LogUtil.isInfoEnabled()) {
    //	        LogUtil.debug("进入 PrepareInterceptor 拦截器...");
    //        }
    //
    //        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
    //        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
    //        this.sqlParser(metaObject);
    //        // 先判断是不是SELECT操作 不是直接过滤
    //        MappedStatement mappedStatement = (MappedStatement)
    // metaObject.getValue("delegate.mappedStatement");
    //        if (!SqlCommandType.SELECT.equals(mappedStatement.getSqlCommandType())) {
    //            return invocation.proceed();
    //        }
    //        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
    //        // 执行的SQL语句
    //        String originalSql = boundSql.getSql();
    //        // SQL语句的参数
    //        Object parameterObject = boundSql.getParameterObject();
    //
    //        //查找参数中包含DataScope类型的参数
    //        DataScope dataScope = findDataScopeObject(parameterObject);
    //        if (ObjectUtil.isNull(dataScope)) {
    //            return invocation.proceed();
    //        }
    //
    //        String scopeFiledName = dataScope.getScopeFiledName();
    //        List<Integer> deptIds = dataScope.getDeptIds();
    //        // 优先获取赋值数据
    //        if (CollUtil.isEmpty(deptIds)) {
    //            SecurityUser user = SecurityUtil.getUser();
    //            if (user == null) {
    //                throw new CheckedException("auto datascope, set up security details true");
    //            }
    //
    //            // 解析角色Id
    //            List<String> roleIdList = user.getAuthorities()
    //                    .stream().map(GrantedAuthority::getAuthority)
    //                    .filter(authority -> authority.startsWith("ROLE_"))
    //                    .map(authority -> authority.split("_")[1])
    //                    .toList();
    //
    //            // 通过角色Id查询范围权限
    //            Entity query = Db.use(dataSource)
    //                    .query("SELECT * FROM sys_role where role_id IN (" +
    // CollUtil.join(roleIdList, ",") + ")")
    //                    .stream().min(Comparator.comparingInt(o -> o.getInt("ds_type"))).get();
    //
    //            // 数据库权限范围字段
    //            Integer dsType = query.getInt("ds_type");
    //            // 查询全部
    //            if (DataScopeTypeEnum.ALL.getType() == dsType) {
    //                return invocation.proceed();
    //            }
    //
    //            // 除了全部 则要获取自定义 本级及其下级 查询本级
    //            String dsScope = query.getStr("ds_scope");
    //
    //            deptIds.addAll(Arrays.stream(dsScope.split(","))
    //                    .map(Integer::parseInt).toList());
    //
    //            String join = CollectionUtil.join(deptIds, ",");
    //            originalSql = "select * from (" + originalSql + ") temp_data_scope where
    // temp_data_scope." + scopeFiledName + " in (" + join + ")";
    //            metaObject.setValue("delegate.boundSql.sql", originalSql);
    //
    //            return invocation.proceed();
    //        }
    //        return invocation.proceed();
    //    }
    //

    //    /**
    //     * 生成拦截对象的代理
    //     *
    //     * @param target 目标对象
    //     * @return 代理对象
    //     */
    //    @Override
    //    public Object plugin(Object target) {
    //        if (target instanceof StatementHandler) {
    //            return Plugin.wrap(target, this);
    //        }
    //        return target;
    //    }

    /**
     * mybatis配置的属性
     *
     * @param properties mybatis配置的属性
     */
    @Override
    public void setProperties(Properties properties) {}

    /**
     * 查找参数是否包括DataScope对象
     *
     * @param parameterObj 参数列表
     * @return DataScope
     */
    private com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.DataScope findDataScopeObject(Object parameterObj) {
        if (parameterObj instanceof com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.DataScope) {
            return (com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.DataScope) parameterObj;
        } else if (parameterObj instanceof Map) {
            for (Object val : ((Map<?, ?>) parameterObj).values()) {
                if (val instanceof com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.DataScope) {
                    return (com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.DataScope) val;
                }
            }
        }
        return null;
    }

    /**
     * 查找参数是否包括DataScope对象
     *
     * @param parameterObj 参数列表
     * @return DataScope
     */
    protected Optional<com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.DataScope> findDataScope(Object parameterObj) {
        if (parameterObj == null) {
            return Optional.empty();
        }
        if (parameterObj instanceof com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.DataScope) {
            return Optional.of((com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.DataScope) parameterObj);
        } else if (parameterObj instanceof Map) {
            for (Object val : ((Map<?, ?>) parameterObj).values()) {
                if (val instanceof com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.DataScope) {
                    return Optional.of((com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.DataScope) val);
                }
            }
        }
        return Optional.empty();
    }
}
