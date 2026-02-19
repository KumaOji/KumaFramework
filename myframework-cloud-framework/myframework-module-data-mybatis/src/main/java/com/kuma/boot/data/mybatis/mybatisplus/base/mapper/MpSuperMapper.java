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

package com.kuma.boot.data.mybatis.mybatisplus.base.mapper;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.data.mybatis.mybatisplus.MpUtils;
import com.kuma.boot.data.mybatis.mybatisplus.query.BaseMapperX;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * 基于MP的 BaseMapper 新增了2个方法： insertBatchSomeColumn、updateAllById
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 21:17:15
 */
public interface MpSuperMapper<T, I extends Serializable> extends BaseMapperX<T> {

    /**
     * 根据实体条件查询并加锁
     */
    @SelectProvider(type = ForUpdateSqlProvider.class, method = "selectForUpdate")
    List<T> selectForUpdate(@Param("ew") Wrapper<T> wrapper);


    default T selectByIdForUpdate(Long id) {
        Class<?> entityClass = ReflectionKit.getSuperClassGenericType(getClass(), BaseMapper.class, 0);
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        String tableName = tableInfo.getTableName();

        String sql = String.format("SELECT * FROM %s WHERE %s = #{id} FOR UPDATE",
                tableName, tableInfo.getKeyColumn());

        return selectOneForUpdate(sql, id);
    }

    @Select("${sql}")
    T selectOneForUpdate(@Param("sql") String sql, @Param("id") Long id);

    /**
     * 根据ID加锁查询
     */
    default T selectByIdForUpdate(Serializable id) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getClass());
        String sql = String.format("SELECT * FROM %s WHERE %s = #{%s} FOR UPDATE",
                tableInfo.getTableName(),
                tableInfo.getKeyColumn(),
                tableInfo.getKeyProperty());
        return selectOneForUpdate(sql, (Long) id);
    }

    /**
     * 根据Wrapper加锁查询
     */
    default List<T> selectListForUpdate(Wrapper<T> queryWrapper) {
        String originalSql = queryWrapper.getCustomSqlSegment();
        String forUpdateSql = originalSql + " FOR UPDATE";
        AbstractWrapper abstractWrapper = (AbstractWrapper) queryWrapper;
        // 需要重新构建Wrapper或使用自定义SQL
        return selectListForUpdateBySql(forUpdateSql, abstractWrapper.getParamNameValuePairs());
    }

    @Select("${sql}")
    List<T> selectListForUpdateBySql(@Param("sql") String sql, @Param("params") Map<String, Object> params);

    /**
     * 查询单条记录加锁
     */
    default T selectOneForUpdate(Wrapper<T> queryWrapper) {
        List<T> list = selectListForUpdate(queryWrapper);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 选择页面
     *
     * @param queryWrapper 查询包装
     * @param pageQuery    页面参数
     * @return {@link IPage }<{@link T }>
     * @since 2022-09-07 08:52:08
     */
    default IPage<T> selectPage(@Param("ew") Wrapper<T> queryWrapper, PageQuery pageQuery) {
        // MyBatis Plus 查询
        IPage<T> page = MpUtils.buildMpPage(pageQuery);
        return selectPage(page, queryWrapper);
    }

    // default IPage<T> selectPage(@Param("ew") Wrapper<T> queryWrapper,
    // 	Collection<SortingField> sortingFields) {
    // 	// MyBatis Plus 查询
    // 	Page<T> mpPage = new Page<>(
    // 		Convert.toLong(ServletUtils.getParameterToInt(MybatisPageConstants.PAGE_NUM), 1L)
    // 		, Convert.toLong(ServletUtils.getParameterToInt(MybatisPageConstants.PAGE_SIZE), 10L));
    // 	if (!CollectionUtil.isEmpty(sortingFields)) {
    // 		mpPage.addOrder(sortingFields.stream()
    // 			.map(sortingField -> SortingField.ORDER_ASC.equals(sortingField.getOrder()) ?
    // 				OrderItem.asc(sortingField.getField())
    // 				: OrderItem.desc(sortingField.getField()))
    // 			.toList());
    // 	}
    // 	return selectPage(mpPage, queryWrapper);
    // }

    /**
     * 选择一个
     *
     * @param field 场
     * @param value 价值
     * @return {@link T }
     * @since 2022-09-07 08:52:08
     */
    default T selectOne(String field, Object value) {
        return Optional.ofNullable(selectOne(new QueryWrapper<T>().eq(field, value)))
                .orElseThrow(()->new BusinessException(ResultEnum.DATA_NOT_EXIST_ERROR));
    }

    /**
     * 选择一个
     *
     * @param field 场
     * @param value 价值
     * @return {@link T }
     * @since 2022-09-07 08:52:08
     */
    default T selectOne(SFunction<T, ?> field, Object value) {
        return selectOne(new LambdaQueryWrapper<T>().eq(field, value));
    }

    /**
     * 选择一个
     *
     * @param field1 field1
     * @param value1 value1
     * @param field2 field2
     * @param value2 value2
     * @return {@link T }
     * @since 2022-09-07 08:52:08
     */
    default T selectOne(String field1, Object value1, String field2, Object value2) {
        return selectOne(new QueryWrapper<T>().eq(field1, value1).eq(field2, value2));
    }

    /**
     * 选择一个
     *
     * @param field1 field1
     * @param value1 value1
     * @param field2 field2
     * @param value2 value2
     * @return {@link T }
     * @since 2022-09-07 08:52:08
     */
    default T selectOne(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2) {
        return selectOne(new LambdaQueryWrapper<T>().eq(field1, value1).eq(field2, value2));
    }

    /**
     * 选择数
     *
     * @return {@link Long }
     * @since 2022-09-07 08:52:09
     */
    default Long selectCount() {
        return selectCount(new QueryWrapper<T>());
    }

    /**
     * 选择数
     *
     * @param field 场
     * @param value 价值
     * @return {@link Long }
     * @since 2022-09-07 08:52:09
     */
    default Long selectCount(String field, Object value) {
        return selectCount(new QueryWrapper<T>().eq(field, value));
    }

    /**
     * 选择数
     *
     * @param field 场
     * @param value 价值
     * @return {@link Long }
     * @since 2022-09-07 08:52:09
     */
    default Long selectCount(SFunction<T, ?> field, Object value) {
        return selectCount(new LambdaQueryWrapper<T>().eq(field, value));
    }

    /**
     * 选择列表
     *
     * @return {@link List }<{@link T }>
     * @since 2022-09-07 08:52:09
     */
    default List<T> selectList() {
        return selectList(new QueryWrapper<>());
    }

    /**
     * 选择列表
     *
     * @param field 场
     * @param value 价值
     * @return {@link List }<{@link T }>
     * @since 2022-09-07 08:52:09
     */
    default List<T> selectList(String field, Object value) {
        return selectList(new QueryWrapper<T>().eq(field, value));
    }

    /**
     * 选择列表
     *
     * @param field 场
     * @param value 价值
     * @return {@link List }<{@link T }>
     * @since 2022-09-07 08:52:09
     */
    default List<T> selectList(SFunction<T, ?> field, Object value) {
        return selectList(new LambdaQueryWrapper<T>().eq(field, value));
    }

    /**
     * 选择列表
     *
     * @param field  场
     * @param values 值
     * @return {@link List }<{@link T }>
     * @since 2022-09-07 08:52:09
     */
    default List<T> selectList(String field, Collection<?> values) {
        return selectList(new QueryWrapper<T>().in(field, values));
    }

    /**
     * 选择列表
     *
     * @param field  场
     * @param values 值
     * @return {@link List }<{@link T }>
     * @since 2022-09-07 08:52:09
     */
    default List<T> selectList(SFunction<T, ?> field, Collection<?> values) {
        return selectList(new LambdaQueryWrapper<T>().in(field, values));
    }

    /**
     * 插入批
     *
     * @param entities 实体
     * @since 2022-09-07 08:52:09
     */
    default void insertBatch(Collection<T> entities) {
        entities.forEach(this::insert);
    }

    /**
     * 批处理更新
     *
     * @param update 更新
     * @since 2022-09-07 08:52:09
     */
//	default void update(T update) {
//		update(update, new LambdaQueryWrapper<T>().eq(SuperEntity::getId, update.getId()));
//	}

    /**
     * 全量修改所有字段
     *
     * @param entity 实体
     * @return 修改数量
     * @since 2021-09-02 21:17:23
     */
    int updateAllById(@Param(Constants.ENTITY) T entity);

    /**
     * 批量插入所有字段
     *
     * <p>只测试过MySQL！只测试过MySQL！只测试过MySQL！
     *
     * @param entityList 实体集合
     * @return 插入数量
     * @since 2021-09-02 21:17:23
     */
    int insertBatchSomeColumn(@Param("list") Collection<T> entityList);

    /**
     * 自定义批量插入 如果要自动填充，@Param(xx) xx参数名必须是 list/collection/array 3个的其中之一
     *
     * @param list 列表
     * @return int
     * @since 2022-10-10 13:48:20
     */
    int insertBatch(@Param("list") List<T> list);

    /**
     * 自定义批量更新，条件为主键 如果要自动填充，@Param(xx) xx参数名必须是 list/collection/array 3个的其中之一
     */
    // int updateBatch(@Param("collection") List<T> list);

    // 应为mysql对于太长的sql语句是有限制的，所以我这里设置每1000条批量插入拼接sql
    int BATCH_SIZE = 1000;

    default Integer batchInsert(Collection<T> entityList) {
        int result = 0;
        Collection<T> tempEntityList = new ArrayList<>();
        int i = 0;
        for (T entity : entityList) {
            tempEntityList.add(entity);
            if (i > 0 && (i % BATCH_SIZE == 0)) {
                result += insertBatchSomeColumn(tempEntityList);
                tempEntityList.clear();
            }
            i++;
        }

        if (CollectionUtils.isNotEmpty(tempEntityList)) {
            result += insertBatchSomeColumn(tempEntityList);
        }

        return result;
    }

    /**
     * 处理sql查询字段名 此接口有问题
     *
     * <p class='code'>Page<OrderModel> page = orderService.page(new Page<>(1, 20), new
     * QueryWrapper<OrderModel>() .select(" SUM(fee_amt) AS fee_amt, SUM(arrival_amt) AS arrival_amt, mer_no ",
     * getParamSql(OrderModel::getOrderNo)) .lambda() .between(StrUtil.isNotBlank(commandReq.getStartDate()) &&
     * StrUtil.isNotBlank(commandReq.getEndDate()), OrderModel::getTradeDate, StrUtil.isNotBlank(startDateStr) ?
     * LocalDate.parse(startDateStr) : null, StrUtil.isNotBlank(endDateStr) ? LocalDate.parse(cendDateStr) : null)
     * .groupBy(OrderModel::getMerNo));
     *
     * @param columns
     * @return
     */
    @Deprecated
    private String getParamSql(SFunction<T, ?>... columns) {
        return Arrays.stream(columns)
                .map(c -> StrUtil.toUnderlineCase(LambdaUtils.extract(c).getImplMethodName()))
                .collect(Collectors.joining(StringPool.COMMA, StringPool.SPACE, StringPool.SPACE));
    }
}
