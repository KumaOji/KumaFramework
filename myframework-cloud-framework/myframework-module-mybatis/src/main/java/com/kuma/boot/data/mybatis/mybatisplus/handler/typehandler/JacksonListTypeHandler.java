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

package com.kuma.boot.data.mybatis.mybatisplus.handler.typehandler;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * Jackson 实现 JSON 字段类型处理器, 会记录对象属性类型, 通常用于被容器(List、Set、Map)包装的属性上
 *
 * <pre>{@code
 *	支付通道信息列表
 * @see PayChannelInfo
 * @TableField(typeHandler = JacksonListTypeHandler.class)
 * @BigField
 * private List<PayChannelInfo> payChannelInfo;
 *  }
 * </pre>
 *
 */
@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class JacksonListTypeHandler extends AbstractJsonTypeHandler<Object> {
    private final Class<?> type;

    public JacksonListTypeHandler(Class<?> type) {
        super(type);

        if (LogUtils.isTraceEnabled()) {
            LogUtils.trace("JacksonListTypeHandler(" + type + ")");
        }
        Assert.notNull(type, "Type argument cannot be null");
        this.type = type;
    }

    @Override
    public Object parse(String json) {
        return JacksonUtils.toObject(json, type);
    }

    @Override
    public String toJson(Object obj) {
        return JacksonUtils.toJSONString(obj);
    }
}
