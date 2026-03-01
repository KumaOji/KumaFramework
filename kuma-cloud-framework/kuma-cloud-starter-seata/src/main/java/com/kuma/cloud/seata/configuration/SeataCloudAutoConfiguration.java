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

package com.kuma.cloud.seata.configuration;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.cloud.seata.properties.SeataCloudProperties;
import org.apache.seata.core.context.RootContext;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * SeataDataSourceConfiguration
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-07 20:54:47
 */
@AutoConfiguration
@EnableConfigurationProperties({SeataCloudProperties.class})
@ConditionalOnProperty(prefix = SeataCloudProperties.PREFIX, name = "enabled", havingValue = "true")
public class SeataCloudAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(SeataCloudAutoConfiguration.class, StarterNameConstants.SEATA_CLOUD_STARTER);
    }

//    @Bean
//    @ConditionalOnClass(RequestInterceptor.class)
//    public SeataInterceptor seataInterceptor() {
//        return new SeataInterceptor();
//    }

    /**
     * SeataInterceptor
     *
     * @author kuma
     * @version 2022.03
     * @since 2020/10/22 17:00
     */
//    public static class SeataInterceptor implements RequestInterceptor {
//
//        // 这里在feign请求的header中加入xid
//        // 注意：这里一定要将feign.hystrix.enabled设为false，因为为true时feign是通过线程池调用，而XID并不是一个InheritablThreadLocal变量。
//        @Override
//        public void apply(RequestTemplate template) {
//            String xid = RootContext.getXID();
//            if (StrUtil.isNotBlank(xid)) {
//                template.header("xid", xid);
//            }
//        }
//    }

    // @Bean
    // public SeataAspect seataAspect() {
    //	return new SeataAspect();
    // }
    //
    // @Aspect
    // public static class SeataAspect extends AspectUtil {
    //
    //	@Before("execution(* com.kuma.cloud.*.biz.service.*.*(..))")
    //	public void before(JoinPoint joinPoint) throws TransactionException {
    //		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    //		Method method = signature.getMethod();
    //		GlobalTransaction tx = GlobalTransactionContext.getCurrentOrCreate();
    //		tx.begin(300000, "tran");
    //		LogUtil.info("**********创建分布式事务完毕 {0}", tx.getXid());
    //	}
    //
    //	@AfterThrowing(throwing = "e", pointcut = "execution(*
    // com.kuma.cloud.*.biz.service.*.*(..))")
    //	public void doRecoveryActions(Throwable e) throws Throwable {
    //		LogUtil.info("方法执行异常:{0}", e.getMessage());
    //		if (!StrUtil.isBlank(RootContext.getXID())) {
    //			GlobalTransactionContext.reload(RootContext.getXID()).rollback();
    //		}
    //		throw e;
    //	}
    //
    // }
}
