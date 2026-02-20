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

package com.kuma.boot.web.gracefulresponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class AbstractExceptionAliasRegisterConfig implements ApplicationContextAware {

    private Logger logger = LoggerFactory.getLogger(AbstractExceptionAliasRegisterConfig.class);

    protected abstract void registerAlias( ExceptionAliasRegister register);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {

        try {
            ExceptionAliasRegister aliasRegister =
                    applicationContext.getBean(ExceptionAliasRegister.class);
            this.registerAlias(aliasRegister);
        } catch (Exception e) {
            logger.warn(
                    "未从ApplicationContext中获取到ExceptionAliasRegister实例， @ExceptionAliasFor注解将无效", e);
        }
    }
}
