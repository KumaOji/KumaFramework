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

package com.kuma.boot.data.mybatis.interceptor.easylog.common.utils.audit;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.kuma.boot.common.utils.lang.StringUtils;

/**
 * ParsePrimaryKeyUtils
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class ParsePrimaryKeyUtils {

    /**
     * 解析主键 aop截取的参数 要从参数中解析出来的操作数据主键-当等于 GET 的时候默认取 objs toString
     */
    public static String resolveRhePrimaryKey( Object[] objs, String promaryKeyTitle ) {
        // 解析操作数据的唯一主键的标识
        if (!StringUtils.isEmpty(promaryKeyTitle) && objs != null) {

            // 主键-可能是批量操作的
            StringBuilder sb = new StringBuilder();

            for (Object obj : objs) {
                if (obj != null) {
                    if ("GET".equals(promaryKeyTitle)) {
                        sb.append(obj.toString() + ",");
                    } else {
                        JSONObject json = JSON.parseObject(JSON.toJSONString(obj));
                        if (!StringUtils.isEmpty(json.getString(promaryKeyTitle))) {
                            sb.append(json.getString(promaryKeyTitle) + ",");
                        }
                    }
                }
            }

            // 不是空
            if (!StringUtils.isEmpty(sb.toString())) {
                String id = sb.toString().substring(0, sb.toString().length() - 1);
                if (id.contains(",")) {
                    return null;
                }
                return id;
            }
        }
        return null;
    }
}
