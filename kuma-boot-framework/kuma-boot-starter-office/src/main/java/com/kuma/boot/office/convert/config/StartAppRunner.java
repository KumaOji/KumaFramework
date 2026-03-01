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

package com.kuma.boot.office.convert.config;

import com.aspose.words.License;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * 服务初始化之后，执行方法
 *
 * @since 2020/5/22 19:29
 */
@Component
public class StartAppRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        LogUtils.info("《服务初始化执行处理》 start...");
        try {
            LogUtils.info("实现`aspose-words`授权 -> 去掉头部水印");
            /*
             实现匹配文件授权 -> 去掉头部水印 `Evaluation Only. Created with Aspose.Words. Copyright 2003-2018 Aspose Pty Ltd.` |
                                         `Evaluation Only. Created with Aspose.Cells for Java. Copyright 2003 - 2020 Aspose Pty Ltd.`
            */
            LogUtils.info("暂时不考虑去除水印");
//            InputStream is = new ClassPathResource("license.xml").getInputStream();
//            License license = new License();
//            license.setLicense(is);
        } catch (Exception e) {
            LogUtils.error("《`aspose-words`授权》 失败： {}", e.getMessage());
        }
        LogUtils.info("《服务初始化执行处理》 end...");
    }
}
