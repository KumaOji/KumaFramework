/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson2.JSON
 *  com.alibaba.fastjson2.JSONObject
 *  com.kuma.boot.common.utils.aop.AopUtils
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.lock.support.DistributedLock
 *  jakarta.servlet.http.HttpServletRequest
 *  org.aspectj.lang.JoinPoint
 *  org.aspectj.lang.annotation.After
 *  org.aspectj.lang.annotation.Aspect
 *  org.aspectj.lang.annotation.Before
 *  org.aspectj.lang.annotation.Pointcut
 *  org.aspectj.lang.reflect.CodeSignature
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.web.context.request.RequestContextHolder
 *  org.springframework.web.context.request.ServletRequestAttributes
 */
package com.kuma.boot.idempotent.aop;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.kuma.boot.common.utils.aop.AopUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.idempotent.annotation.Idempotent;
import com.kuma.boot.idempotent.enums.IdempotentTypeEnum;
import com.kuma.boot.idempotent.exception.IdempotentException;
import com.kuma.boot.lock.support.DistributedLock;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
public class IdempotentAspect {
    private final ThreadLocal<String> PER_FIX_KEY = new ThreadLocal();
    private final boolean enable = true;
    private static final String HEADER_RID_KEY = "RID";
    private static final String REDIS_KEY_PREFIX = "RID:";
    private static final int LOCK_WAIT_TIME = 10;
    private final DistributedLock distributedLock;

    public IdempotentAspect(DistributedLock distributedLock) {
        this.distributedLock = distributedLock;
    }

    @Pointcut(value="@annotation(com.kuma.boot.idempotent.annotation.Idempotent)")
    public void watchIde() {
    }

    @Before(value="watchIde()")
    public void doBefore(JoinPoint joinPoint) throws Exception {
        Idempotent idempotent = (Idempotent)AnnotationUtils.getAnnotation((Method)AopUtils.getMethod((JoinPoint)joinPoint), Idempotent.class);
        if (null != idempotent) {
            String key;
            ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
            if (null == attributes) {
                throw new IdempotentException("\u8bf7\u6c42\u6570\u636e\u4e3a\u7a7a");
            }
            HttpServletRequest request = attributes.getRequest();
            if (idempotent.ideTypeEnum() == IdempotentTypeEnum.ALL || idempotent.ideTypeEnum() == IdempotentTypeEnum.RID) {
                String rid = "111";
                try {
                    if (StringUtils.isNotBlank((String)rid)) {
                        boolean result = this.distributedLock.tryLock(REDIS_KEY_PREFIX + rid, 10L, TimeUnit.SECONDS);
                        if (result) {
                            LogUtils.error((String)"\u547d\u4e2dRID\u91cd\u590d\u8bf7\u6c42", (Object[])new Object[0]);
                            throw new IdempotentException("\u91cd\u590d\u8bf7\u6c42");
                        }
                        LogUtils.debug((String)"msg1=\u5f53\u524d\u8bf7\u6c42\u5df2\u6210\u529f\u8bb0\u5f55,\u4e14\u6807\u8bb0\u4e3a0\u672a\u5904\u7406,,{}={}", (Object[])new Object[]{HEADER_RID_KEY, rid});
                    } else {
                        LogUtils.warn((String)("msg1=header\u6ca1\u6709rid,\u9632\u91cd\u590d\u63d0\u4ea4\u529f\u80fd\u5931\u6548,,remoteHost={}" + request.getRemoteHost()), (Object[])new Object[0]);
                    }
                }
                catch (Exception e) {
                    LogUtils.error((String)"\u83b7\u53d6redis\u9501\u53d1\u751f\u5f02\u5e38", (Object[])new Object[]{e});
                    throw new IdempotentException("\u670d\u52a1\u5185\u90e8\u9519\u8bef\u3001\u8bf7\u8054\u7cfb\u7ba1\u7406\u5458");
                }
            }
            if ((idempotent.ideTypeEnum() == IdempotentTypeEnum.ALL || idempotent.ideTypeEnum() == IdempotentTypeEnum.KEY) && StringUtils.isNotBlank((String)(key = idempotent.key()))) {
                String val = "";
                Object[] paramValues = joinPoint.getArgs();
                String[] paramNames = ((CodeSignature)joinPoint.getSignature()).getParameterNames();
                for (int i = 0; i < paramNames.length; ++i) {
                    String params = JSON.toJSONString((Object)paramValues[i]);
                    if (params.startsWith("{")) {
                        JSONObject jsonObject = JSON.parseObject((String)params);
                        val = jsonObject.getString(key);
                        continue;
                    }
                    if (key.equals(paramNames[i])) {
                        val = params;
                        continue;
                    }
                    LogUtils.warn((String)"\u81ea\u5b9a\u4e49\u7684key,\u5728\u8bf7\u6c42\u53c2\u6570\u4e2d\u6ca1\u6709\u6b64\u53c2\u6570,\u9632\u91cd\u590d\u63d0\u4ea4\u529f\u80fd\u5931\u6548", (Object[])new Object[0]);
                }
                Object perFix = idempotent.perFix();
                if (StringUtils.isNotBlank((String)val)) {
                    perFix = (String)perFix + ":" + val;
                    try {
                        boolean result = this.distributedLock.tryLock((String)perFix, 10L, TimeUnit.SECONDS);
                        if (result) {
                            String targetName = joinPoint.getTarget().getClass().getName();
                            String methodName = joinPoint.getSignature().getName();
                            LogUtils.error((String)"\u4e0d\u5141\u8bb8\u91cd\u590d\u6267\u884c,,key={},,targetName={},,methodName={}", (Object[])new Object[]{perFix, targetName, methodName});
                            throw new IdempotentException("\u4e0d\u5141\u8bb8\u91cd\u590d\u63d0\u4ea4");
                        }
                        this.PER_FIX_KEY.set((String)perFix);
                        LogUtils.info((String)"msg1=\u5f53\u524d\u8bf7\u6c42\u5df2\u6210\u529f\u9501\u5b9a:{}", (Object[])new Object[]{perFix});
                    }
                    catch (Exception e) {
                        LogUtils.error((String)"\u83b7\u53d6\u9501\u53d1\u751f\u5f02\u5e38", (Object[])new Object[]{e});
                        throw new IdempotentException("\u670d\u52a1\u5185\u90e8\u9519\u8bef\u3001\u8bf7\u8054\u7cfb\u7ba1\u7406\u5458");
                    }
                } else {
                    LogUtils.warn((String)"\u81ea\u5b9a\u4e49\u7684key,\u5728\u8bf7\u6c42\u53c2\u6570\u4e2dvalue\u4e3a\u7a7a,\u9632\u91cd\u590d\u63d0\u4ea4\u529f\u80fd\u5931\u6548", (Object[])new Object[0]);
                }
            }
        }
    }

    @After(value="watchIde()")
    public void doAfter(JoinPoint joinPoint) throws Throwable {
        block10: {
            try {
                String key;
                Idempotent idempotent = (Idempotent)AopUtils.getAnnotation((JoinPoint)joinPoint, Idempotent.class);
                if (null == idempotent) break block10;
                if (idempotent.ideTypeEnum() == IdempotentTypeEnum.ALL || idempotent.ideTypeEnum() == IdempotentTypeEnum.RID) {
                    ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
                    assert (attributes != null);
                    HttpServletRequest request = attributes.getRequest();
                    String rid = "111";
                    if (StringUtils.isNotBlank((String)rid)) {
                        try {
                            this.distributedLock.unlock();
                            LogUtils.info((String)"msg1=\u5f53\u524d\u8bf7\u6c42\u5df2\u6210\u529f\u5904\u7406,,rid={}", (Object[])new Object[]{rid});
                            this.PER_FIX_KEY.remove();
                        }
                        catch (Exception e) {
                            LogUtils.error((String)"\u91ca\u653eredis\u9501\u5f02\u5e38", (Object[])new Object[]{e});
                        }
                    }
                }
                if ((idempotent.ideTypeEnum() == IdempotentTypeEnum.ALL || idempotent.ideTypeEnum() == IdempotentTypeEnum.KEY) && StringUtils.isNotBlank((String)(key = idempotent.key())) && StringUtils.isNotBlank((String)this.PER_FIX_KEY.get())) {
                    try {
                        this.distributedLock.unlock();
                        LogUtils.info((String)"msg1=\u5f53\u524d\u8bf7\u6c42\u5df2\u6210\u529f\u91ca\u653e,,key={}", (Object[])new Object[]{this.PER_FIX_KEY.get()});
                        this.PER_FIX_KEY.remove();
                    }
                    catch (Exception e) {
                        LogUtils.error((String)"\u91ca\u653eredis\u9501\u5f02\u5e38", (Object[])new Object[]{e});
                    }
                }
            }
            catch (Exception e) {
                LogUtils.error((String)e.getMessage(), (Object[])new Object[]{e});
            }
        }
    }
}

