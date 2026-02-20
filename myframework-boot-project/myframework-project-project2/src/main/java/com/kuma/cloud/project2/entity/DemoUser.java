/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use it except in compliance with the License.
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

package com.kuma.cloud.project2.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.kuma.boot.data.mybatis.mybatisplus.base.entity.MpSuperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 演示用户实体 - data-mybatis MpSuperEntity 使用示例
 * <p>对应表 demo_user，需先执行 schema.sql 建表</p>
 *
 * @author kuma
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("demo_user")
public class DemoUser extends MpSuperEntity<Long> {

    private String name;
    private String email;
}
