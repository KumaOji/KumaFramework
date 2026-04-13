package com.kuma.boot.data.jpa.extend;

import cn.hutool.core.util.StrUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.query.JpaParameters;

public final class ExpressionQueryResolverStrategy {
   public ExpressionQueryResolverStrategy() {
   }

   public static QueryResolveResult resolve(String queryString, boolean expressionQuery, JpaParameters parameters, Object[] values) {
      Optional<ExpressionQueryResolverEnum> resolverEnumOptional = Arrays.stream(ExpressionQueryResolverStrategy.ExpressionQueryResolverEnum.values()).filter((item) -> item.match(queryString, expressionQuery)).findFirst();
      if (!resolverEnumOptional.isPresent()) {
         throw new RuntimeException("\u6ca1\u6709\u627e\u5230SQL\u89e3\u6790\u7b56\u7565");
      } else {
         return ((ExpressionQueryResolverEnum)resolverEnumOptional.get()).resolve(queryString, parameters, values);
      }
   }

   interface ExpressionQueryResolver {
      String PLACEHOLDER_PREFIX = "?{";
      String PLACEHOLDER_SUFFIX = "}";
      String BLANK_STR = "";
      Pattern PLACEHOLDER_EXPRESSION_PARAMETER = Pattern.compile("\\?\\{(.+?)\\}");
      Pattern POSITION_EXPRESSION_PARAMETER = Pattern.compile("\\?[1-9+]");
      Pattern NAME_EXPRESSION_PARAMETER = Pattern.compile(":[a-zA-Z0-9]+");
      Pattern NO_PLACEHOLDER_POSITION_EXPRESSION_PARAMETER = Pattern.compile("(where|WHERE|and|AND|or|OR)\\s+[a-zA-Z._]+\\s+=\\s+\\?[1-9+]");
      Pattern NO_PLACEHOLDER_NAME_EXPRESSION_PARAMETER = Pattern.compile("(where|WHERE|and|AND|or|OR)\\s+[a-zA-Z._]+\\s+=\\s+:[a-zA-Z0-9]+");

      boolean match(String queryString, boolean expressionQuery);

      QueryResolveResult resolve(String queryString, JpaParameters parameters, Object[] values);

      default String nameParameterProcessor(String queryString, Map<String, Object> allQueryParams, List<String> removeParams, String matchExpression) {
         Matcher parameterExpressionMatcher = NAME_EXPRESSION_PARAMETER.matcher(matchExpression);
         if (parameterExpressionMatcher.find()) {
            String parameterName = parameterExpressionMatcher.group().replace(":", "");
            Object parameterValue = allQueryParams.get(parameterName);
            if (parameterValue == null) {
               queryString = queryString.replace(matchExpression, "");
               removeParams.add(parameterName);
            }
         }

         if (removeParams.size() == allQueryParams.size()) {
            queryString = StrUtil.replace(queryString, "where", "", true);
         }

         return queryString;
      }

      default String positionParameterProcessor(String queryString, Object[] values, List<Integer> removeParamIndex, String parameterExpression) {
         Matcher positionExpressionMatcher = POSITION_EXPRESSION_PARAMETER.matcher(parameterExpression);
         if (positionExpressionMatcher.find()) {
            String paramExpression = positionExpressionMatcher.group();
            Integer index = Integer.valueOf(paramExpression.replace("?", ""));
            Integer position = index - 1;
            Object paramValue = values[position];
            if (paramValue == null) {
               queryString = queryString.replace(parameterExpression, "");
               removeParamIndex.add(position);
            } else {
               int count = ((List)removeParamIndex.stream().filter((item) -> index > item).collect(Collectors.toList())).size();
               Integer newIndex = index - count;
               String newParameter = parameterExpression.replace("?" + index, "?" + newIndex);
               queryString = queryString.replace(parameterExpression, newParameter);
            }
         }

         if (removeParamIndex.size() == values.length) {
            queryString = StrUtil.replace(queryString, "where", "", true);
         }

         return queryString;
      }
   }

   static enum ExpressionQueryResolverEnum implements ExpressionQueryResolver {
      EmptyExpressionQueryResolver {
         public boolean match(String queryString, boolean expressionQuery) {
            return !expressionQuery;
         }

         public QueryResolveResult resolve(String queryString, JpaParameters parameters, Object[] values) {
            Matcher positionExpressionParameter = POSITION_EXPRESSION_PARAMETER.matcher(queryString);
            boolean positionParam = false;
            if (positionExpressionParameter.find()) {
               positionParam = true;
            }

            return new QueryResolveResult.EmptyQueryResolveResult(queryString, positionParam, parameters, values);
         }
      },
      PlaceholderPositionExpressionQueryResolver {
         public boolean match(String queryString, boolean expressionQuery) {
            if (!expressionQuery) {
               return false;
            } else {
               Matcher expressionParameter = PLACEHOLDER_EXPRESSION_PARAMETER.matcher(queryString);
               Matcher positionExpressionParameter = POSITION_EXPRESSION_PARAMETER.matcher(queryString);
               return expressionParameter.find() && positionExpressionParameter.find();
            }
         }

         public QueryResolveResult resolve(String queryString, JpaParameters parameters, Object[] values) {
            Matcher expressionParameter = PLACEHOLDER_EXPRESSION_PARAMETER.matcher(queryString);

            List<Integer> removeParamIndex;
            String parameter;
            for(removeParamIndex = new ArrayList(); expressionParameter.find(); queryString = super.positionParameterProcessor(queryString, values, removeParamIndex, parameter)) {
               parameter = expressionParameter.group(1);
            }

            parameter = queryString.replace("?{", "").replace("}", "");
            return new QueryResolveResult.PositionExpressionQueryResolveResult(parameter, removeParamIndex, JpaExtendQueryUtils.toPositionMap(values));
         }
      },
      PlaceholderNameExpressionQueryResolver {
         public boolean match(String queryString, boolean expressionQuery) {
            if (!expressionQuery) {
               return false;
            } else {
               Matcher expressionParameter = PLACEHOLDER_EXPRESSION_PARAMETER.matcher(queryString);
               Matcher nameExpressionParameter = NAME_EXPRESSION_PARAMETER.matcher(queryString);
               return expressionParameter.find() && nameExpressionParameter.find();
            }
         }

         public QueryResolveResult resolve(String queryString, JpaParameters parameters, Object[] values) {
            Map<String, Object> allQueryParams = JpaExtendQueryUtils.getParams(parameters, values);
            List<String> removeParams = new ArrayList();

            String matchExpression;
            for(Matcher expressionParameter = PLACEHOLDER_EXPRESSION_PARAMETER.matcher(queryString); expressionParameter.find(); queryString = super.nameParameterProcessor(queryString, allQueryParams, removeParams, matchExpression)) {
               matchExpression = expressionParameter.group();
            }

            matchExpression = queryString.replace("?{", "").replace("}", "");
            return new QueryResolveResult.NameExpressionQueryResolveResult(matchExpression, removeParams, allQueryParams);
         }
      },
      PositionExpressionQueryResolver {
         public boolean match(String queryString, boolean expressionQuery) {
            if (!expressionQuery) {
               return false;
            } else {
               return !PLACEHOLDER_EXPRESSION_PARAMETER.matcher(queryString).find() && NO_PLACEHOLDER_POSITION_EXPRESSION_PARAMETER.matcher(queryString).find();
            }
         }

         public QueryResolveResult resolve(String queryString, JpaParameters parameters, Object[] values) {
            Matcher expressionParameter = NO_PLACEHOLDER_POSITION_EXPRESSION_PARAMETER.matcher(queryString);

            List<Integer> removeParamIndex;
            String parameter;
            for(removeParamIndex = new ArrayList(); expressionParameter.find(); queryString = super.positionParameterProcessor(queryString, values, removeParamIndex, parameter)) {
               parameter = expressionParameter.group();
            }

            return new QueryResolveResult.PositionExpressionQueryResolveResult(queryString, removeParamIndex, JpaExtendQueryUtils.toPositionMap(values));
         }
      },
      NameExpressionQueryResolver {
         public boolean match(String queryString, boolean expressionQuery) {
            if (!expressionQuery) {
               return false;
            } else {
               return !PLACEHOLDER_EXPRESSION_PARAMETER.matcher(queryString).find() && NO_PLACEHOLDER_NAME_EXPRESSION_PARAMETER.matcher(queryString).find();
            }
         }

         public QueryResolveResult resolve(String queryString, JpaParameters parameters, Object[] values) {
            Map<String, Object> allQueryParams = JpaExtendQueryUtils.getParams(parameters, values);
            List<String> removeParams = new ArrayList();

            String matchExpression;
            for(Matcher expressionParameter = NO_PLACEHOLDER_NAME_EXPRESSION_PARAMETER.matcher(queryString); expressionParameter.find(); queryString = super.nameParameterProcessor(queryString, allQueryParams, removeParams, matchExpression)) {
               matchExpression = expressionParameter.group();
            }

            return new QueryResolveResult.NameExpressionQueryResolveResult(queryString, removeParams, allQueryParams);
         }
      };

      private ExpressionQueryResolverEnum() {
      }

      // $FF: synthetic method
      private static ExpressionQueryResolverEnum[] $values() {
         return new ExpressionQueryResolverEnum[]{EmptyExpressionQueryResolver, PlaceholderPositionExpressionQueryResolver, PlaceholderNameExpressionQueryResolver, PositionExpressionQueryResolver, NameExpressionQueryResolver};
      }
   }
}
