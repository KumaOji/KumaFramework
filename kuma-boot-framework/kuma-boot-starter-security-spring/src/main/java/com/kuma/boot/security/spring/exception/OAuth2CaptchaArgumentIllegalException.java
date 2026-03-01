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

package com.kuma.boot.security.spring.exception;

/**
 * <p>Oauth2 使用的验证码参数错误
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:07:00
 */
public class OAuth2CaptchaArgumentIllegalException extends OAuth2CaptchaException {

    /**
     * oauth2验证码参数非法例外
     *
     * @param msg   味精
     * @param cause 导致
     * @since 2023-07-04 10:07:00
     */
    public OAuth2CaptchaArgumentIllegalException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * oauth2验证码参数非法例外
     *
     * @param msg 味精
     * @since 2023-07-04 10:07:00
     */
    public OAuth2CaptchaArgumentIllegalException(String msg) {
        super(msg);
    }

    //    @Override
    //    public Feedback getFeedback() {
    //        return FeedbackFactory.notAcceptable(CaptchaErrorCodes.CAPTCHA_PARAMETER_ILLEGAL,
    // "验证码参数格式错误");
    //    }
}
