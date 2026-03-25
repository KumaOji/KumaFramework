package com.kuma.boot.openapi.common.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONUtil;
import java.lang.reflect.Type;

public class StrObjectConvert {
   public static Object strToObj(String str, Type type) {
      if (str == null) {
         return null;
      } else {
         String typeName = type.getTypeName();
         boolean isClassType = false;
         if (type instanceof Class) {
            isClassType = true;
         }

         Object ins;
         if (isClassType && ClassUtil.isBasicType((Class)type)) {
            if (!typeName.equals(Void.class.getName()) && !typeName.equals(Void.TYPE.getName())) {
               ins = Convert.convert(type, str);
            } else {
               ins = null;
            }
         } else if (typeName.equals(String.class.getName())) {
            ins = str;
         } else if (isClassType && ((Class)type).isEnum()) {
            ins = Enum.valueOf((Class)type, str);
         } else {
            ins = JSONUtil.toBean(str, type.getClass());
         }

         return ins;
      }
   }

   public static String objToStr(Object obj, Type type) {
      if (obj == null) {
         return null;
      } else {
         String typeName = type.getTypeName();
         String str;
         if (ObjUtil.isBasicType(obj)) {
            if (!typeName.equals(Void.class.getName()) && !typeName.equals(Void.TYPE.getName())) {
               str = String.valueOf(obj);
            } else {
               str = null;
            }
         } else if (type.getTypeName().equals(String.class.getName())) {
            str = (String)obj;
         } else if (obj.getClass().isEnum()) {
            str = obj.toString();
         } else {
            str = JSONUtil.toJsonStr(obj);
         }

         return str;
      }
   }
}
