package com.kuma.boot.data.mongodb.helper.bean;

import com.kuma.boot.data.mongodb.helper.reflection.ReflectionUtil;
import com.kuma.boot.data.mongodb.helper.reflection.SerializableFunction;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Sort;

public class SortBuilder {
   List<Sort.Order> orderList = new ArrayList();

   public SortBuilder() {
   }

   public SortBuilder(List<Sort.Order> orderList) {
      this.orderList.addAll(orderList);
   }

   public <E, R> SortBuilder(SerializableFunction<E, R> column, Sort.Direction direction) {
      Sort.Order order = new Sort.Order(direction, ReflectionUtil.getFieldName(column));
      this.orderList.add(order);
   }

   public <E, R> SortBuilder add(SerializableFunction<E, R> column, Sort.Direction direction) {
      Sort.Order order = new Sort.Order(direction, ReflectionUtil.getFieldName(column));
      this.orderList.add(order);
      return this;
   }

   public Sort toSort() {
      return Sort.by(this.orderList);
   }
}
