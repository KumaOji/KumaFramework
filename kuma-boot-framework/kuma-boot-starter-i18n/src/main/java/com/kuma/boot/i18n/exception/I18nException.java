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

/**
 * 携带国际化消息 code 的业务异常.
 *
 * <p>抛出此异常后，{@link I18nExceptionHandler} 会根据当前请求 Locale 自动将
 * {@code messageCode} 翻译为对应语言并返回 {@code Result.fail(message)}。
 *
 * <pre>{@code
 * // properties 中配置：
 * // user.not.found=User [{0}] not found
 * // user.not.found=用户 [{0}] 不存在
 *
 * throw new I18nException("user.not.found", userId);
 * }</pre>
 *
 * @author kuma
 */
public class I18nException extends RuntimeException {

    private final String messageCode;
    private final Object[] args;

    public I18nException(String messageCode) {
        super(messageCode);
        this.messageCode = messageCode;
        this.args = null;
    }

    public I18nException(String messageCode, Object... args) {
        super(messageCode);
        this.messageCode = messageCode;
        this.args = args;
    }

    public I18nException(String messageCode, Throwable cause) {
        super(messageCode, cause);
        this.messageCode = messageCode;
        this.args = null;
    }

    public I18nException(String messageCode, Throwable cause, Object... args) {
        super(messageCode, cause);
        this.messageCode = messageCode;
        this.args = args;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public Object[] getArgs() {
        return args;
    }
}
