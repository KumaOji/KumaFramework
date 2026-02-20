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

package com.kuma.cloud.project2.mapper;

import com.kuma.boot.data.mybatis.mybatisplus.base.mapper.MpSuperMapper;
import com.kuma.cloud.project2.entity.DemoUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 演示用户 Mapper - data-mybatis MpSuperMapper 使用示例
 * <p>继承 MpSuperMapper 获得 selectOne、selectList、selectPage、insertBatch 等扩展方法</p>
 *
 * @author kuma
 */
@Mapper
public interface DemoUserMapper extends MpSuperMapper<DemoUser, Long> {
}
