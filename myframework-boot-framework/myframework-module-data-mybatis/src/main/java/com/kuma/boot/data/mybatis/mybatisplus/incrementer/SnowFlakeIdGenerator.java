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

package com.kuma.boot.data.mybatis.mybatisplus.incrementer;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.kuma.boot.common.utils.id.IdGeneratorUtils;
import com.kuma.boot.common.utils.log.LogUtils;

/**
 * 雪花算法ID生成器
 *
 * <p>{@code
 *	@TableId(type = IdType.ASSIGN_ID)  使用IdType.ASSIGN_ID
 *	@ApiModelProperty(value = "主键")
 *	private Long id;
 * }
 *
 * </p>
 *
 * @author kuma
 * @version 2022.04
 * @since 2022-04-27 17:16:48
 */
public class SnowFlakeIdGenerator implements IdentifierGenerator {

    @Override
    public Number nextId(Object entity) {
        // 可以将当前传入的class全类名来作为bizKey,或者提取参数来生成bizKey进行分布式Id调用生成.
        String className = entity.getClass().getName();

        LogUtils.info(
                "mybatis plus IdentifierGenerator -> SnowFlakeIdGenerator bizKey: {} ", className);

        return IdUtil.getSnowflakeNextId();
    }
}
