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

package com.kuma.boot.data.mybatis.delay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** 测试类 */
@RestController
@ConditionalOnProperty(prefix = "kuma.boot.data.mybatis.delay", name = "enabled", havingValue = "true", matchIfMissing = false)
public class DelayController {

    @Autowired private AppDelayMessageService appDelayMessageService;

    /**发布一个超时的监听*/
    @GetMapping("/testPublishDelay")
    public void testPublishDelay() {
        appDelayMessageService.publish(
                "00000000011111111",
                10,
                AppDelayMessage.Type.TYPE1,
                AppDelayMessage.Stage.REAL_COMPILE,
                AppAuditCallback.class);
    }

    /**当业务未超时，修改消息状态*/
    @GetMapping("/testChangeDelay")
    public void testChangeDelay() {
        // 修改延迟消息状态
        appDelayMessageService.changeToProcessed(
                "00000000011111111", AppDelayMessage.Stage.REAL_COMPILE);
    }
}
