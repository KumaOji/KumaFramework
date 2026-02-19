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

package com.kuma.boot.data.mybatis.sharding.annos;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记sharding规则， 仅支持简单sharding
 * 只能放在interface上面，一个interface 只能访问一个逻辑表
 * 可以支持仅分表， 也可以支持仅分库， 也可以支持分库+分表
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Sharding {

    /**
     * datasource names
     *
     * @return datasource names, exactly the name what you have configured
     */
    String[] datasource() default {};

    /**
     * specify the location of the mapper xml files
     *
     * @return location of mapper files
     */
    String mapperLocation();

    /**
     * example:  'ds-' + userId % 1024 / 64
     *
     * @return actual database rule
     */
    String dbRule() default "";

    /**
     * example: 'trade_order_' + userId % 1024 % 64
     *
     * @return actual table rule
     */
    String tableRule() default "";

    /**
     * 分库或者分表的时候表达式里面用到的占位符字段
     *
     * @return 占位符字段名称
     */
    String shardingKey() default "";
}
