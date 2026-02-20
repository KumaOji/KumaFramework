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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datachanage.listener;

import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datachanage.event.DataVersionLogEvent;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datachanage.model.DataVersionLogData;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datachanage.service.DataVersionLogService;
import java.util.List;
import java.util.Objects;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

/**
 * 注解形式的监听 异步监听日志事件
 *
 * @author kuma
 * @version 2022.03
 * @since 2020/6/3 13:33
 */
public class DataVersionLogListener {

    private final List<DataVersionLogService> dataVersionLogServices;

    public DataVersionLogListener(List<DataVersionLogService> dataVersionLogServices) {
        this.dataVersionLogServices = dataVersionLogServices;
    }

    @Async
    @EventListener(DataVersionLogEvent.class)
    public void saveRequestLog(DataVersionLogEvent event) {
        DataVersionLogData dataVersionLogData = (DataVersionLogData) event.getSource();

        if (Objects.nonNull(dataVersionLogServices) && !dataVersionLogServices.isEmpty()) {
            dataVersionLogServices.stream()
                    .filter(Objects::nonNull)
                    .forEach(service -> service.add(dataVersionLogData));
        }
    }
}
