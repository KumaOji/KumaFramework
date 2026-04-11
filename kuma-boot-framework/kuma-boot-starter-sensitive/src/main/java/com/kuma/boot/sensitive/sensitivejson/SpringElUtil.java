package com.kuma.boot.sensitive.sensitivejson;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.extra.spring.SpringUtil;
import java.lang.reflect.Method;
import java.util.Map;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public final class SpringElUtil {
   private static final ExpressionParser parser = new SpelExpressionParser();
   private static final ParserContext parserContext = new TemplateParserContext();
   private static final BeanResolver beanResolver = new BeanFactoryResolver(SpringUtil.getBeanFactory());

   private SpringElUtil() {
   }

   public static <T> T parse(String el, StandardEvaluationContext context, Class<T> type) {
      context.setBeanResolver(beanResolver);
      return (T)(CharSequenceUtil.startWith(el, parserContext.getExpressionPrefix()) && CharSequenceUtil.endWith(el, parserContext.getExpressionSuffix()) ? parser.parseExpression(el, parserContext).getValue(context, type) : parser.parseExpression(el).getValue(context, type));
   }

   public static StandardEvaluationContext getStandardEvaluationContext(String name, Object value) {
      StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext();
      standardEvaluationContext.setVariable(name, value);
      return standardEvaluationContext;
   }

   public static StandardEvaluationContext getStandardEvaluationContext(Map<String, Object> variable) {
      StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext();
      standardEvaluationContext.setVariables(variable);
      return standardEvaluationContext;
   }

   public static MethodBasedEvaluationContext getMethodBasedEvaluationContext(Method method, Object[] args) {
      return new MethodBasedEvaluationContext((Object)null, method, args, new DefaultParameterNameDiscoverer());
   }
}
