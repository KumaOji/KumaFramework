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

package com.kuma.cloud.job.server.persistence.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kuma.cloud.job.server.persistence.domain.JobInfo;
import com.kuma.cloud.job.server.persistence.mapper.JobInfoMapper;
import com.kuma.cloud.job.server.persistence.service.JobInfoService;
import org.springframework.stereotype.Service;

/**
 * @author kuma
 * @description 针对表【job_info】的数据库操作Service实现
 * @createDate 2024-10-20 19:56:42
 */
@Service
public class JobInfoServiceImpl extends ServiceImpl<JobInfoMapper, JobInfo>
        implements JobInfoService {}
