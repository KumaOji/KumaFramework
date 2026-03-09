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

package com.kuma.cloud.design.patterns.pipeline.demo;

import cn.hutool.json.JSONUtil;
import com.kuma.cloud.design.patterns.pipeline.AbstractDemoJob;
import com.kuma.cloud.design.patterns.pipeline.DemoPipelineProduct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 审核-参数验证-实现类
 *
 * @author
 * @date 2023/05/15 19:50
 */
@Slf4j
@Component
public class ManagerCheckParamJob extends AbstractDemoJob {

    /**
     * 执行基本入参验证
     *
     * @param tradeId
     * @param productData 请求数据
     * @return
     */
    @Override
    protected DemoPipelineProduct.DemoSignalEnum execute(
            String tradeId, DemoPipelineProduct.DemoProductData productData) {
        /*
         * 入参验证
         */
        DemoReq userRequestData = productData.getUserRequestData();
        log.info("任务[{}]入参验证,线程号:{}", JSONUtil.toJsonStr(userRequestData), tradeId);
        // 非空验证

        // 有效验证

        // 校验通过,退出
        return DemoPipelineProduct.DemoSignalEnum.NORMAL;
    }
}
