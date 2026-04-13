package com.kuma.boot.data.jpa.fenix.core.builder;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.jpa.fenix.bean.BuildSource;
import com.kuma.boot.data.jpa.fenix.bean.SqlInfo;
import com.kuma.boot.data.jpa.fenix.consts.LikeTypeEnum;
import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class SqlInfoBuilder {
   protected SqlInfo sqlInfo;
   protected Object context;
   protected String prefix;
   private String symbol;
   private Map<String, Object> others;

   SqlInfoBuilder(BuildSource source) {
      this.sqlInfo = source.getSqlInfo();
      this.context = source.getContext();
      this.prefix = source.getPrefix();
      this.symbol = source.getSymbol();
      this.others = source.getOthers();
   }

   private void doPrependWhere() {
      if (this.sqlInfo.isPrependWhere()) {
         this.sqlInfo.getJoin().append(" WHERE ");
         if (" AND ".equalsIgnoreCase(this.prefix) || " OR ".equalsIgnoreCase(this.prefix)) {
            this.prefix = "";
         }

         this.sqlInfo.setPrependWhere(false);
      }

   }

   public void buildNormalSql(String fieldText, String name, Object value) {
      this.doPrependWhere();
      this.sqlInfo.getJoin().append(this.prefix).append(fieldText).append(this.symbol).append(":").append(name);
      this.sqlInfo.getParams().put(name, value);
   }

   public void buildLikeSql(String fieldText, String name, Object value) {
      this.doPrependWhere();
      this.sqlInfo.getJoin().append(this.prefix).append(fieldText).append(StringHelper.isBlank(this.symbol) ? " LIKE " : this.symbol).append(":").append(name);
      if (this.others != null && this.others.size() != 0) {
         LikeTypeEnum likeTypeEnum = (LikeTypeEnum)this.others.get("type");
         if (likeTypeEnum == LikeTypeEnum.STARTS_WITH) {
            this.sqlInfo.getParams().put(name, String.valueOf(value) + "%");
         } else if (likeTypeEnum == LikeTypeEnum.ENDS_WITH) {
            this.sqlInfo.getParams().put(name, "%" + String.valueOf(value));
         }

      } else {
         this.sqlInfo.getParams().put(name, "%" + String.valueOf(value) + "%");
      }
   }

   public void buildLikePatternSql(String fieldText, String pattern) {
      this.doPrependWhere();
      this.sqlInfo.getJoin().append(this.prefix).append(fieldText).append(StringHelper.isBlank(this.symbol) ? " LIKE " : this.symbol).append("'").append(pattern).append("'");
   }

   public void buildBetweenSql(String fieldText, String startText, Object startValue, String endText, Object endValue) {
      if (startValue == null && endValue == null) {
         LogUtils.warn("between \u533a\u95f4\u67e5\u8be2\u7684\u5f00\u59cb\u503c\u548c\u7ed3\u675f\u503c\u5747\u4e3a null\uff0c\u5c06\u76f4\u63a5\u8df3\u8fc7.", new Object[0]);
      } else {
         this.doPrependWhere();
         if (startValue != null && endValue == null) {
            String startNamed = StringHelper.fixDot(startText);
            this.sqlInfo.getJoin().append(this.prefix).append(fieldText).append(" >= ").append(":").append(startNamed);
            this.sqlInfo.getParams().put(startNamed, startValue);
         } else if (startValue == null) {
            String endNamed = StringHelper.fixDot(endText);
            this.sqlInfo.getJoin().append(this.prefix).append(fieldText).append(" <= ").append(":").append(endNamed);
            this.sqlInfo.getParams().put(endNamed, endValue);
         } else {
            String startNamed = StringHelper.fixDot(startText);
            String endNamed = StringHelper.fixDot(endText);
            this.sqlInfo.getJoin().append(this.prefix).append(fieldText).append(" BETWEEN ").append(":").append(startNamed).append(" AND ").append(":").append(endNamed);
            Map<String, Object> params = this.sqlInfo.getParams();
            params.put(startNamed, startValue);
            params.put(endNamed, endValue);
         }

      }
   }

   public void buildInSql(String fieldText, String name, Object obj) {
      this.doPrependWhere();
      this.sqlInfo.getJoin().append(this.prefix).append(fieldText).append(this.symbol).append(":").append(name);
      if (obj instanceof Collection) {
         this.sqlInfo.getParams().put(name, obj);
      } else if (obj.getClass().isArray()) {
         this.sqlInfo.getParams().put(name, Arrays.asList(obj));
      } else {
         this.sqlInfo.getParams().put(name, Collections.singletonList(obj));
      }

   }

   public void buildIsNullSql(String fieldText) {
      this.doPrependWhere();
      this.sqlInfo.getJoin().append(this.prefix).append(fieldText).append(this.symbol);
   }

   public SqlInfo getSqlInfo() {
      return this.sqlInfo;
   }

   public void setSqlInfo(SqlInfo sqlInfo) {
      this.sqlInfo = sqlInfo;
   }

   public Object getContext() {
      return this.context;
   }

   public void setContext(Object context) {
      this.context = context;
   }

   public String getPrefix() {
      return this.prefix;
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }

   public String getSymbol() {
      return this.symbol;
   }

   public void setSymbol(String symbol) {
      this.symbol = symbol;
   }

   public Map<String, Object> getOthers() {
      return this.others;
   }

   public void setOthers(Map<String, Object> others) {
      this.others = others;
   }
}
