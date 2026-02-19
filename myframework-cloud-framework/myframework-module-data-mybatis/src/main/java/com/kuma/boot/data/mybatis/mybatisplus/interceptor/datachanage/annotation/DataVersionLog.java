/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datachanage.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据版本日志
 *
 * @DataVersionLog("系统日志变更数据")
 * @Getter
 * @Setter
 * @ToString(callSuper = true)
 * @NoArgsConstructor
 * @AllArgsConstructor
 * @Entity
 * @Table(name = Log.TABLE_NAME)
 * @TableName(Log.TABLE_NAME)
 * @org.hibernate.annotations.Table(appliesTo = Log.TABLE_NAME, comment = "日志表")
 * public class Log extends BaseSuperEntity<Log, Long> {
 * }
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DataVersionLog {

    /**
     * 数据记录的标题
     */
    String title() default "";
}
