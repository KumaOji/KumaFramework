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
         throw new NodeNotFoundException(StringHelper.format("\u3010Fenix \u5f02\u5e38\u3011\u672a\u627e\u5230\u8be5\u3010<{}>\u3011\u6807\u7b7e\u5bf9\u5e94\u7684\u5904\u7406\u5668.", tag));
      } else {
         source.setPrefix(handler.getPrefix()).setSymbol(handler.getSymbol());
         FenixHandlerFactory handlerFactory = handler.getHandlerFactory();
         if (handlerFactory != null) {
            handlerFactory.newInstance().buildSqlInfo(source);
         } else {
            try {
               ((FenixHandler)handler.getHandlerCls().getDeclaredConstructor().newInstance()).buildSqlInfo(source);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
               throw new FenixException(StringHelper.format("\u3010Fenix \u5f02\u5e38\u3011\u8bbf\u95ee\u6216\u5b9e\u4f8b\u5316\u3010{}\u3011class \u51fa\u9519!", handler.getHandlerCls().getName()), e);
            }
         }
      }
   }
}
