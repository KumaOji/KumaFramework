package com.kuma.boot.data.jpa.fenix.helper;

import com.kuma.boot.data.jpa.fenix.bean.SqlInfo;
import com.kuma.boot.data.jpa.fenix.exception.FenixException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.data.repository.query.Param;

public final class ClassMethodInvoker {
   public ClassMethodInvoker() {
   }

   public static SqlInfo invoke(Class<?> cls, String method, Map<String, Object> paramMap) {
      Method[] methods = cls.getMethods();

      for(Method m : methods) {
         if (m.getName().equals(method)) {
            Parameter[] parameters = m.getParameters();
            List<Object> paramValues = new ArrayList(parameters.length);

            for(Parameter p : parameters) {
               Param param = (Param)p.getAnnotation(Param.class);
               paramValues.add(param != null ? paramMap.get(param.value()) : null);
            }

            return invokeMethod(cls, m, paramValues);
         }
      }

      String var10002 = cls.getName();
      throw new FenixException("\u3010Fenix \u5f02\u5e38\u3011\u672a\u627e\u5230\u3010" + var10002 + "\u3011\u7c7b\u4e2d\u53ef\u6267\u884c\u7684\u516c\u5171\u3010" + method + "\u3011\u65b9\u6cd5\uff0c\u8bf7\u68c0\u67e5\u8be5\u65b9\u6cd5\u662f\u5426\u5b58\u5728\u6216\u8005\u8bbf\u95ee\u6743\u9650\u662f public \u578b\u7684\uff01");
   }

   private static SqlInfo invokeMethod(Class<?> cls, Method m, List<Object> paramValues) {
      try {
         m.setAccessible(true);
         return (SqlInfo)m.invoke(cls.getDeclaredConstructor().newInstance(), paramValues.toArray());
      } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
         throw new FenixException("\u3010Fenix \u5f02\u5e38\u3011\u521b\u5efa\u3010" + cls.getName() + "\u3011\u7c7b\u7684\u5b9e\u4f8b\u5f02\u5e38\uff0c\u8bf7\u68c0\u67e5\u6784\u9020\u65b9\u6cd5\u662f\u5426\u662f\u65e0\u53c2 public \u578b\u7684\uff0c\u6216\u8005\u68c0\u67e5\u8c03\u7528\u7684\u3010" + m.getName() + "\u3011\u65b9\u6cd5\u662f\u5426\u662f public \u578b\u7684\uff01", e);
      }
   }
}
