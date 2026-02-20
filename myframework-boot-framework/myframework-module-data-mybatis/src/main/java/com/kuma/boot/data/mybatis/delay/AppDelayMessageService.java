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

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 应用创建超时消息处理服务接口
 */
public interface AppDelayMessageService extends IService<AppDelayMessage> {

    /**
     * 发布延迟消息
     *
     * @param appId    应用ID
     * @param timeout  超时时长(H)
     * @param type     TYPE_ONE; TYPE_TWO;
     * @param stage    编译:COMPILE; 测试:TEST;
     * @param callback 回调函数
     */
    void publish(
            String appId,
            Integer timeout,
            AppDelayMessage.Type type,
            AppDelayMessage.Stage stage,
            Class callback);

    /**
     * 超时回调
     *
     * @param message 消息体
     */
    void callback(AppDelayMessage message);

    /**
     * 修改延迟消息状态[PROCESSED]
     *
     * @param appId 应用ID
     * @param stage 编译:COMPILE; 测试:TEST;
     * @return 成功:true, 失败：false
     */
    boolean changeToProcessed(String appId, AppDelayMessage.Stage stage);
}
