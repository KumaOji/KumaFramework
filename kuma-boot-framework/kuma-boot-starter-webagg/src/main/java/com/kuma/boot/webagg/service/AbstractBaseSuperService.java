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

package com.kuma.boot.webagg.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.repository.CrudRepository;
import com.kuma.boot.data.mybatis.mybatisplus.base.mapper.MpSuperMapper;
import com.kuma.boot.webagg.entity.SuperEntity;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * AbstractBaseSuperService
 *
 * @author kuma
 * @version 2022.03
 * @since 2021/10/10 12:53
 */
public abstract class AbstractBaseSuperService<
        T extends SuperEntity<T, I>,
        I extends Serializable,
        M extends MpSuperMapper<T, I>>
        extends CrudRepository<M, T>
        implements BaseSuperService<T, I> {

   /**
    * 发现通过id列
    *
    * @param id      id
    * @param columns 列
    * @return {@link Optional }<{@link T }>
    * @since 2023-04-11 13:26:32
    */
   @SafeVarargs
   public final Optional<T> findByIdWithColumns(Serializable id, SFunction<T, ?>... columns) {
      LambdaQueryWrapper<T> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.select(columns);
      queryWrapper.eq(SuperEntity::getId, id);
      return Optional.ofNullable(mapper().selectOne(queryWrapper));
   }

   /**
    * 发现通过id列
    *
    * @param ids     id
    * @param columns 列
    * @return {@link List }<{@link T }>
    * @since 2022-09-22 10:00:57
    */
   @SafeVarargs
   public final List<T> findByIdsWithColumns(List<Serializable> ids, SFunction<T, ?>... columns) {
      LambdaQueryWrapper<T> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.select(columns);
      queryWrapper.in(SuperEntity::getId, ids);
      return mapper().selectList(queryWrapper);
   }
}
