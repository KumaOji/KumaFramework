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

package com.kuma.cloud.dubbo.filter;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;

/**
 * Dubbo 上下文透传过滤器.
 *
 * <p>当前服务作为提供方处理入站调用时，若在同一调用链中再作为消费方发起下游 Dubbo 调用，
 * 自动把入站 attachment（鉴权 Token、TraceId、灰度标签等）复制到出站调用，
 * 从而贯穿整条 RPC 调用链。
 *
 * <p>仅作用于消费端（{@code CONSUMER} 分组）；出站调用已显式设置同名 attachment 时不覆盖。
 * 透传开关与 key 列表由 {@link DubboPropagationKeys}（自动配置写入）控制。
 *
 * @author kuma
 */
@Activate(group = CommonConstants.CONSUMER, order = -10000)
public class DubboContextPropagationFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (DubboPropagationKeys.isEnabled()) {
            for (String key : DubboPropagationKeys.getKeys()) {
                // 出站调用已显式设置同名 attachment 时不覆盖
                if (RpcContext.getClientAttachment().getObjectAttachment(key) != null) {
                    continue;
                }
                Object value = RpcContext.getServerAttachment().getObjectAttachment(key);
                if (value != null) {
                    RpcContext.getClientAttachment().setObjectAttachment(key, value);
                }
            }
        }
        return invoker.invoke(invocation);
    }
}
