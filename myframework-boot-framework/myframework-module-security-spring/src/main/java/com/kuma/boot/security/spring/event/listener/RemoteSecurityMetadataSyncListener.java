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

package com.kuma.boot.security.spring.event.listener;

import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.security.spring.access.security.SecurityAttribute;
import com.kuma.boot.security.spring.authorization.SecurityMetadataSourceAnalyzer;
import com.kuma.boot.security.spring.event.RemoteSecurityMetadataSyncEvent;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

/**
 * <p>Security Metadata 数据同步监听
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:00:22
 */
public class RemoteSecurityMetadataSyncListener
        implements ApplicationListener<RemoteSecurityMetadataSyncEvent> {

    /**
     * 日志
     */
    private static final Logger log =
            LoggerFactory.getLogger(RemoteSecurityMetadataSyncListener.class);

    /**
     * 安全元数据来源分析仪
     */
    private final SecurityMetadataSourceAnalyzer securityMetadataSourceAnalyzer;

    //    private final ServiceMatcher serviceMatcher;

    /**
     * 远程安全元数据同步监听器
     *
     * @param securityMetadataSourceAnalyzer 安全元数据来源分析仪
     * @since 2023-07-04 10:00:22
     */
    public RemoteSecurityMetadataSyncListener(
            SecurityMetadataSourceAnalyzer securityMetadataSourceAnalyzer
            //											  ServiceMatcher serviceMatcher
    ) {
        this.securityMetadataSourceAnalyzer = securityMetadataSourceAnalyzer;
        //        this.serviceMatcher = serviceMatcher;
    }

    /**
     * 应用程序事件
     *
     * @param event 事件
     * @since 2023-07-04 10:00:22
     */
    @Override
    public void onApplicationEvent(RemoteSecurityMetadataSyncEvent event) {

        //        if (!serviceMatcher.isFromSelf(event)) {
        log.info("Remote security metadata sync listener, response event!");

        String data = event.getData();
        if (StringUtils.isNotBlank(data)) {
            List<SecurityAttribute> securityMetadata =
                    JacksonUtils.toList(data, SecurityAttribute.class);

            if (CollectionUtils.isNotEmpty(securityMetadata)) {
                //                   log.info("Got security attributes from service [{}], current
                // [{}] start to process
                // security attributes.", event.getOriginService(), event.getDestinationService());
                securityMetadataSourceAnalyzer.processSecurityAttribute(securityMetadata);
            }
        }
        //        }
    }
}
