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

package com.kuma.boot.web.controller;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.servlet.RequestDispatcher;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.DefaultErrorAttributes;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

/**
 * 全局异常处理
 */
@SuppressWarnings("unchecked")
public class CustomErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(
            WebRequest webRequest, ErrorAttributeOptions options) {
        // 请求地址
        String requestUrl = this.getAttr(webRequest, RequestDispatcher.ERROR_REQUEST_URI);
        if (StringUtils.isBlank(requestUrl)) {
            requestUrl = this.getAttr(webRequest, RequestDispatcher.FORWARD_REQUEST_URI);
        }
        // status code
        Integer status = this.getAttr(webRequest, RequestDispatcher.ERROR_STATUS_CODE);
        // error
        Throwable error = getError(webRequest);
        LogUtils.error("URL:{} error status:{}", requestUrl, status, error);
        // Result<Object> result;
        // if (error instanceof ServiceException serviceException) {
        // 	result = serviceException.getResult();
        // 	result = Optional.ofNullable(result).orElse(Result.fail(500));
        // } else {
        // 	result = Result.fail(SystemCode.FAILURE, "System error status:" + status);
        // }
        // return JsonUtils.toMap(result);
        return super.getErrorAttributes(webRequest, options);
    }

    @Nullable
    private <T> T getAttr(WebRequest webRequest, String name) {
        return (T) webRequest.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
    }
}
