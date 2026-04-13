package com.kuma.boot.data.jpa.fenix.core.builder;

import com.kuma.boot.data.jpa.fenix.bean.BuildSource;
import com.kuma.boot.data.jpa.fenix.exception.FenixException;
import com.kuma.boot.data.jpa.fenix.helper.ParseHelper;
import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import java.util.Arrays;
import java.util.Map;

public final class XmlSqlInfoBuilder extends SqlInfoBuilder {
   public XmlSqlInfoBuilder(BuildSource source) {
      super(source);
   }

   public void buildNormalSql(String fieldText, String name, String valueText) {
      super.buildNormalSql(fieldText, StringHelper.isBlank(name) ? StringHelper.fixDot(valueText) : name, ParseHelper.parseExpressWithException(valueText, this.context));
   }

   public void buildLikeSql(String fieldText, String name, String valueText, String patternText) {
      if (StringHelper.isNotBlank(valueText) && StringHelper.isBlank(patternText)) {
         super.buildLikeSql(fieldText, StringHelper.isBlank(name) ? StringHelper.fixDot(valueText) : name, ParseHelper.parseExpressWithException(valueText, this.context));
      } else {
         if (!StringHelper.isBlank(valueText) || !StringHelper.isNotBlank(patternText)) {
            throw new FenixException("\u3010Fenix \u5f02\u5e38\u3011<like /> \u76f8\u5173\u7684\u6807\u7b7e\u4e2d\uff0c\u3010value\u3011\u5c5e\u6027\u548c\u3010pattern\u3011\u5c5e\u6027\u4e0d\u80fd\u540c\u65f6\u4e3a\u7a7a\u6216\u8005\u540c\u65f6\u4e0d\u4e3a\u7a7a\uff01");
         }

         super.buildLikePatternSql(fieldText, patternText);
      }

   }

   public void buildBetweenSql(String fieldText, String startName, String startText, String endName, String endText) {
      super.buildBetweenSql(fieldText, StringHelper.isBlank(startName) ? StringHelper.fixDot(startText) : startName, ParseHelper.parseExpress(startText, this.context), StringHelper.isBlank(endName) ? StringHelper.fixDot(endText) : endName, ParseHelper.parseExpress(endText, this.context));
   }

   public void buildInSql(String fieldText, String name, String valueText) {
      Object obj = ParseHelper.parseExpressWithException(valueText, this.context);
      if (obj != null) {
         super.buildInSql(fieldText, StringHelper.isBlank(name) ? StringHelper.fixDot(valueText) : name, obj);
      }

   }

   public void buildTextSqlParams(String valueText) {
      Object obj;
      if (!StringHelper.isBlank(valueText) && (obj = ParseHelper.parseExpressWithException(valueText, this.context)) != null) {
         if (!(obj instanceof Map)) {
            throw new FenixException("\u3010Fenix \u5f02\u5e38\u63d0\u793a\u3011<text /> \u6807\u7b7e\u4e2d value \u503c\u7684\u7c7b\u578b\u4e0d\u662f Map \u7c7b\u578b\uff0c\u8bf7\u68c0\u67e5\uff01");
         } else {
            Map<String, Object> params = super.sqlInfo.getParams();

            for(Map.Entry<String, Object> entry : ((Map)obj).entrySet()) {
               Object value = entry.getValue();
               params.put((String)entry.getKey(), value != null && value.getClass().isArray() ? Arrays.asList(value) : value);
            }

         }
      }
   }
}
