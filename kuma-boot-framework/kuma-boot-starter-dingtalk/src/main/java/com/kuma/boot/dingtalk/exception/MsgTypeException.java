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

package com.kuma.boot.dingtalk.exception;

import com.kuma.boot.dingtalk.enums.ExceptionEnum;

/**
 * 类型异常
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-07-06 15:21:30
 */
public class MsgTypeException extends DingerException {
    public MsgTypeException(String msg) {
        super(msg, ExceptionEnum.MSG_TYPE_CHECK);
    }

    public MsgTypeException(Throwable cause) {
        super(cause, ExceptionEnum.MSG_TYPE_CHECK);
    }
}
