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

package com.kuma.boot.i18n.exception;

import com.kuma.boot.common.model.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * {@link I18nException} 全局异常处理器.
 *
 * <p>将异常携带的 {@code messageCode} 翻译为当前请求 Locale 对应的语言文本，
 * 并以 {@code Result.fail(message)} 的形式返回给客户端。
 * 若 code 在 MessageSource 中不存在，则直接使用 code 本身作为消息。
 *
 * @author kuma
 */
@RestControllerAdvice
public class I18nExceptionHandler {

    private final MessageSource messageSource;

    public I18nExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(I18nException.class)
    public Result<?> handleI18nException(I18nException e) {
        String message;
        try {
            message = messageSource.getMessage(
                    e.getMessageCode(), e.getArgs(), LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException ex) {
            message = e.getMessageCode();
        }
        return Result.fail(message);
    }
}
