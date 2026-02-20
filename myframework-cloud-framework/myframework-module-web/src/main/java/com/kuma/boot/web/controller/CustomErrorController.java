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

import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.common.utils.json.JacksonUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.autoconfigure.error.BasicErrorController;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.JacksonJsonView;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

import static com.kuma.boot.common.enums.ResultEnum.REQUEST_NOT_FOUND;

/**
 * 更改 html 请求异常为 ajax，对前后端分离更加友好
 */
public class CustomErrorController extends BasicErrorController {
    private final JsonMapper jsonMapper;

    public CustomErrorController(
            JsonMapper jsonMapper,
            ErrorAttributes errorAttributes,
            ErrorProperties errorProperties) {
        super(errorAttributes, errorProperties);
        this.jsonMapper = jsonMapper;
    }

    /**
     * 覆盖默认的JSON响应
     */
    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        Result<String> fail = Result.fail(REQUEST_NOT_FOUND);
        Map<String, Object> map = JacksonUtils.toMap(fail);
        return new ResponseEntity<>(map, HttpStatusCode.valueOf(200));
    }

    /**
     * 覆盖默认的HTML响应
     */
    // @Override
    // public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
    // 	// 请求的状态
    // 	response.setStatus(getStatus(request).value());
    // 	// 指定自定义的视图
    // 	//log.info("覆盖默认的HTML响应:{}", "https://vue.ruoyi.vip/404");
    // 	return new ModelAndView("redirect:https://vue.ruoyi.vip/404");
    // }

    @Override
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> body = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        HttpStatus status = getStatus(request);
        response.setStatus(status.value());
        JacksonJsonView view = new JacksonJsonView(jsonMapper);
        view.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return new ModelAndView(view, body);
    }
}
