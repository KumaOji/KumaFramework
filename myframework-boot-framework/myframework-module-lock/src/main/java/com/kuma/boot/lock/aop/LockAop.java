/*
 *  cn.hutool.core.util.StrUtil
 *  com.kuma.boot.common.exception.LockException
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.aspectj.lang.ProceedingJoinPoint
 *  org.aspectj.lang.annotation.Around
 *  org.aspectj.lang.annotation.Aspect
 *  org.aspectj.lang.reflect.MethodSignature
 *  org.springframework.beans.factory.ObjectProvider
 *  org.springframework.core.DefaultParameterNameDiscoverer
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.Expression
 *  org.springframework.expression.spel.standard.SpelExpressionParser
 *  org.springframework.expression.spel.support.StandardEvaluationContext
 *  org.springframework.transaction.support.TransactionSynchronization
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 *  org.springframework.util.Assert
 */
package com.kuma.boot.lock.aop;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.exception.LockException;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.lock.annotation.Lock;
import com.kuma.boot.lock.enums.LockScopeEnum;
import com.kuma.boot.lock.exception.UnSupportLockException;
import com.kuma.boot.lock.support.DistributedLock;
import java.util.Objects;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

@Aspect
public class LockAop {
    private final ObjectProvider<DistributedLock> distributedLockProvider;
    private final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    public LockAop(ObjectProvider<DistributedLock> distributedLockProvider) {
        this.distributedLockProvider = distributedLockProvider;
    }

    @Around(value="@within(onLock) || @annotation(onLock)")
    public Object aroundLock(ProceedingJoinPoint point, Lock onLock) throws Throwable {
        Assert.notNull((Object)onLock, (String)"lock is null");
        LockScopeEnum scope = onLock.scope();
        final DistributedLock distributedLock = this.distributedLockProvider.orderedStream().filter(lock -> lock.scope().equals((Object)scope)).findFirst().orElseThrow(() -> new UnSupportLockException("\u7f3a\u5c11scope\uff1a" + String.valueOf((Object)scope) + "\u7684\u9501\u5b9e\u73b0"));
        String lockKey = onLock.key();
        if (StrUtil.isEmpty((CharSequence)lockKey)) {
            throw new LockException("lockKey is null");
        }
        if (lockKey.contains("#")) {
            MethodSignature methodSignature = (MethodSignature)point.getSignature();
            Object[] args = point.getArgs();
            lockKey = this.getValBySpEl(lockKey, methodSignature, args);
        }
        boolean async = !LockScopeEnum.STANDALONE_LOCK.equals((Object)scope) && onLock.async();
        boolean isLock = distributedLock.tryLock(onLock.type(), lockKey, onLock.leaseTime(), onLock.waitTime(), onLock.unit(), async);
        try {
            if (isLock) {
                LogUtils.info((String)"\u83b7\u53d6\u9501[\u6210\u529f]\uff0c\u52a0\u9501\u5b8c\u6210\uff0c\u5f00\u59cb\u6267\u884c\u4e1a\u52a1\u903b\u8f91...", (Object[])new Object[0]);
                if (onLock.transactional()) {
                    TransactionSynchronizationManager.registerSynchronization((TransactionSynchronization)new TransactionSynchronization(){
                        {
                            Objects.requireNonNull(this$0);
                        }

                        public void afterCommit() {
                            distributedLock.unlock();
                            LogUtils.info((String)"abandon lock after commit", (Object[])new Object[0]);
                        }

                        public void afterCompletion(int status) {
                            if (status != 0) {
                                distributedLock.unlock();
                                LogUtils.info((String)"abandon lock after completion", (Object[])new Object[0]);
                            }
                        }
                    });
                }
                Object object = point.proceed();
                return object;
            }
            LogUtils.error((String)"\u83b7\u53d6\u5206\u5e03\u5f0f\u9501[\u5931\u8d25]", (Object[])new Object[0]);
            throw new LockException("\u83b7\u53d6\u9501\u5931\u8d25!");
        }
        finally {
            if (isLock) {
                distributedLock.unlock();
            }
        }
    }

    private String getValBySpEl(String spEl, MethodSignature methodSignature, Object[] args) {
        String[] paramNames = this.nameDiscoverer.getParameterNames(methodSignature.getMethod());
        if (paramNames != null && paramNames.length > 0) {
            Expression expression = this.spelExpressionParser.parseExpression(spEl);
            StandardEvaluationContext context = new StandardEvaluationContext();
            for (int i = 0; i < args.length; ++i) {
                context.setVariable(paramNames[i], args[i]);
            }
            return Objects.requireNonNull(expression.getValue((EvaluationContext)context)).toString();
        }
        return null;
    }
}

