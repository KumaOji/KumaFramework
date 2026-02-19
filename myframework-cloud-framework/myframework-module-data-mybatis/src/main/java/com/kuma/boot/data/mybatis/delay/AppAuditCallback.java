/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

import com.alibaba.fastjson2.JSON;
import com.kuma.boot.common.utils.log.LogUtils;
import cn.hutool.core.util.StrUtil;

/** 超时处理 */
@com.kuma.boot.data.mybatis.delay.Callback("AppAuditCallback")
public class AppAuditCallback implements com.kuma.boot.data.mybatis.delay.TimeoutCallback {

    /** 超时消息 */
    private static final String MESSAGE = "{}超时, 请重新审核应用";

    @Override
    public void handle(com.kuma.boot.data.mybatis.delay.AppDelayMessage message) {
        LogUtils.info("@@ 应用超时异常处理, AppDelayMessage : {}", JSON.toJSONString(message));
        // 回调的业务处理
        doAnythings(formatMessage(message.getStage()));
    }

    /**
     * 格式化超时的消息
     * @param stage 超时阶段(COMPILE, TEST)
     * @return 消息内容
     */
    private String formatMessage(com.kuma.boot.data.mybatis.delay.AppDelayMessage.Stage stage) {
        // 超时错误消息
        if (com.kuma.boot.data.mybatis.delay.AppDelayMessage.Stage.REAL_COMPILE.equals(stage)) {
            return StrUtil.format(MESSAGE, "XXXX业务处理");
        }
        return MESSAGE;
    }

    public void doAnythings(String msg) {
        LogUtils.info("@@ 超时异常业务处理 : {}", msg);
    }
}
