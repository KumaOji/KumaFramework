package com.kuma.boot.data.jpa.fenix.core;

import com.kuma.boot.data.jpa.fenix.bean.BuildSource;
import com.kuma.boot.data.jpa.fenix.bean.SqlInfo;
import com.kuma.boot.data.jpa.fenix.config.FenixConfig;
import com.kuma.boot.data.jpa.fenix.config.FenixConfigManager;
import com.kuma.boot.data.jpa.fenix.core.builder.JavaSqlInfoBuilder;
import com.kuma.boot.data.jpa.fenix.core.concrete.EndsWithHandler;
import com.kuma.boot.data.jpa.fenix.core.concrete.StartsWithHandler;
import com.kuma.boot.data.jpa.fenix.exception.FenixException;
import com.kuma.boot.data.jpa.fenix.helper.SqlInfoPrinter;
import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

public final class Fenix {
   private static final String START = "_start";
   private static final String END = "_end";
   private static final Map<String, Object> startMap = StartsWithHandler.getStartMap();
   private static final Map<String, Object> endMap = EndsWithHandler.getEndsMap();
   private BuildSource source = new BuildSource(new SqlInfo());

   private Fenix() {
   }

   public static Fenix start() {
      return new Fenix();
   }

   public SqlInfo end() {
      SqlInfo sqlInfo = this.source.getSqlInfo();
      sqlInfo.setSql(StringHelper.replaceWhereAndOr(StringHelper.replaceBlank(sqlInfo.getJoin().toString())));
      FenixConfig fenixConfig = FenixConfigManager.getInstance().getFenixConfig();
      if (fenixConfig != null && fenixConfig.isPrintSqlInfo()) {
         (new SqlInfoPrinter()).print(sqlInfo);
      }

      return sqlInfo;
   }

   public static SqlInfo getXmlSqlInfo(String fullFenixId, Object context) {
      return FenixXmlBuilder.getXmlSqlInfo(fullFenixId, context);
   }

   public static SqlInfo getXmlSqlInfo(String namespace, String fenixId, Object context) {
      return FenixXmlBuilder.getXmlSqlInfo(namespace, fenixId, context);
   }

   private Fenix concat(String sqlKey, String... params) {
      this.source.getSqlInfo().getJoin().append(" ").append(sqlKey).append(" ");
      if (params != null && params.length > 0) {
         for(String param : params) {
            this.source.getSqlInfo().getJoin().append(param).append(" ");
         }
      }

      return this;
   }

   public Fenix insertInto(String text) {
      return this.concat("INSERT INTO", text);
   }

   public Fenix values(String text) {
      return this.concat("VALUES", text);
   }

   public Fenix deleteFrom(String text) {
      return this.concat("DELETE FROM", text);
   }

   public Fenix update(String text) {
      return this.concat("UPDATE", text);
   }

   public Fenix select(String text) {
      return this.concat("SELECT", text);
   }

   public Fenix from(String text) {
      return this.concat("FROM", text);
   }

   public Fenix where() {
      this.concat("WHERE");
      return this;
   }

   public Fenix where(String text) {
      this.concat("WHERE", text);
      return this;
   }

   public Fenix where(String text, Map<String, Object> paramMap) {
      this.concat("WHERE", text);
      return this.params(paramMap);
   }

   public Fenix where(String text, String key, Object value) {
      this.concat("WHERE", text);
      return this.param(key, value);
   }

   public Fenix where(Consumer<Fenix> consumer) {
      this.source.getSqlInfo().setPrependWhere(true);
      consumer.accept(this);
      return this;
   }

   public Fenix whereDynamic() {
      this.source.getSqlInfo().setPrependWhere(true);
      return this;
   }

   public Fenix and() {
      return this.concat("AND");
   }

   public Fenix and(String text) {
      return this.concat("AND", text);
   }

   public Fenix or() {
      return this.concat("OR");
   }

   public Fenix or(String text) {
      return this.concat("OR", text);
   }

   public Fenix as(String text) {
      return this.concat("AS", text);
   }

   public Fenix set(String text) {
      return this.concat("SET", text);
   }

   public Fenix innerJoin(String text) {
      return this.concat("INNER JOIN", text);
   }

   public Fenix leftJoin(String text) {
      return this.concat("LEFT JOIN", text);
   }

   public Fenix rightJoin(String text) {
      return this.concat("RIGHT JOIN", text);
   }

   public Fenix fullJoin(String text) {
      return this.concat("FULL JOIN", text);
   }

   public Fenix on(String text) {
      return this.concat("ON", text);
   }

   public Fenix orderBy(String text) {
      return this.concat("ORDER BY", text);
   }

   public Fenix groupBy(String text) {
      return this.concat("GROUP BY", text);
   }

   public Fenix having(String text) {
      return this.concat("HAVING", text);
   }

   public Fenix limit(String text) {
      return this.concat("LIMIT", text);
   }

   public Fenix offset(String text) {
      return this.concat("OFFSET", text);
   }

   public Fenix asc() {
      return this.concat("ASC");
   }

   public Fenix desc() {
      return this.concat("DESC");
   }

   public Fenix union() {
      return this.concat("UNION");
   }

   public Fenix unionAll() {
      return this.concat("UNION ALL");
   }

   public Fenix text(String text) {
      this.source.getSqlInfo().getJoin().append(text);
      return this;
   }

   public Fenix text(String text, String key, Object value) {
      this.source.getSqlInfo().getJoin().append(text);
      this.param(key, value);
      return this;
   }

   public Fenix text(String text, Map<String, Object> paramMap) {
      this.source.getSqlInfo().getJoin().append(text);
      this.params(paramMap);
      return this;
   }

   public Fenix text(boolean match, String text, String key, Object value) {
      return match ? this.text(text, key, value) : this;
   }

   public Fenix text(boolean match, String text, Map<String, Object> paramMap) {
      return match ? this.text(text, paramMap) : this;
   }

   public Fenix param(String key, Object value) {
      if (StringHelper.isBlank(key)) {
         throw new FenixException("\u3010Fenix \u5f02\u5e38\u63d0\u793a\u3011\u6dfb\u52a0\u7684\u547d\u540d\u53c2\u6570\u540d\u79f0\u4e3a\u7a7a\uff01");
      } else {
         this.source.getSqlInfo().getParams().put(key, value);
         return this;
      }
   }

   public Fenix params(Map<String, Object> paramMap) {
      if (paramMap == null) {
         throw new FenixException("\u3010Fenix \u5f02\u5e38\u63d0\u793a\u3011\u6dfb\u52a0\u7684\u547d\u540d\u53c2\u6570 Map \u4e3anull");
      } else {
         paramMap.forEach(this::param);
         return this;
      }
   }

   public Fenix doAny(FenixAction action) {
      SqlInfo sqlInfo = this.source.getSqlInfo();
      action.execute(sqlInfo.getJoin(), sqlInfo.getParams());
      return this;
   }

   public Fenix doAny(boolean match, FenixAction action) {
      return match ? this.doAny(action) : this;
   }

   private Fenix doNormal(String prefix, String field, String name, Object value, String symbol, boolean match) {
      if (match) {
         this.source.setPrefix(prefix).setSymbol(symbol);
         (new JavaSqlInfoBuilder(this.source)).buildNormalSql(field, StringHelper.isBlank(name) ? StringHelper.fixDot(field) : name, value);
         this.source.reset();
      }

      return this;
   }

   private Fenix doLike(String prefix, String field, String name, Object value, boolean match, boolean positive, Map<String, Object> likeTypeMap) {
      if (match) {
         this.source.setPrefix(prefix).setSymbol(positive ? " LIKE " : " NOT LIKE ").setOthers(likeTypeMap);
         (new JavaSqlInfoBuilder(this.source)).buildLikeSql(field, StringHelper.isBlank(name) ? StringHelper.fixDot(field) : name, value);
         this.source.reset();
      }

      return this;
   }

   private Fenix doLikePattern(String prefix, String field, String pattern, boolean match, boolean positive) {
      if (match) {
         this.source.setPrefix(prefix).setSymbol(positive ? " LIKE " : " NOT LIKE ");
         (new JavaSqlInfoBuilder(this.source)).buildLikePatternSql(field, pattern);
         this.source.reset();
      }

      return this;
   }

   private Fenix doBetween(String prefix, String field, String startName, Object startValue, String endName, Object endValue, boolean match) {
      if (match) {
         this.source.setPrefix(prefix);
         (new JavaSqlInfoBuilder(this.source)).buildBetweenSql(field, StringHelper.isBlank(startName) ? field + "_start" : startName, startValue, StringHelper.isBlank(endName) ? field + "_end" : endName, endValue);
         this.source.reset();
      }

      return this;
   }

   private Fenix doInByType(String prefix, String field, String name, Object value, boolean match, boolean positive) {
      if (match) {
         this.source.setPrefix(prefix).setSymbol(positive ? " IN " : " NOT IN ");
         (new JavaSqlInfoBuilder(this.source)).buildInSql(field, StringHelper.isBlank(name) ? StringHelper.fixDot(field) : name, value);
         this.source.reset();
      }

      return this;
   }

   private Fenix doIn(String prefix, String field, String name, Object[] values, boolean match, boolean positive) {
      return this.doInByType(prefix, field, name, values, match, positive);
   }

   private Fenix doIn(String prefix, String field, String name, Collection<?> values, boolean match, boolean positive) {
      return this.doInByType(prefix, field, name, values, match, positive);
   }

   private Fenix doIsNull(String prefix, String field, boolean match, boolean positive) {
      if (match) {
         this.source = this.source.setPrefix(prefix).setSymbol(positive ? " IS NULL " : " IS NOT NULL ");
         (new JavaSqlInfoBuilder(this.source)).buildIsNullSql(field);
         this.source.reset();
      }

      return this;
   }

   public Fenix equal(String field, Object value) {
      return this.doNormal(" ", field, (String)null, value, " = ", true);
   }

   public Fenix equal(String field, Object value, String name) {
      return this.doNormal(" ", field, name, value, " = ", true);
   }

   public Fenix equal(String field, Object value, boolean match) {
      return this.doNormal(" ", field, (String)null, value, " = ", match);
   }

   public Fenix equal(String field, Object value, String name, boolean match) {
      return this.doNormal(" ", field, name, value, " = ", match);
   }

   public Fenix andEqual(String field, Object value) {
      return this.doNormal(" AND ", field, (String)null, value, " = ", true);
   }

   public Fenix andEqual(String field, Object value, String name) {
      return this.doNormal(" AND ", field, name, value, " = ", true);
   }

   public Fenix andEqual(String field, Object value, boolean match) {
      return this.doNormal(" AND ", field, (String)null, value, " = ", match);
   }

   public Fenix andEqual(String field, Object value, String name, boolean match) {
      return this.doNormal(" AND ", field, name, value, " = ", match);
   }

   public Fenix orEqual(String field, Object value) {
      return this.doNormal(" OR ", field, (String)null, value, " = ", true);
   }

   public Fenix orEqual(String field, Object value, String name) {
      return this.doNormal(" OR ", field, name, value, " = ", true);
   }

   public Fenix orEqual(String field, Object value, boolean match) {
      return this.doNormal(" OR ", field, (String)null, value, " = ", match);
   }

   public Fenix orEqual(String field, Object value, String name, boolean match) {
      return this.doNormal(" OR ", field, name, value, " = ", match);
   }

   public Fenix notEqual(String field, Object value) {
      return this.doNormal(" ", field, (String)null, value, " <> ", true);
   }

   public Fenix notEqual(String field, Object value, String name) {
      return this.doNormal(" ", field, name, value, " <> ", true);
   }

   public Fenix notEqual(String field, Object value, boolean match) {
      return this.doNormal(" ", field, (String)null, value, " <> ", match);
   }

   public Fenix notEqual(String field, Object value, String name, boolean match) {
      return this.doNormal(" ", field, name, value, " <> ", match);
   }

   public Fenix andNotEqual(String field, Object value) {
      return this.doNormal(" AND ", field, (String)null, value, " <> ", true);
   }

   public Fenix andNotEqual(String field, Object value, String name) {
      return this.doNormal(" AND ", field, name, value, " <> ", true);
   }

   public Fenix andNotEqual(String field, Object value, boolean match) {
      return this.doNormal(" AND ", field, (String)null, value, " <> ", match);
   }

   public Fenix andNotEqual(String field, Object value, String name, boolean match) {
      return this.doNormal(" AND ", field, name, value, " <> ", match);
   }

   public Fenix orNotEqual(String field, Object value) {
      return this.doNormal(" OR ", field, (String)null, value, " <> ", true);
   }

   public Fenix orNotEqual(String field, Object value, String name) {
      return this.doNormal(" OR ", field, name, value, " <> ", true);
   }

   public Fenix orNotEqual(String field, Object value, boolean match) {
      return this.doNormal(" OR ", field, (String)null, value, " <> ", match);
   }

   public Fenix orNotEqual(String field, Object value, String name, boolean match) {
      return this.doNormal(" OR ", field, name, value, " <> ", match);
   }

   public Fenix greaterThan(String field, Object value) {
      return this.doNormal(" ", field, (String)null, value, " > ", true);
   }

   public Fenix greaterThan(String field, Object value, String name) {
      return this.doNormal(" ", field, name, value, " > ", true);
   }

   public Fenix greaterThan(String field, Object value, boolean match) {
      return this.doNormal(" ", field, (String)null, value, " > ", match);
   }

   public Fenix greaterThan(String field, Object value, String name, boolean match) {
      return this.doNormal(" ", field, name, value, " > ", match);
   }

   public Fenix andGreaterThan(String field, Object value) {
      return this.doNormal(" AND ", field, (String)null, value, " > ", true);
   }

   public Fenix andGreaterThan(String field, Object value, String name) {
      return this.doNormal(" AND ", field, name, value, " > ", true);
   }

   public Fenix andGreaterThan(String field, Object value, boolean match) {
      return this.doNormal(" AND ", field, (String)null, value, " > ", match);
   }

   public Fenix andGreaterThan(String field, Object value, String name, boolean match) {
      return this.doNormal(" AND ", field, name, value, " > ", match);
   }

   public Fenix orGreaterThan(String field, Object value) {
      return this.doNormal(" OR ", field, (String)null, value, " > ", true);
   }

   public Fenix orGreaterThan(String field, Object value, String name) {
      return this.doNormal(" OR ", field, name, value, " > ", true);
   }

   public Fenix orGreaterThan(String field, Object value, boolean match) {
      return this.doNormal(" OR ", field, (String)null, value, " > ", match);
   }

   public Fenix orGreaterThan(String field, Object value, String name, boolean match) {
      return this.doNormal(" OR ", field, name, value, " > ", match);
   }

   public Fenix lessThan(String field, Object value) {
      return this.doNormal(" ", field, (String)null, value, " < ", true);
   }

   public Fenix lessThan(String field, Object value, String name) {
      return this.doNormal(" ", field, name, value, " < ", true);
   }

   public Fenix lessThan(String field, Object value, boolean match) {
      return this.doNormal(" ", field, (String)null, value, " < ", match);
   }

   public Fenix lessThan(String field, Object value, String name, boolean match) {
      return this.doNormal(" ", field, name, value, " < ", match);
   }

   public Fenix andLessThan(String field, Object value) {
      return this.doNormal(" AND ", field, (String)null, value, " < ", true);
   }

   public Fenix andLessThan(String field, Object value, String name) {
      return this.doNormal(" AND ", field, name, value, " < ", true);
   }

   public Fenix andLessThan(String field, Object value, boolean match) {
      return this.doNormal(" AND ", field, (String)null, value, " < ", match);
   }

   public Fenix andLessThan(String field, Object value, String name, boolean match) {
      return this.doNormal(" AND ", field, name, value, " < ", match);
   }

   public Fenix orLessThan(String field, Object value) {
      return this.doNormal(" OR ", field, (String)null, value, " < ", true);
   }

   public Fenix orLessThan(String field, Object value, String name) {
      return this.doNormal(" OR ", field, name, value, " < ", true);
   }

   public Fenix orLessThan(String field, Object value, boolean match) {
      return this.doNormal(" OR ", field, (String)null, value, " < ", match);
   }

   public Fenix orLessThan(String field, Object value, String name, boolean match) {
      return this.doNormal(" OR ", field, name, value, " < ", match);
   }

   public Fenix greaterThanEqual(String field, Object value) {
      return this.doNormal(" ", field, (String)null, value, " >= ", true);
   }

   public Fenix greaterThanEqual(String field, Object value, String name) {
      return this.doNormal(" ", field, name, value, " >= ", true);
   }

   public Fenix greaterThanEqual(String field, Object value, boolean match) {
      return this.doNormal(" ", field, (String)null, value, " >= ", match);
   }

   public Fenix greaterThanEqual(String field, Object value, String name, boolean match) {
      return this.doNormal(" ", field, name, value, " >= ", match);
   }

   public Fenix andGreaterThanEqual(String field, Object value) {
      return this.doNormal(" AND ", field, (String)null, value, " >= ", true);
   }

   public Fenix andGreaterThanEqual(String field, Object value, String name) {
      return this.doNormal(" AND ", field, name, value, " >= ", true);
   }

   public Fenix andGreaterThanEqual(String field, Object value, boolean match) {
      return this.doNormal(" AND ", field, (String)null, value, " >= ", match);
   }

   public Fenix andGreaterThanEqual(String field, Object value, String name, boolean match) {
      return this.doNormal(" AND ", field, name, value, " >= ", match);
   }

   public Fenix orGreaterThanEqual(String field, Object value) {
      return this.doNormal(" OR ", field, (String)null, value, " >= ", true);
   }

   public Fenix orGreaterThanEqual(String field, Object value, String name) {
      return this.doNormal(" OR ", field, name, value, " >= ", true);
   }

   public Fenix orGreaterThanEqual(String field, Object value, boolean match) {
      return this.doNormal(" OR ", field, (String)null, value, " >= ", match);
   }

   public Fenix orGreaterThanEqual(String field, Object value, String name, boolean match) {
      return this.doNormal(" OR ", field, name, value, " >= ", match);
   }

   public Fenix lessThanEqual(String field, Object value) {
      return this.doNormal(" ", field, (String)null, value, " <= ", true);
   }

   public Fenix lessThanEqual(String field, Object value, String name) {
      return this.doNormal(" ", field, name, value, " <= ", true);
   }

   public Fenix lessThanEqual(String field, Object value, boolean match) {
      return this.doNormal(" ", field, (String)null, value, " <= ", match);
   }

   public Fenix lessThanEqual(String field, Object value, String name, boolean match) {
      return this.doNormal(" ", field, name, value, " <= ", match);
   }

   public Fenix andLessThanEqual(String field, Object value) {
      return this.doNormal(" AND ", field, (String)null, value, " <= ", true);
   }

   public Fenix andLessThanEqual(String field, Object value, String name) {
      return this.doNormal(" AND ", field, name, value, " <= ", true);
   }

   public Fenix andLessThanEqual(String field, Object value, boolean match) {
      return this.doNormal(" AND ", field, (String)null, value, " <= ", match);
   }

   public Fenix andLessThanEqual(String field, Object value, String name, boolean match) {
      return this.doNormal(" AND ", field, name, value, " <= ", match);
   }

   public Fenix orLessThanEqual(String field, Object value) {
      return this.doNormal(" OR ", field, (String)null, value, " <= ", true);
   }

   public Fenix orLessThanEqual(String field, Object value, String name) {
      return this.doNormal(" OR ", field, name, value, " <= ", true);
   }

   public Fenix orLessThanEqual(String field, Object value, boolean match) {
      return this.doNormal(" OR ", field, (String)null, value, " <= ", match);
   }

   public Fenix orLessThanEqual(String field, Object value, String name, boolean match) {
      return this.doNormal(" OR ", field, name, value, " <= ", match);
   }

   public Fenix like(String field, Object value) {
      return this.doLike(" ", field, (String)null, value, true, true, (Map)null);
   }

   public Fenix like(String field, Object value, String name) {
      return this.doLike(" ", field, name, value, true, true, (Map)null);
   }

   public Fenix like(String field, Object value, boolean match) {
      return this.doLike(" ", field, (String)null, value, match, true, (Map)null);
   }

   public Fenix like(String field, Object value, String name, boolean match) {
      return this.doLike(" ", field, name, value, match, true, (Map)null);
   }

   public Fenix andLike(String field, Object value) {
      return this.doLike(" AND ", field, (String)null, value, true, true, (Map)null);
   }

   public Fenix andLike(String field, Object value, String name) {
      return this.doLike(" AND ", field, name, value, true, true, (Map)null);
   }

   public Fenix andLike(String field, Object value, boolean match) {
      return this.doLike(" AND ", field, (String)null, value, match, true, (Map)null);
   }

   public Fenix andLike(String field, Object value, String name, boolean match) {
      return this.doLike(" AND ", field, name, value, match, true, (Map)null);
   }

   public Fenix orLike(String field, Object value) {
      return this.doLike(" OR ", field, (String)null, value, true, true, (Map)null);
   }

   public Fenix orLike(String field, Object value, String name) {
      return this.doLike(" OR ", field, name, value, true, true, (Map)null);
   }

   public Fenix orLike(String field, Object value, boolean match) {
      return this.doLike(" OR ", field, (String)null, value, match, true, (Map)null);
   }

   public Fenix orLike(String field, Object value, String name, boolean match) {
      return this.doLike(" OR ", field, name, value, match, true, (Map)null);
   }

   public Fenix notLike(String field, Object value) {
      return this.doLike(" ", field, (String)null, value, true, false, (Map)null);
   }

   public Fenix notLike(String field, Object value, String name) {
      return this.doLike(" ", field, name, value, true, false, (Map)null);
   }

   public Fenix notLike(String field, Object value, boolean match) {
      return this.doLike(" ", field, (String)null, value, match, false, (Map)null);
   }

   public Fenix notLike(String field, Object value, String name, boolean match) {
      return this.doLike(" ", field, name, value, match, false, (Map)null);
   }

   public Fenix andNotLike(String field, Object value) {
      return this.doLike(" AND ", field, (String)null, value, true, false, (Map)null);
   }

   public Fenix andNotLike(String field, Object value, String name) {
      return this.doLike(" AND ", field, name, value, true, false, (Map)null);
   }

   public Fenix andNotLike(String field, Object value, boolean match) {
      return this.doLike(" AND ", field, (String)null, value, match, false, (Map)null);
   }

   public Fenix andNotLike(String field, Object value, String name, boolean match) {
      return this.doLike(" AND ", field, name, value, match, false, (Map)null);
   }

   public Fenix orNotLike(String field, Object value) {
      return this.doLike(" OR ", field, (String)null, value, true, false, (Map)null);
   }

   public Fenix orNotLike(String field, Object value, String name) {
      return this.doLike(" OR ", field, name, value, true, false, (Map)null);
   }

   public Fenix orNotLike(String field, Object value, boolean match) {
      return this.doLike(" OR ", field, (String)null, value, match, false, (Map)null);
   }

   public Fenix orNotLike(String field, Object value, String name, boolean match) {
      return this.doLike(" OR ", field, name, value, match, false, (Map)null);
   }

   public Fenix startsWith(String field, Object value) {
      return this.doLike(" ", field, (String)null, value, true, true, startMap);
   }

   public Fenix startsWith(String field, Object value, String name) {
      return this.doLike(" ", field, name, value, true, true, startMap);
   }

   public Fenix startsWith(String field, Object value, boolean match) {
      return this.doLike(" ", field, (String)null, value, match, true, startMap);
   }

   public Fenix startsWith(String field, Object value, String name, boolean match) {
      return this.doLike(" ", field, name, value, match, true, startMap);
   }

   public Fenix andStartsWith(String field, Object value) {
      return this.doLike(" AND ", field, (String)null, value, true, true, startMap);
   }

   public Fenix andStartsWith(String field, Object value, String name) {
      return this.doLike(" AND ", field, name, value, true, true, startMap);
   }

   public Fenix andStartsWith(String field, Object value, boolean match) {
      return this.doLike(" AND ", field, (String)null, value, match, true, startMap);
   }

   public Fenix andStartsWith(String field, Object value, String name, boolean match) {
      return this.doLike(" AND ", field, name, value, match, true, startMap);
   }

   public Fenix orStartsWith(String field, Object value) {
      return this.doLike(" OR ", field, (String)null, value, true, true, startMap);
   }

   public Fenix orStartsWith(String field, Object value, String name) {
      return this.doLike(" OR ", field, name, value, true, true, startMap);
   }

   public Fenix orStartsWith(String field, Object value, boolean match) {
      return this.doLike(" OR ", field, (String)null, value, match, true, startMap);
   }

   public Fenix orStartsWith(String field, Object value, String name, boolean match) {
      return this.doLike(" OR ", field, name, value, match, true, startMap);
   }

   public Fenix notStartsWith(String field, Object value) {
      return this.doLike(" ", field, (String)null, value, true, false, startMap);
   }

   public Fenix notStartsWith(String field, Object value, String name) {
      return this.doLike(" ", field, name, value, true, false, startMap);
   }

   public Fenix notStartsWith(String field, Object value, boolean match) {
      return this.doLike(" ", field, (String)null, value, match, false, startMap);
   }

   public Fenix notStartsWith(String field, Object value, String name, boolean match) {
      return this.doLike(" ", field, name, value, match, false, startMap);
   }

   public Fenix andNotStartsWith(String field, Object value) {
      return this.doLike(" AND ", field, (String)null, value, true, false, startMap);
   }

   public Fenix andNotStartsWith(String field, Object value, String name) {
      return this.doLike(" AND ", field, name, value, true, false, startMap);
   }

   public Fenix andNotStartsWith(String field, Object value, boolean match) {
      return this.doLike(" AND ", field, (String)null, value, match, false, startMap);
   }

   public Fenix andNotStartsWith(String field, Object value, String name, boolean match) {
      return this.doLike(" AND ", field, name, value, match, false, startMap);
   }

   public Fenix orNotStartsWith(String field, Object value) {
      return this.doLike(" OR ", field, (String)null, value, true, false, startMap);
   }

   public Fenix orNotStartsWith(String field, Object value, String name) {
      return this.doLike(" OR ", field, name, value, true, false, startMap);
   }

   public Fenix orNotStartsWith(String field, Object value, boolean match) {
      return this.doLike(" OR ", field, (String)null, value, match, false, startMap);
   }

   public Fenix orNotStartsWith(String field, Object value, String name, boolean match) {
      return this.doLike(" OR ", field, name, value, match, false, startMap);
   }

   public Fenix endsWith(String field, Object value) {
      return this.doLike(" ", field, (String)null, value, true, true, endMap);
   }

   public Fenix endsWith(String field, Object value, String name) {
      return this.doLike(" ", field, name, value, true, true, endMap);
   }

   public Fenix endsWith(String field, Object value, boolean match) {
      return this.doLike(" ", field, (String)null, value, match, true, endMap);
   }

   public Fenix endsWith(String field, Object value, String name, boolean match) {
      return this.doLike(" ", field, name, value, match, true, endMap);
   }

   public Fenix andEndsWith(String field, Object value) {
      return this.doLike(" AND ", field, (String)null, value, true, true, endMap);
   }

   public Fenix andEndsWith(String field, Object value, String name) {
      return this.doLike(" AND ", field, name, value, true, true, endMap);
   }

   public Fenix andEndsWith(String field, Object value, boolean match) {
      return this.doLike(" AND ", field, (String)null, value, match, true, endMap);
   }

   public Fenix andEndsWith(String field, Object value, String name, boolean match) {
      return this.doLike(" AND ", field, name, value, match, true, endMap);
   }

   public Fenix orEndsWith(String field, Object value) {
      return this.doLike(" OR ", field, (String)null, value, true, true, endMap);
   }

   public Fenix orEndsWith(String field, Object value, String name) {
      return this.doLike(" OR ", field, name, value, true, true, endMap);
   }

   public Fenix orEndsWith(String field, Object value, boolean match) {
      return this.doLike(" OR ", field, (String)null, value, match, true, endMap);
   }

   public Fenix orEndsWith(String field, Object value, String name, boolean match) {
      return this.doLike(" OR ", field, name, value, match, true, endMap);
   }

   public Fenix notEndsWith(String field, Object value) {
      return this.doLike(" ", field, (String)null, value, true, false, endMap);
   }

   public Fenix notEndsWith(String field, Object value, String name) {
      return this.doLike(" ", field, name, value, true, false, endMap);
   }

   public Fenix notEndsWith(String field, Object value, boolean match) {
      return this.doLike(" ", field, (String)null, value, match, false, endMap);
   }

   public Fenix notEndsWith(String field, Object value, String name, boolean match) {
      return this.doLike(" ", field, name, value, match, false, endMap);
   }

   public Fenix andNotEndsWith(String field, Object value) {
      return this.doLike(" AND ", field, (String)null, value, true, false, endMap);
   }

   public Fenix andNotEndsWith(String field, Object value, String name) {
      return this.doLike(" AND ", field, name, value, true, false, endMap);
   }

   public Fenix andNotEndsWith(String field, Object value, boolean match) {
      return this.doLike(" AND ", field, (String)null, value, match, false, endMap);
   }

   public Fenix andNotEndsWith(String field, Object value, String name, boolean match) {
      return this.doLike(" AND ", field, name, value, match, false, endMap);
   }

   public Fenix orNotEndsWith(String field, Object value) {
      return this.doLike(" OR ", field, (String)null, value, true, false, endMap);
   }

   public Fenix orNotEndsWith(String field, Object value, String name) {
      return this.doLike(" OR ", field, name, value, true, false, endMap);
   }

   public Fenix orNotEndsWith(String field, Object value, boolean match) {
      return this.doLike(" OR ", field, (String)null, value, match, false, endMap);
   }

   public Fenix orNotEndsWith(String field, Object value, String name, boolean match) {
      return this.doLike(" OR ", field, name, value, match, false, endMap);
   }

   public Fenix likePattern(String field, String pattern) {
      return this.doLikePattern(" ", field, pattern, true, true);
   }

   public Fenix likePattern(String field, String pattern, boolean match) {
      return this.doLikePattern(" ", field, pattern, match, true);
   }

   public Fenix andLikePattern(String field, String pattern) {
      return this.doLikePattern(" AND ", field, pattern, true, true);
   }

   public Fenix andLikePattern(String field, String pattern, boolean match) {
      return this.doLikePattern(" AND ", field, pattern, match, true);
   }

   public Fenix orLikePattern(String field, String pattern) {
      return this.doLikePattern(" OR ", field, pattern, true, true);
   }

   public Fenix orLikePattern(String field, String pattern, boolean match) {
      return this.doLikePattern(" OR ", field, pattern, match, true);
   }

   public Fenix notLikePattern(String field, String pattern) {
      return this.doLikePattern(" ", field, pattern, true, false);
   }

   public Fenix notLikePattern(String field, String pattern, boolean match) {
      return this.doLikePattern(" ", field, pattern, match, false);
   }

   public Fenix andNotLikePattern(String field, String pattern) {
      return this.doLikePattern(" AND ", field, pattern, true, false);
   }

   public Fenix andNotLikePattern(String field, String pattern, boolean match) {
      return this.doLikePattern(" AND ", field, pattern, match, false);
   }

   public Fenix orNotLikePattern(String field, String pattern) {
      return this.doLikePattern(" OR ", field, pattern, true, false);
   }

   public Fenix orNotLikePattern(String field, String pattern, boolean match) {
      return this.doLikePattern(" OR ", field, pattern, match, false);
   }

   public Fenix between(String field, Object startValue, Object endValue) {
      return this.doBetween(" ", field, (String)null, startValue, (String)null, endValue, true);
   }

   public Fenix between(String field, String startName, Object startValue, String endName, Object endValue) {
      return this.doBetween(" ", field, startName, startValue, endName, endValue, true);
   }

   public Fenix between(String field, Object startValue, Object endValue, boolean match) {
      return this.doBetween(" ", field, (String)null, startValue, (String)null, endValue, match);
   }

   public Fenix between(String field, String startName, Object startValue, String endName, Object endValue, boolean match) {
      return this.doBetween(" ", field, startName, startValue, endName, endValue, match);
   }

   public Fenix andBetween(String field, Object startValue, Object endValue) {
      return this.doBetween(" AND ", field, (String)null, startValue, (String)null, endValue, true);
   }

   public Fenix andBetween(String field, String startName, Object startValue, String endName, Object endValue) {
      return this.doBetween(" AND ", field, startName, startValue, endName, endValue, true);
   }

   public Fenix andBetween(String field, Object startValue, Object endValue, boolean match) {
      return this.doBetween(" AND ", field, (String)null, startValue, (String)null, endValue, match);
   }

   public Fenix andBetween(String field, String startName, Object startValue, String endName, Object endValue, boolean match) {
      return this.doBetween(" AND ", field, startName, startValue, endName, endValue, match);
   }

   public Fenix orBetween(String field, Object startValue, Object endValue) {
      return this.doBetween(" OR ", field, (String)null, startValue, (String)null, endValue, true);
   }

   public Fenix orBetween(String field, String startName, Object startValue, String endName, Object endValue) {
      return this.doBetween(" OR ", field, startName, startValue, endName, endValue, true);
   }

   public Fenix orBetween(String field, Object startValue, Object endValue, boolean match) {
      return this.doBetween(" OR ", field, (String)null, startValue, (String)null, endValue, match);
   }

   public Fenix orBetween(String field, String startName, Object startValue, String endName, Object endValue, boolean match) {
      return this.doBetween(" OR ", field, startName, startValue, endName, endValue, match);
   }

   public Fenix in(String field, Object[] values) {
      return this.doIn(" ", field, (String)null, values, true, true);
   }

   public Fenix in(String field, String name, Object[] values) {
      return this.doIn(" ", field, name, values, true, true);
   }

   public Fenix in(String field, Object[] values, boolean match) {
      return this.doIn(" ", field, (String)null, values, match, true);
   }

   public Fenix in(String field, String name, Object[] values, boolean match) {
      return this.doIn(" ", field, name, values, match, true);
   }

   public Fenix in(String field, Collection<?> values) {
      return this.doIn(" ", field, (String)null, (Collection)values, true, true);
   }

   public Fenix in(String field, String name, Collection<?> values) {
      return this.doIn(" ", field, name, values, true, true);
   }

   public Fenix in(String field, Collection<?> values, boolean match) {
      return this.doIn(" ", field, (String)null, (Collection)values, match, true);
   }

   public Fenix in(String field, String name, Collection<?> values, boolean match) {
      return this.doIn(" ", field, name, values, match, true);
   }

   public Fenix andIn(String field, Object[] values) {
      return this.doIn(" AND ", field, (String)null, values, true, true);
   }

   public Fenix andIn(String field, String name, Object[] values) {
      return this.doIn(" AND ", field, name, values, true, true);
   }

   public Fenix andIn(String field, Object[] values, boolean match) {
      return this.doIn(" AND ", field, (String)null, values, match, true);
   }

   public Fenix andIn(String field, String name, Object[] values, boolean match) {
      return this.doIn(" AND ", field, name, values, match, true);
   }

   public Fenix andIn(String field, Collection<?> values) {
      return this.doIn(" AND ", field, (String)null, (Collection)values, true, true);
   }

   public Fenix andIn(String field, String name, Collection<?> values) {
      return this.doIn(" AND ", field, name, values, true, true);
   }

   public Fenix andIn(String field, Collection<?> values, boolean match) {
      return this.doIn(" AND ", field, (String)null, (Collection)values, match, true);
   }

   public Fenix andIn(String field, String name, Collection<?> values, boolean match) {
      return this.doIn(" AND ", field, name, values, match, true);
   }

   public Fenix orIn(String field, Object[] values) {
      return this.doIn(" OR ", field, (String)null, values, true, true);
   }

   public Fenix orIn(String field, String name, Object[] values) {
      return this.doIn(" OR ", field, name, values, true, true);
   }

   public Fenix orIn(String field, Object[] values, boolean match) {
      return this.doIn(" OR ", field, (String)null, values, match, true);
   }

   public Fenix orIn(String field, String name, Object[] values, boolean match) {
      return this.doIn(" OR ", field, name, values, match, true);
   }

   public Fenix orIn(String field, Collection<?> values) {
      return this.doIn(" OR ", field, (String)null, (Collection)values, true, true);
   }

   public Fenix orIn(String field, String name, Collection<?> values) {
      return this.doIn(" OR ", field, name, values, true, true);
   }

   public Fenix orIn(String field, Collection<?> values, boolean match) {
      return this.doIn(" OR ", field, (String)null, (Collection)values, match, true);
   }

   public Fenix orIn(String field, String name, Collection<?> values, boolean match) {
      return this.doIn(" OR ", field, name, values, match, true);
   }

   public Fenix notIn(String field, Object[] values) {
      return this.doIn(" ", field, (String)null, values, true, false);
   }

   public Fenix notIn(String field, String name, Object[] values) {
      return this.doIn(" ", field, name, values, true, false);
   }

   public Fenix notIn(String field, Object[] values, boolean match) {
      return this.doIn(" ", field, (String)null, values, match, false);
   }

   public Fenix notIn(String field, String name, Object[] values, boolean match) {
      return this.doIn(" ", field, name, values, match, false);
   }

   public Fenix notIn(String field, Collection<?> values) {
      return this.doIn(" ", field, (String)null, (Collection)values, true, false);
   }

   public Fenix notIn(String field, String name, Collection<?> values) {
      return this.doIn(" ", field, name, values, true, false);
   }

   public Fenix notIn(String field, Collection<?> values, boolean match) {
      return this.doIn(" ", field, (String)null, (Collection)values, match, false);
   }

   public Fenix notIn(String field, String name, Collection<?> values, boolean match) {
      return this.doIn(" ", field, name, values, match, false);
   }

   public Fenix andNotIn(String field, Object[] values) {
      return this.doIn(" AND ", field, (String)null, values, true, false);
   }

   public Fenix andNotIn(String field, String name, Object[] values) {
      return this.doIn(" AND ", field, name, values, true, false);
   }

   public Fenix andNotIn(String field, Object[] values, boolean match) {
      return this.doIn(" AND ", field, (String)null, values, match, false);
   }

   public Fenix andNotIn(String field, String name, Object[] values, boolean match) {
      return this.doIn(" AND ", field, name, values, match, false);
   }

   public Fenix andNotIn(String field, Collection<?> values) {
      return this.doIn(" AND ", field, (String)null, (Collection)values, true, false);
   }

   public Fenix andNotIn(String field, String name, Collection<?> values) {
      return this.doIn(" AND ", field, name, values, true, false);
   }

   public Fenix andNotIn(String field, Collection<?> values, boolean match) {
      return this.doIn(" AND ", field, (String)null, (Collection)values, match, false);
   }

   public Fenix andNotIn(String field, String name, Collection<?> values, boolean match) {
      return this.doIn(" AND ", field, name, values, match, false);
   }

   public Fenix orNotIn(String field, Object[] values) {
      return this.doIn(" OR ", field, (String)null, values, true, false);
   }

   public Fenix orNotIn(String field, String name, Object[] values) {
      return this.doIn(" OR ", field, name, values, true, false);
   }

   public Fenix orNotIn(String field, Object[] values, boolean match) {
      return this.doIn(" OR ", field, (String)null, values, match, false);
   }

   public Fenix orNotIn(String field, String name, Object[] values, boolean match) {
      return this.doIn(" OR ", field, name, values, match, false);
   }

   public Fenix orNotIn(String field, Collection<?> values) {
      return this.doIn(" OR ", field, (String)null, (Collection)values, true, false);
   }

   public Fenix orNotIn(String field, String name, Collection<?> values) {
      return this.doIn(" OR ", field, name, values, true, false);
   }

   public Fenix orNotIn(String field, Collection<?> values, boolean match) {
      return this.doIn(" OR ", field, (String)null, (Collection)values, match, false);
   }

   public Fenix orNotIn(String field, String name, Collection<?> values, boolean match) {
      return this.doIn(" OR ", field, name, values, match, false);
   }

   public Fenix isNull(String field) {
      return this.doIsNull(" ", field, true, true);
   }

   public Fenix isNull(String field, boolean match) {
      return this.doIsNull(" ", field, match, true);
   }

   public Fenix andIsNull(String field) {
      return this.doIsNull(" AND ", field, true, true);
   }

   public Fenix andIsNull(String field, boolean match) {
      return this.doIsNull(" AND ", field, match, true);
   }

   public Fenix orIsNull(String field) {
      return this.doIsNull(" OR ", field, true, true);
   }

   public Fenix orIsNull(String field, boolean match) {
      return this.doIsNull(" OR ", field, match, true);
   }

   public Fenix isNotNull(String field) {
      return this.doIsNull(" ", field, true, false);
   }

   public Fenix isNotNull(String field, boolean match) {
      return this.doIsNull(" ", field, match, false);
   }

   public Fenix andIsNotNull(String field) {
      return this.doIsNull(" AND ", field, true, false);
   }

   public Fenix andIsNotNull(String field, boolean match) {
      return this.doIsNull(" AND ", field, match, false);
   }

   public Fenix orIsNotNull(String field) {
      return this.doIsNull(" OR ", field, true, false);
   }

   public Fenix orIsNotNull(String field, boolean match) {
      return this.doIsNull(" OR ", field, match, false);
   }
}
