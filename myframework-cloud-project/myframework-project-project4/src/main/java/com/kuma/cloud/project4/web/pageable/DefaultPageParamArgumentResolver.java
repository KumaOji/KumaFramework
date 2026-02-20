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

package com.kuma.cloud.project4.web.pageable;

import com.kuma.boot.common.model.request.PageParam;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 默认分页参数解析器
 *
 * @author kuma
 */
@Slf4j
public class DefaultPageParamArgumentResolver extends PageParamArgumentResolverSupport implements PageParamArgumentResolver {

    public DefaultPageParamArgumentResolver(int maxPageSize, String pageParameterName, String sizeParameterName,
            String sortParameterName) {
        setMaxPageSize(maxPageSize);
        setPageParameterName(pageParameterName);
        setSizeParameterName(sizeParameterName);
        setSortParameterName(sortParameterName);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return PageParam.class.isAssignableFrom(parameter.getParameterType());
    }

    @NonNull
    @Override
    public PageParam resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            return new PageParam();
        }

        PageParam pageParam = getPageParam(parameter, request);
        paramValidate(parameter, mavContainer, webRequest, binderFactory, pageParam);
        return pageParam;
    }
}
