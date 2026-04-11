package com.kuma.boot.data.mongodb.helper.utils;

import cn.hutool.core.bean.BeanUtil;
import com.kuma.boot.data.mongodb.helper.bean.Page;
import java.util.ArrayList;
import java.util.List;

public class BeanExtUtil {
   public BeanExtUtil() {
   }

   public static <T> List<T> copyListByProperties(List<?> list, Class<T> clazz) {
      if (list == null) {
         return null;
      } else {
         List<T> rsList = new ArrayList();

         for(Object source : list) {
            rsList.add(BeanUtil.copyProperties(source, clazz, new String[0]));
         }

         return rsList;
      }
   }

   public static <T> Page<T> copyPageByProperties(Page<?> page, Class<T> clazz) {
      Page<T> pageNew = (Page)copyBeanByProperties(page, Page.class);
      pageNew.setList(copyListByProperties(page.getList(), clazz));
      return pageNew;
   }

   public static <T> T copyBeanByProperties(Object source, Class<T> tClass) {
      return (T)BeanUtil.copyProperties(source, tClass, new String[0]);
   }
}
