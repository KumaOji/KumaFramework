package com.kuma.boot.idempotent.idempotentenhance.core.aspect;

import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.idempotent.idempotentenhance.core.annotation.Idempotent;
import com.kuma.boot.idempotent.idempotentenhance.core.helper.IdempotentTemplate;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * 幂等切面
 *
 * @author wenpan 2023/01/05 22:56
 */
@Aspect
public class IdempotentAspect {

	@Around("@annotation(idempotent)")
	public Object idempotentAround( ProceedingJoinPoint joinPoint, Idempotent idempotent ) throws Throwable {
		// 解析幂等关键要素
		String source = idempotent.source();
		String operationType = idempotent.operationType();
		String businessKey = idempotent.businessKey();

		// 解析目标方法并解析参数名称
		Method method = IdempotentAnnotationProvider.getMethod(joinPoint);
		String[] paramNames = IdempotentAnnotationProvider.getParameterNames(method);

		// el表达式解析
		if (ArrayUtils.isNotEmpty(paramNames)) {
			EvaluationContext context = buildContext(paramNames, joinPoint.getArgs());
			source = IdempotentAnnotationProvider.parse(idempotent.source(), context);
			operationType = IdempotentAnnotationProvider.parse(idempotent.operationType(), context);
			businessKey = IdempotentAnnotationProvider.parse(idempotent.businessKey(), context);
		}

		IdempotentTemplate idempotentHelper = ContextUtils.getBean(IdempotentTemplate.class);

		// 幂等执行目标方法
		return idempotentHelper.invoke(source, operationType, businessKey,
			method.getGenericReturnType(), idempotent.determineCurrentLookupKey(), joinPoint::proceed);
	}

	/**
	 * <p>
	 * 根据参数名和参数值构建EvaluationContext
	 * </p>
	 *
	 * @param paramNames 参数名称集合
	 * @param paramValues 参数值集合
	 * @return org.springframework.expression.EvaluationContext
	 */
	private EvaluationContext buildContext( String[] paramNames, Object[] paramValues ) {
		EvaluationContext context = new StandardEvaluationContext();
		if (paramNames == null || paramValues == null) {
			return context;
		}
		for (int i = 0; i < paramValues.length; i++) {
			context.setVariable(paramNames[i], paramValues[i]);
		}
		return context;
	}

}
