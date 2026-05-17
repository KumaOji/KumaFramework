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

package com.kuma.boot.data.mybatis.mybatisplus;

import static com.kuma.boot.common.model.result.PageResult.of;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import com.baomidou.mybatisplus.core.toolkit.support.LambdaMeta;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kuma.boot.common.model.request.BasePageQuery;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.boot.common.model.result.PageResult;
import com.kuma.boot.common.utils.common.AntiSqlFilterUtils;
import com.kuma.boot.core.utils.context.ContextUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;

/**
 * MP工具类
 *
 * @author kuma
 * @version 1.0.0
 * @since 2022/03/24 14:18
 */
public class MpUtils {

    /**
     * 默认每次处理1000条
     */
    private static final int BATCH_SIZE = 1000;

    private static final String MYSQL_ESCAPE_CHARACTER = "`";

    /**
     * 批量处理修改或者插入
     *
     * @param data        处理的数据
     * @param mapperClass mapper类
     * @param function    function 处理逻辑
     * @return 影响的总行数
     * @since 2022-03-24 14:29:12
     */
    public static <T, M, R> int batchUpdateOrInsert(
            List<T> data, Class<M> mapperClass, BiFunction<T, M, R> function) {
        int i = 1;
        SqlSessionFactory sqlSessionFactory = ContextUtils.getBean(SqlSessionFactory.class, true);
        if (Objects.isNull(sqlSessionFactory)) {
            throw new MybatisPlusException("未获取到sqlSession");
        }

        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        try {
            M mapper = sqlSession.getMapper(mapperClass);
            int size = data.size();
            for (T element : data) {
                function.apply(element, mapper);
                if ((i % BATCH_SIZE == 0) || i == size) {
                    sqlSession.flushStatements();
                }
                i++;
            }
        } catch (Exception e) {
            sqlSession.rollback();
            LogUtils.error(e);
        } finally {
            sqlSession.close();
        }

        return i - 1;
    }

    /**
     * 获取的一条数据, 有多条取第一条
     */
    public static <T> Optional<T> findOne(LambdaQueryChainWrapper<T> lambdaQuery) {
        Page<T> mpPage = new Page<>(0, 1);
        Page<T> page = lambdaQuery.page(mpPage);
        // 关闭 count 查询
        page.setSearchCount(false);
        if (CollUtil.isNotEmpty(page.getRecords())) {
            return Optional.of(page.getRecords().get(0));
        }
        return Optional.empty();
    }

    /**
     * 获取关联的 TableInfo
     */
    public static TableInfo getTableInfo(String tableName) {
        for (TableInfo tableInfo : TableInfoHelper.getTableInfos()) {
            if (tableName.equalsIgnoreCase(tableInfo.getTableName())) {
                return tableInfo;
            }
        }
        return null;
    }

    /**
     * 将拦截器添加到链中
     *
     * @param interceptor 链
     * @param inner       拦截器
     * @param index       位置
     */
    public static void addInterceptor(
            MybatisPlusInterceptor interceptor, InnerInterceptor inner, int index) {
        List<InnerInterceptor> inners = new ArrayList<>(interceptor.getInterceptors());
        inners.add(index, inner);
        interceptor.setInterceptors(inners);
    }

    /**
     * 获得 Table 对应的表名
     *
     * <p>兼容 MySQL 转义表名 `t_xxx`
     *
     * @param table 表
     * @return 去除转移字符后的表名
     */
    public static String getTableName(Table table) {
        String tableName = table.getName();
        if (tableName.startsWith(MYSQL_ESCAPE_CHARACTER)
                && tableName.endsWith(MYSQL_ESCAPE_CHARACTER)) {
            tableName = tableName.substring(1, tableName.length() - 1);
        }
        return tableName;
    }

    /**
     * 构建 Column 对象
     *
     * @param tableName  表名
     * @param tableAlias 别名
     * @param column     字段名
     * @return Column 对象
     */
    public static Column buildColumn(String tableName, Alias tableAlias, String column) {
        return new Column(tableAlias != null ? tableAlias.getName() + "." + column : column);
    }

    /**
     * 获取行名称
     *
     * @param function 对象字段对应的读取方法的Lambda表达式
     * @return 字段名
     */
    public static <T> String getColumnName(SFunction<T, ?> function) {
        LambdaMeta meta = LambdaUtils.extract(function);
        Map<String, ColumnCache> columnMap = LambdaUtils.getColumnMap(meta.getInstantiatedClass());
        Assert.notEmpty(columnMap, "错误:无法执行.因为无法获取到实体类的表对应缓存!");
        String fieldName = PropertyNamer.methodToProperty(meta.getImplMethodName());
        ColumnCache columnCache = columnMap.get(LambdaUtils.formatKey(fieldName));
        return columnCache.getColumn();
    }

    /**
     * 获取行名称
     *
     * @param readMethod 对象字段对应的读取方法对象
     * @param clazz      实体类类型. 辅助进行判断, 传多个只有第一个生效，可以为空, 为空时使用读取方法对应的Class类，
     * @return 字段名
     */
    public static <T> String getColumnName(Method readMethod, Class<T>... clazz) {
        Class<?> beanClass;
        if (ArrayUtil.isNotEmpty(clazz)) {
            beanClass = clazz[0];
        } else {
            beanClass = readMethod.getDeclaringClass();
        }

        Map<String, ColumnCache> columnMap = LambdaUtils.getColumnMap(beanClass);
        Assert.notEmpty(columnMap, "错误:无法执行.因为无法获取到实体类的表对应缓存!");
        String fieldName = PropertyNamer.methodToProperty(readMethod.getName());
        ColumnCache columnCache = columnMap.get(LambdaUtils.formatKey(fieldName));
        return columnCache.getColumn();
    }

    /**
     * 批量执行语句, 通常用于for循环方式的批量插入
     */
    public static <T> void executeBatch(
            List<T> saveList, Consumer<List<T>> consumer, int batchSize) {
        // 开始游标
        int start = 0;
        // 结束游标
        int end = Math.min(batchSize, saveList.size());
        while (start < end) {
            List<T> list = ListUtil.sub(saveList, start, end);
            start = end;
            end = Math.min(end + batchSize, saveList.size());
            consumer.accept(list);
        }
    }

    /**
     * 支持多个字段排序，用法： eg.1, 参数：{order:"name,id", order:"desc,asc" }。 排序： name desc, id asc eg.2,
     * 参数：{order:"name", order:"desc,asc" }。 排序： name desc eg.3, 参数：{order:"name,id", order:"desc"
     * }。 排序： name desc
     *
     * @return {@link IPage }
     * @since 2021-09-02 21:19:05
     */
    @JsonIgnore
    public static <T, QueryDTO> IPage<T> buildMpPage(BasePageQuery<QueryDTO> params) {
        Page<T> page = new Page<>(params.getCurrentPage(), params.getPageSize());

        // 没有排序参数
        if (CollUtil.isEmpty(params.getSortQuery())) {
            return page;
        }

        List<OrderItem> orders = new ArrayList<>();
        params.getSortQuery()
                .forEach(
                        sortDTO -> {
                            String filed = sortDTO.getFiled();
                            String order = sortDTO.getOrder();
                            // 驼峰转下划线
                            String underlineSort = StrUtil.toUnderlineCase(filed);
                            // 除了 createTime 和 updateTime 都过滤sql关键字
                            if (!StrUtil.equalsAny(filed, "createTime", "updateTime")) {
                                underlineSort = AntiSqlFilterUtils.getSafeValue(underlineSort);
                            }

                            if (StrUtil.equalsAny(order, "asc")) {
                                orders.add(OrderItem.asc(underlineSort));
                            } else {
                                orders.add(OrderItem.desc(underlineSort));
                            }
                        });
        page.setOrders(orders);

        return page;
    }

    /**
     * 构造mp分页参数
     *
     * @return 分页参数
     * @since 2022/3/14 13:50
     */
    public static <T extends PageQuery, R> IPage<R> buildMpPage(T pageParam) {
        Page<R> page = new Page<>(pageParam.getCurrentPage(), pageParam.getPageSize());

        List<OrderItem> orders = new ArrayList<>();
        orders.add(OrderItem.desc("create_time"));

        if (StringUtils.isNotBlank(pageParam.getSort())
                && StringUtils.isNotBlank(pageParam.getOrder())) {
            OrderItem orderItem =
                    "asc".equals(pageParam.getOrder())
                            ? OrderItem.asc(pageParam.getSort())
                            : OrderItem.desc(pageParam.getSort());
            orders.add(orderItem);
        }

        page.setOrders(orders);

        return page;
    }

    /**
     * 基于function转换mybatis分页数据
     *
     * @param page page
     * @param <R>  R
     * @return {@link PageResult }
     * @since 2021-09-02 19:10:49
     */
    public static <R, T> PageResult<R> convertMybatisPage(IPage<T> page, Function<T, R> function) {
        List<T> records = page.getRecords();
        List<R> collect =
                Optional.ofNullable(records).orElse(new ArrayList<>()).stream()
                        .filter(Objects::nonNull)
                        .map(function)
                        .toList();

        return of(
                page.getTotal(),
                (int) page.getPages(),
                (int) page.getCurrent(),
                (int) page.getSize(),
                collect);
    }

    /**
     * 基于BeanUtil.toBean转换mybatis分页数据
     *
     * @param page page
     * @param <R>  R
     * @return {@link PageResult }
     * @since 2021-09-02 19:10:49
     */
    public static <R, T> PageResult<R> convertMybatisPage(IPage<T> page, Class<R> r) {
        return convertMybatisPage(page, t -> BeanUtil.toBean(t, r));
    }
}
