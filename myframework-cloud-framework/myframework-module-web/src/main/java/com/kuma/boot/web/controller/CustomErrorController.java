/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.enums.ResultEnum
 *  com.kuma.boot.common.model.result.Result
 *  com.kuma.boot.common.utils.json.JacksonUtils
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.boot.autoconfigure.web.ErrorProperties
 *  org.springframework.boot.web.error.ErrorAttributeOptions
 *  org.springframework.boot.webmvc.autoconfigure.error.BasicErrorController
 *  org.springframework.boot.webmvc.error.ErrorAttributes
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.HttpStatusCode
 *  org.springframework.http.ResponseEntity
 *  org.springframework.web.servlet.ModelAndView
 *  org.springframework.web.servlet.View
 *  org.springframework.web.servlet.view.json.JacksonJsonView
 *  tools.jackson.databind.json.JsonMapper
 */
package com.kuma.boot.web.controller;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.common.utils.json.JacksonUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.autoconfigure.error.BasicErrorController;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.json.JacksonJsonView;
import tools.jackson.databind.json.JsonMapper;

public class CustomErrorController
extends BasicErrorController {
    private final JsonMapper jsonMapper;

    public CustomErrorController(JsonMapper jsonMapper, ErrorAttributes errorAttributes, ErrorProperties errorProperties) {
        super(errorAttributes, errorProperties);
        this.jsonMapper = jsonMapper;
    }

    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        Result fail = Result.fail((ResultEnum)ResultEnum.REQUEST_NOT_FOUND);
        Map map = JacksonUtils.toMap((Object)fail);
        return new ResponseEntity((Object)map, HttpStatusCode.valueOf((int)200));
    }

    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        Map body = this.getErrorAttributes(request, ErrorAttributeOptions.defaults());
        HttpStatus status = this.getStatus(request);
        response.setStatus(status.value());
        JacksonJsonView view = new JacksonJsonView(this.jsonMapper);
        view.setContentType("application/json");
        return new ModelAndView((View)view, body);
    }
}

