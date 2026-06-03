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

package com.kuma.boot.security.satoken.exception;

import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;
import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.model.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Sa-Token 异常统一处理器.
 *
 * <p>将 Sa-Token 鉴权失败异常转为框架统一的 {@link Result} 响应体，
 * HTTP 状态码遵循 REST 规范（401 未登录，403 权限不足）。
 *
 * @author kuma
 */
@RestControllerAdvice
public class SaTokenExceptionHandler {

    /** 未登录 / Token 过期 / Token 无效 */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(NotLoginException.class)
    public Result<?> handleNotLogin(NotLoginException e) {
        return Result.fail(ResultEnum.UNAUTHORIZED);
    }

    /** 缺少接口权限码 */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(NotPermissionException.class)
    public Result<?> handleNotPermission(NotPermissionException e) {
        return Result.of(
                StatusEnum.FAILURE.name(),
                ResultEnum.FORBIDDEN.codeDesc(),
                "缺少权限: " + e.getPermission(),
                null);
    }

    /** 缺少角色 */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(NotRoleException.class)
    public Result<?> handleNotRole(NotRoleException e) {
        return Result.of(
                StatusEnum.FAILURE.name(),
                ResultEnum.FORBIDDEN.codeDesc(),
                "缺少角色: " + e.getRole(),
                null);
    }

    /** 账号被封禁 */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(DisableServiceException.class)
    public Result<?> handleDisableService(DisableServiceException e) {
        return Result.of(
                StatusEnum.FAILURE.name(),
                ResultEnum.FORBIDDEN.codeDesc(),
                "账号已被封禁，解封时间: " + e.getDisableTime() + " 秒后",
                null);
    }

    /** 其他 Sa-Token 异常兜底 */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(SaTokenException.class)
    public Result<?> handleSaTokenException(SaTokenException e) {
        return Result.of(
                StatusEnum.FAILURE.name(),
                ResultEnum.UNAUTHORIZED.codeDesc(),
                e.getMessage(),
                null);
    }
}
