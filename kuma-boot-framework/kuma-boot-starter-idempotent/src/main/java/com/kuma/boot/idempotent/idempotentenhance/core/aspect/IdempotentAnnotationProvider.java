package com.kuma.boot.idempotent.idempotentenhance.core.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Method;

/**
 * 幂等注解Provider
 *
 * @author wenpan 2023/01/06 21:23
 */
public class IdempotentAnnotationProvider {

    private final static ParameterNameDiscoverer NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    private final static ExpressionParser PARSER = new SpelExpressionParser();

    /**
     * 从连接点中取目标方法参数名称列表
     *
     * @param joinPoint 连接点
     * @return java.lang.String[]
     */
    public static String[] getParameterNames(ProceedingJoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        return NAME_DISCOVERER.getParameterNames(method);
    }

    /**
     * 获取method的参数名称列表
     *
     * @param method method
     * @return java.lang.String[]
     */
    public static String[] getParameterNames(Method method) {
        return NAME_DISCOVERER.getParameterNames(method);
    }

    /**
     * 解析表达式
     *
     * @param expressionStr 待解析的表达式字符串
     * @param context       context
     * @return java.lang.String
     */
    public static String parse(String expressionStr, EvaluationContext context) {
        // 解析过后的Spring表达式对象
        Expression expression = PARSER.parseExpression(expressionStr);
        // 表达式从上下文中计算出实际参数值
        return expression.getValue(context, String.class);
    }

    /**
     * 从连接点中获取拦截的方法
     *
     * @param joinPoint joinPoint
     * @return java.lang.reflect.Method
     */
    public static Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        if (method.getDeclaringClass().isInterface()) {
            try {
                method = joinPoint.getTarget().getClass().getDeclaredMethod(signature.getName(),
                        method.getParameterTypes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return method;
    }

}
