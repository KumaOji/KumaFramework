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

package com.kuma.boot.data.mongodb.helper.utils;

import com.kuma.boot.data.mongodb.helper.bean.Page;
import java.util.ArrayList;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;

/**
 * 豆ext跑龙套
 *
 * @author shuigedeng
 * @version 2022.05
 * @since 2022-05-27 21:52:35
 */
public class BeanExtUtil {

   /**
    * 根据List对象属性批量创建对应的Class List对象
    *
    * @param list
    * @param clazz
    * @return {@link List }<{@link T }>
    * @since 2022-05-27 21:52:35
    */
   public static <T> List<T> copyListByProperties(List<?> list, Class<T> clazz) {
      if (list == null) {
         return null;
      }

      List<T> rsList = new ArrayList<>();
      for (Object source : list) {
         rsList.add((T) BeanUtil.copyProperties(source, clazz));
      }

      return rsList;
   }

   /**
    * 根据PageResp对象属性批量创建对应的Class PageResp对象
    *
    * @param page 页面
    * @param clazz
    * @return {@link Page }<{@link T }>
    * @since 2022-05-27 21:52:35
    */
   @SuppressWarnings("unchecked")
   public static <T> Page<T> copyPageByProperties(Page<?> page, Class<T> clazz) {
      Page<T> pageNew = copyBeanByProperties(page, Page.class);
      pageNew.setList(copyListByProperties(page.getList(), clazz));
      return pageNew;
   }

   /**
    * 按照Bean对象属性创建对应的Class对象
    *
    * @param source 源
    * @param tClass t类
    * @return {@link T }
    * @since 2022-05-27 21:52:35
    */
   public static <T> T copyBeanByProperties(Object source, Class<T> tClass) {
      return BeanUtil.copyProperties(source, tClass);
   }
}
