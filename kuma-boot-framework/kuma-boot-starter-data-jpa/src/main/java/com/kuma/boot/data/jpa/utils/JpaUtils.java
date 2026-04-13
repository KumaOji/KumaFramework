/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.data.jpa.utils;

import static com.kuma.boot.common.model.result.PageResult.of;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kuma.boot.common.model.request.BasePageQuery;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.boot.common.model.result.PageResult;
import com.kuma.boot.common.utils.common.AntiSqlFilterUtils;
import com.kuma.boot.common.utils.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

/**
 * JpaUtils
 *
 * @author shuigedeng
 * @version 2026.03
 * @since 2025-12-19 09:30:45
 */
public class JpaUtils {

   @JsonIgnore
   public static <QueryDTO> Pageable buildJpaPage( BasePageQuery<QueryDTO> params ) {
      // 没有排序参数

      if (CollUtil.isEmpty(params.getSortQuery())) {
         return PageRequest.of(params.getCurrentPage(), params.getPageSize());
      }

      List<Order> orders = new ArrayList<>();
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
                            orders.add(Order.asc(underlineSort));
                         } else {
                            orders.add(Order.desc(underlineSort));
                         }
                      });
      return PageRequest.of(params.getCurrentPage(), params.getPageSize(), Sort.by(orders));
   }

   /**
    * 构造jpa分页参数
    *
    * @return 分页参数
    * @since 2022/3/14 13:53
    */
   public static <T extends PageQuery> Pageable buildJpaPage( T pageParam ) {
      List<Order> orders = new ArrayList<>();
      orders.add(Order.desc("create_time"));

      if (StringUtils.isNotBlank(pageParam.getSort())
              && StringUtils.isNotBlank(pageParam.getOrder())) {
         Order orderItem =
                 "asc".equals(pageParam.getOrder())
                         ? Order.asc(pageParam.getSort())
                         : Order.desc(pageParam.getSort());
         orders.add(orderItem);
      }

      return PageRequest.of(pageParam.getCurrentPage(), pageParam.getPageSize(), Sort.by(orders));
   }

   /**
    * 基于BeanUtil.toBean转换jpa分页数据
    *
    * @param page page
    * @param r r
    * @param <R> R
    * @return {@link PageResult }
    * @since 2021-09-02 19:10:45
    */
   public static <R, T> PageResult<R> convertJpaPage( Page<T> page, Class<R> r ) {
      return convertJpaPage(page, t -> BeanUtil.toBean(t, r));
   }

   /**
    * 基于function转换jpa分页数据
    *
    * @param page page
    * @param <R> R
    * @return {@link PageResult }
    * @since 2021-09-02 19:10:45
    */
   public static <R, T> PageResult<R> convertJpaPage( Page<T> page, Function<T, R> function ) {
      List<T> records = page.getContent();
      List<R> collect =
              Optional.of(records).orElse(new ArrayList<>()).stream()
                      .filter(Objects::nonNull)
                      .map(function)
                      .toList();

      return of(
              page.getTotalElements(),
              page.getTotalPages(),
              page.getNumber(),
              page.getSize(),
              collect);
   }
}
