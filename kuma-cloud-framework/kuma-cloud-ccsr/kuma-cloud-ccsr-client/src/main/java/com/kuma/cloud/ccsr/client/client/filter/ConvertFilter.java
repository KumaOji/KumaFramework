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

package com.kuma.cloud.ccsr.client.client.filter;

import com.kuma.cloud.ccsr.api.grpc.auto.Response;
import com.kuma.cloud.ccsr.api.result.ResponseHelper;
import com.kuma.cloud.ccsr.client.client.AbstractClient;
import com.kuma.cloud.ccsr.client.context.CcsrContext;
import com.kuma.cloud.ccsr.client.option.RequestOption;
import com.kuma.cloud.ccsr.client.request.Payload;
import com.kuma.cloud.ccsr.common.enums.ResponseCode;
import com.kuma.cloud.ccsr.common.utils.GsonUtils;
import com.kuma.cloud.ccsr.common.utils.MD5Utils;

/**
 * ConvertFilter
 *
 * @author kuma
 * @version 2026.02
 * @since 2025-12-19 09:30:45
 */
public class ConvertFilter<OPTION extends RequestOption> extends AbstractFilter<OPTION> {

    public ConvertFilter( AbstractClient<OPTION> client ) {
        super(client);
    }

    @Override
    protected Response doPreFilter( CcsrContext context, OPTION option, Payload request ) {

        try {
            String configDataString = GsonUtils.getInstance().toJson(request.getConfigData());
            String md5 = MD5Utils.calculateMD5(configDataString);
            context.setConfigDataString(configDataString);
            context.setNamespace(request.getNamespace());
            context.setMd5(md5);
        } catch (Exception e) {
            return ResponseHelper.error(ResponseCode.CLIENT_ERROR.getCode(), e.getMessage());
        }

        return null;
    }

    @Override
    protected Response doPostFilter(
            CcsrContext context, OPTION option, Payload request, Response response ) {
        // TODO
        return response;
    }
}
