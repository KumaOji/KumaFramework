/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.model.result.Result
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.security.spring.annotation.NotAuth
 *  io.swagger.v3.oas.annotations.Operation
 *  org.springframework.validation.annotation.Validated
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.kuma.boot.web.controller;

import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.spring.annotation.NotAuth;
import com.kuma.boot.web.annotation.BusinessApi;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@BusinessApi
@Validated
@RestController
@RequestMapping(value={"/request/gateway"})
public class RequestController {
    @NotAuth
    @Operation(summary="\u7f51\u5173\u8bf7\u6c42\u6d4b\u8bd5", description="\u7f51\u5173\u8bf7\u6c42\u6d4b\u8bd5", hidden=true)
    @GetMapping(value={"/test"})
    public Result<Boolean> gatewayTest() {
        LogUtils.info((String)"\u7f51\u5173\u8bf7\u6c42\u6d4b\u8bd5----------------------------", (Object[])new Object[0]);
        return Result.success((Object)true);
    }
}

