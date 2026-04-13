package com.kuma.boot.data.jpa.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

public class JpaUtils {
   public JpaUtils() {
   }

   @JsonIgnore
   public static <QueryDTO> Pageable buildJpaPage(BasePageQuery<QueryDTO> params) {
      if (CollUtil.isEmpty(params.getSortQuery())) {
         return PageRequest.of(params.getCurrentPage(), params.getPageSize());
      } else {
         List<Sort.Order> orders = new ArrayList();
         params.getSortQuery().forEach((sortDTO) -> {
            String filed = sortDTO.getFiled();
            String order = sortDTO.getOrder();
            String underlineSort = StrUtil.toUnderlineCase(filed);
            if (!StrUtil.equalsAny(filed, new CharSequence[]{"createTime", "updateTime"})) {
               underlineSort = AntiSqlFilterUtils.getSafeValue(underlineSort);
            }

            if (StrUtil.equalsAny(order, new CharSequence[]{"asc"})) {
               orders.add(Order.asc(underlineSort));
            } else {
               orders.add(Order.desc(underlineSort));
            }

         });
         return PageRequest.of(params.getCurrentPage(), params.getPageSize(), Sort.by(orders));
      }
   }

   public static <T extends PageQuery> Pageable buildJpaPage(T pageParam) {
      List<Sort.Order> orders = new ArrayList();
      orders.add(Order.desc("create_time"));
      if (StringUtils.isNotBlank(pageParam.getSort()) && StringUtils.isNotBlank(pageParam.getOrder())) {
         Sort.Order orderItem = "asc".equals(pageParam.getOrder()) ? Order.asc(pageParam.getSort()) : Order.desc(pageParam.getSort());
         orders.add(orderItem);
      }

      return PageRequest.of(pageParam.getCurrentPage(), pageParam.getPageSize(), Sort.by(orders));
   }

   public static <R, T> PageResult<R> convertJpaPage(Page<T> page, Class<R> r) {
      return convertJpaPage(page, (Function)((t) -> BeanUtil.toBean(t, r)));
   }

   public static <R, T> PageResult<R> convertJpaPage(Page<T> page, Function<T, R> function) {
      List<T> records = page.getContent();
      List<R> collect = ((List)Optional.of(records).orElse(new ArrayList())).stream().filter(Objects::nonNull).map(function).toList();
      return PageResult.of(page.getTotalElements(), page.getTotalPages(), page.getNumber(), page.getSize(), collect);
   }
}
