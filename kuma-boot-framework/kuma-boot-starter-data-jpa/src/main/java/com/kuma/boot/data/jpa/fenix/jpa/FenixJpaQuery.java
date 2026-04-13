package com.kuma.boot.data.jpa.fenix.jpa;

import com.kuma.boot.data.jpa.fenix.bean.SqlInfo;
import com.kuma.boot.data.jpa.fenix.core.Fenix;
import com.kuma.boot.data.jpa.fenix.helper.ClassMethodInvoker;
import com.kuma.boot.data.jpa.fenix.helper.QueryHelper;
import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.AbstractJpaQuery;
import org.springframework.data.jpa.repository.query.JpaParameters;
import org.springframework.data.jpa.repository.query.JpaParametersParameterAccessor;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.ReturnedType;

public class FenixJpaQuery extends AbstractJpaQuery {
   private static final String REGX_SELECT_FROM = "((?i)select)([\\s\\S]*?)((?i)from)";
   private static final String SELECT_COUNT = "select count(*) as count from ";
   private static final String REGX_SELECT_FROM_DISTINCT = "((?i)select)([\\s\\S]*?)((?i)distinct)\\s+([\\s\\S]*?)((?i)from)";
   private static final String REGX_SQL_ALIAS = "\\s+((?i)as)\\s+[^,\\s]+";
   private static final Pattern SELECT_FROM_PATTERN = Pattern.compile("((?i)select)([\\s\\S]*?)((?i)from)");
   private static final Pattern SELECT_FROM_DISTINCT_PATTERN = Pattern.compile("((?i)select)([\\s\\S]*?)((?i)distinct)\\s+([\\s\\S]*?)((?i)from)");
   private JpaParameters jpaParams;
   private QueryFenix queryFenix;
   private Class<?> queryClass;
   private boolean hasDeclaredCountQuery;

   public JpaParameters getJpaParams() {
      return this.jpaParams;
   }

   public void setJpaParams(JpaParameters jpaParams) {
      this.jpaParams = jpaParams;
   }

   public QueryFenix getQueryFenix() {
      return this.queryFenix;
   }

   public void setQueryFenix(QueryFenix queryFenix) {
      this.queryFenix = queryFenix;
   }

   public Class<?> getQueryClass() {
      return this.queryClass;
   }

   public void setQueryClass(Class<?> queryClass) {
      this.queryClass = queryClass;
   }

   public boolean isHasDeclaredCountQuery() {
      return this.hasDeclaredCountQuery;
   }

   public void setHasDeclaredCountQuery(boolean hasDeclaredCountQuery) {
      this.hasDeclaredCountQuery = hasDeclaredCountQuery;
   }

   FenixJpaQuery(JpaQueryMethod method, EntityManager em) {
      super(method, em);
   }

   public boolean hasDeclaredCountQuery() {
      return this.hasDeclaredCountQuery;
   }

   protected Query doCreateQuery(JpaParametersParameterAccessor accessor) {
      return this.doCreateQuery(accessor.getValues());
   }

   protected Query doCreateQuery(Object[] values) {
      JpaQueryMethod jpaMethod = super.getQueryMethod();
      this.jpaParams = jpaMethod.getParameters();
      FenixQueryInfo fenixQueryInfo = FenixQueryInfo.getInstance();
      fenixQueryInfo.setContextParams(this.buildContextParams(values));
      SqlInfo sqlInfo = this.getSqlInfoByFenix();
      fenixQueryInfo.setSqlInfo(sqlInfo);
      fenixQueryInfo.setQuerySql(sqlInfo.getSql());
      Pageable pageable = this.buildPagableAndSortSql(values, fenixQueryInfo.getQuerySql());
      EntityManager em = super.getEntityManager();
      String querySql = fenixQueryInfo.getQuerySql();
      Query query;
      if (this.queryFenix.nativeQuery()) {
         Class<?> type = this.getTypeToQueryFor(jpaMethod.getResultProcessor().withDynamicProjection(new ParametersParameterAccessor(jpaMethod.getParameters(), values)).getReturnedType(), querySql);
         query = type == null ? em.createNativeQuery(querySql) : em.createNativeQuery(querySql, type);
      } else {
         query = em.createQuery(querySql);
      }

      Map var10000 = sqlInfo.getParams();
      Objects.requireNonNull(query);
      var10000.forEach(query::setParameter);
      if (pageable != null && pageable.isPaged()) {
         query.setFirstResult((int)pageable.getOffset());
         query.setMaxResults(pageable.getPageSize());
      }

      String resultType = sqlInfo.getResultType();
      if (StringHelper.isNotBlank(resultType) || this.queryFenix.resultType() != Void.class) {
         query = QueryResultContext.buildTransformer(query, resultType, this.queryFenix);
      }

      if (pageable == null || pageable.isUnpaged()) {
         fenixQueryInfo.remove();
      }

      return query;
   }

   protected Query doCreateCountQuery(JpaParametersParameterAccessor accessor) {
      return this.doCreateCountQuery(accessor.getValues());
   }

   protected Query doCreateCountQuery(Object[] values) {
      String countSql = this.getCountSql();
      EntityManager em = this.getEntityManager();
      Query query = (Query)(this.queryFenix.nativeQuery() ? em.createNativeQuery(countSql) : em.createQuery(countSql, Long.class));
      Map var10000 = FenixQueryInfo.getInstance().getSqlInfo().getParams();
      Objects.requireNonNull(query);
      var10000.forEach(query::setParameter);
      FenixQueryInfo fenixQueryInfo = FenixQueryInfo.getLocalThreadInstance();
      if (fenixQueryInfo != null) {
         fenixQueryInfo.remove();
      }

      return query;
   }

   private Class<?> getTypeToQueryFor(ReturnedType returnedType, String querySql) {
      Class<?> result = this.getQueryMethod().isQueryForEntity() ? returnedType.getDomainType() : null;
      if (!QueryUtils.hasConstructorExpression(querySql) && !QueryUtils.getProjection(querySql).equalsIgnoreCase(QueryHelper.detectAlias(querySql))) {
         return returnedType.isProjecting() && !this.getMetamodel().isJpaManaged(returnedType.getReturnedType()) ? Tuple.class : result;
      } else {
         return result;
      }
   }

   private Map<String, Object> buildContextParams(Object[] values) {
      int len = this.jpaParams.getNumberOfParameters();
      Map<String, Object> context = new HashMap(len);

      for(int i = 0; i < len; ++i) {
         Parameter parameter = this.jpaParams.getParameter(i);
         if (!parameter.isSpecialParameter()) {
            Optional<String> nameOptional = parameter.getName();
            if (nameOptional.isPresent()) {
               context.put((String)nameOptional.get(), values[i]);
            }
         }
      }

      return context;
   }

   private SqlInfo getSqlInfoByFenix() {
      Class<?> provider = this.queryFenix.provider();
      String method = this.queryFenix.method();
      String fenixId = this.queryFenix.value();
      Map<String, Object> contextParams = FenixQueryInfo.getInstance().getContextParams();
      if (provider != Void.class) {
         if (StringHelper.isNotBlank(method)) {
            return ClassMethodInvoker.invoke(provider, method, contextParams);
         } else {
            return StringHelper.isNotBlank(fenixId) ? this.getXmlSqlInfo(fenixId, contextParams) : ClassMethodInvoker.invoke(provider, this.getQueryMethod().getName(), contextParams);
         }
      } else {
         return StringHelper.isNotBlank(fenixId) ? this.getXmlSqlInfo(fenixId, contextParams) : Fenix.getXmlSqlInfo(this.queryClass.getName(), this.getQueryMethod().getName(), contextParams);
      }
   }

   private SqlInfo getXmlSqlInfo(String fenixId, Map<String, Object> contextParams) {
      if (fenixId.contains(".")) {
         int i = fenixId.lastIndexOf(".");
         return Fenix.getXmlSqlInfo(fenixId.substring(0, i), fenixId.substring(i + 1), contextParams);
      } else {
         return Fenix.getXmlSqlInfo(this.queryClass.getName(), fenixId, contextParams);
      }
   }

   private Pageable buildPagableAndSortSql(Object[] values, String querySql) {
      Pageable pageable = null;
      FenixQueryInfo fenixQueryInfo = FenixQueryInfo.getInstance();
      if (this.jpaParams.hasPageableParameter()) {
         pageable = (Pageable)values[this.jpaParams.getPageableIndex()];
         if (pageable != null) {
            fenixQueryInfo.setQuerySql(QueryUtils.applySorting(querySql, pageable.getSort(), QueryHelper.detectAlias(querySql)));
         }
      }

      if (this.jpaParams.hasSortParameter()) {
         fenixQueryInfo.setQuerySql(QueryUtils.applySorting(querySql, (new ParametersParameterAccessor(this.jpaParams, values)).getSort(), QueryHelper.detectAlias(querySql)));
      }

      return pageable;
   }

   private String getCountSql() {
      Class<?> provider = this.queryFenix.provider();
      String xmlCountQuery = this.queryFenix.countQuery();
      String countMethod = this.queryFenix.countMethod();
      FenixQueryInfo fenixQueryInfo = FenixQueryInfo.getInstance();
      Map<String, Object> contextParams = fenixQueryInfo.getContextParams();
      if (provider != Void.class) {
         if (StringHelper.isNotBlank(countMethod)) {
            fenixQueryInfo.setSqlInfo(ClassMethodInvoker.invoke(provider, countMethod, contextParams));
            return fenixQueryInfo.getSqlInfo().getSql();
         } else if (StringHelper.isNotBlank(xmlCountQuery)) {
            fenixQueryInfo.setSqlInfo(this.getXmlSqlInfo(xmlCountQuery, contextParams));
            return fenixQueryInfo.getSqlInfo().getSql();
         } else {
            return this.getCountSqlByQueryInfo(fenixQueryInfo);
         }
      } else if (StringHelper.isNotBlank(xmlCountQuery)) {
         fenixQueryInfo.setSqlInfo(this.getXmlSqlInfo(xmlCountQuery, contextParams));
         return fenixQueryInfo.getSqlInfo().getSql();
      } else {
         return this.getCountSqlByQueryInfo(fenixQueryInfo);
      }
   }

   private String getCountSqlByQueryInfo(FenixQueryInfo fenixQueryInfo) {
      boolean enableDistinct = this.queryFenix.enableDistinct();
      String infoSql = fenixQueryInfo.getSqlInfo().getSql();
      Matcher matcher = SELECT_FROM_PATTERN.matcher(infoSql);
      String countSql = matcher.replaceFirst("select count(*) as count from ");
      if (!enableDistinct) {
         return countSql;
      } else {
         String selectPrefix = matcher.group();
         matcher = SELECT_FROM_DISTINCT_PATTERN.matcher(selectPrefix);
         if (!matcher.find()) {
            return countSql;
         } else {
            String distinctColumn = matcher.group(4).replaceAll("\\s+((?i)as)\\s+[^,\\s]+", "");
            return countSql.replaceFirst("count\\(\\*\\)", String.format("count(distinct %s)", distinctColumn));
         }
      }
   }
}
