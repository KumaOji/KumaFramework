package com.kuma.boot.data.jpa.fenix.core;

import com.kuma.boot.data.jpa.fenix.bean.BuildSource;
import com.kuma.boot.data.jpa.fenix.bean.SqlInfo;
import com.kuma.boot.data.jpa.fenix.config.FenixConfig;
import com.kuma.boot.data.jpa.fenix.config.entity.TagHandler;
import com.kuma.boot.data.jpa.fenix.exception.FenixException;
import com.kuma.boot.data.jpa.fenix.exception.NodeNotFoundException;
import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import java.lang.reflect.InvocationTargetException;
import org.springframework.util.StringUtils;

public final class FenixContext {
   public FenixContext() {
   }

   public static void buildPlainTextSqlInfo(SqlInfo sqlInfo, String plainText) {
      if (sqlInfo.isPrependWhere()) {
         String text = StringHelper.replaceBlank(plainText);
         if (!StringHelper.isBlank(text)) {
            sqlInfo.getJoin().append(" WHERE ");
            sqlInfo.setPrependWhere(false);
            if (StringUtils.startsWithIgnoreCase(text, "AND ")) {
               text = text.substring(4);
            } else if (StringUtils.startsWithIgnoreCase(text, "OR ")) {
               text = text.substring(3);
            }

            sqlInfo.getJoin().append(text).append(" ");
         }
      } else {
         sqlInfo.getJoin().append(plainText);
      }
   }

   static void buildSqlInfo(BuildSource source, String tag) {
      TagHandler handler = (TagHandler)FenixConfig.getTagHandlerMap().get(tag);
      if (handler == null) {
         throw new NodeNotFoundException(StringHelper.format("【Fenix 异常】未找到该【<{}>】标签对应的处理器.", tag));
      } else {
         source.setPrefix(handler.getPrefix()).setSymbol(handler.getSymbol());
         FenixHandlerFactory handlerFactory = handler.getHandlerFactory();
         if (handlerFactory != null) {
            handlerFactory.newInstance().buildSqlInfo(source);
         } else {
            try {
               ((FenixHandler)handler.getHandlerCls().getDeclaredConstructor().newInstance()).buildSqlInfo(source);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
               throw new FenixException(StringHelper.format("【Fenix 异常】访问或实例化【{}】class 出错!", handler.getHandlerCls().getName()), e);
            }
         }
      }
   }
}
