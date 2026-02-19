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

import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.kuma.boot.data.mybatis.mybatisplus.base.entity.MpSuperEntity;
import com.kuma.boot.data.mybatis.mybatisplus.query.BaseMapperX;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 基于MP的 BaseMapper 新增了2个方法： insertBatchSomeColumn、updateAllById
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 21:17:15
 */
public interface MpSuperMapperOther<T extends MpSuperEntity<I>, I extends Serializable>
        extends BaseMapperX<T> {

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

    default int insertBatchSomeColumn(List<T> entityList, int batchSize) {
        int size = entityList.size();
        if (size < batchSize) {
            return this.insertBatchSomeColumn(entityList);
        }
        int page;
        if (size % batchSize == 0) {
            page = size / batchSize;
        } else {
            page = size / batchSize + 1;
        }
        for (int i = 0; i < page; i++) {
            List<T> sub = new ArrayList<>();
            if (i == page - 1) {
                sub = entityList.subList(i * batchSize, entityList.size());
            } else {
                sub.subList(i * batchSize, (i + 1) * batchSize - 1);
            }
            if (!sub.isEmpty()) {
                this.insertBatchSomeColumn(sub);
            }
        }
        return size;
    }

    /**
     * 自定义批量插入 如果要自动填充，@Param(xx) xx参数名必须是 list/collection/array 3个的其中之一
     */
    int insertBatch(@Param("collection") List<T> list);

    /**
     * 自定义批量更新，条件为主键 如果要自动填充，@Param(xx) xx参数名必须是 list/collection/array 3个的其中之一
     */
    // int updateBatch(@Param("collection") List<T> list);

    List<T> selectByErp(String erp);

    int alwaysUpdateSomeColumnById(@Param(Constants.ENTITY) T entity);
}
