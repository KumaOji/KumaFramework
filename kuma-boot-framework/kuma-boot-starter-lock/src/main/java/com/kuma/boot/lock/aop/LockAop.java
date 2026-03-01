/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.lock.aop;

import com.kuma.boot.common.exception.LockException;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.lock.annotation.Lock;
import com.kuma.boot.lock.enums.LockScopeEnum;
import com.kuma.boot.lock.exception.UnSupportLockException;
import com.kuma.boot.lock.support.DistributedLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import java.util.Objects;
import org.springframework.transaction.support.TransactionSynchronization;
/**
 * 分布式锁切面
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 20:27:36
 */
@Aspect
public class LockAop {

    /**
     * lock的实现类集合
     */
    private final ObjectProvider<DistributedLock> distributedLockProvider;

    /**
     * 用于SpEL表达式解析.
     */
    private final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

    /**
     * 用于获取方法参数定义名字.
     */
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    public LockAop(ObjectProvider<DistributedLock> distributedLockProvider) {
        this.distributedLockProvider = distributedLockProvider;
    }

    @Around("@within(onLock) || @annotation(onLock)")
    public Object aroundLock(ProceedingJoinPoint point, Lock onLock) throws Throwable {
        Assert.notNull(onLock, "lock is null");
        LockScopeEnum scope = onLock.scope();
        DistributedLock distributedLock = distributedLockProvider
                .orderedStream()
                .filter(lock -> lock.scope().equals(scope))
                .findFirst()
                .orElseThrow(() -> new UnSupportLockException("缺少scope：" + scope + "的锁实现"));

        String lockKey = onLock.key();
        if (StrUtil.isEmpty(lockKey)) {
            throw new LockException("lockKey is null");
        }

        if (lockKey.contains("#")) {
            MethodSignature methodSignature = (MethodSignature) point.getSignature();
            Object[] args = point.getArgs();
            lockKey = getValBySpEl(lockKey, methodSignature, args);
        }

        boolean async = !LockScopeEnum.STANDALONE_LOCK.equals(scope) && onLock.async();
        boolean isLock = distributedLock.tryLock(
                onLock.type(), lockKey, onLock.leaseTime(), onLock.waitTime(), onLock.unit(), async);

        try {
            if (isLock) {
                LogUtils.info("获取锁[成功]，加锁完成，开始执行业务逻辑...");

                if(onLock.transactional()){
                    // 注册事务同步回调，在事务提交后释放锁
                    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            distributedLock.unlock();
                            LogUtils.info("abandon lock after commit");
                        }

                        @Override
                        public void afterCompletion(int status) {
                            if (status != STATUS_COMMITTED) {
                                distributedLock.unlock();
                                LogUtils.info("abandon lock after completion");
                            }
                        }
                    });
                }

                return point.proceed();
            }

            LogUtils.error("获取分布式锁[失败]");
            throw new LockException("获取锁失败!");
        } finally {
            if (isLock) {
                distributedLock.unlock();
            }
        }
    }

    /**
     * 解析spEL表达式
     *
     * @param spEl            spEl
     * @param methodSignature 方法签名
     * @param args            参数
     * @return 表达式值
     * @since 2021-09-02 20:28:11
     */
    private String getValBySpEl(String spEl, MethodSignature methodSignature, Object[] args) {
        // 获取方法形参名数组
        String[] paramNames = nameDiscoverer.getParameterNames(methodSignature.getMethod());
        if (paramNames != null && paramNames.length > 0) {
            Expression expression = spelExpressionParser.parseExpression(spEl);
            // spring的表达式上下文对象
            EvaluationContext context = new StandardEvaluationContext();
            // 给上下文赋值
            for (int i = 0; i < args.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
            return Objects.requireNonNull(expression.getValue(context)).toString();
        }
        return null;
    }
}
