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
import cn.hutool.core.io.FileUtil;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

/**
 * `aspose-words`授权处理
 *
 * @since 2020/12/30$ 11:33$
 */
public class MatchLicense {

    public static void init() {
        try {
            LogUtils.info("实现`aspose-words`授权 -> 去掉头部水印");
            /*
             实现匹配文件授权 -> 去掉头部水印 `Evaluation Only. Created with Aspose.Words. Copyright 2003-2018 Aspose Pty Ltd.` |
                                         `Evaluation Only. Created with Aspose.Cells for Java. Copyright 2003 - 2020 Aspose Pty Ltd.`
            */
            InputStream is = new ClassPathResource("license.xml").getInputStream();
            License license = new License();
            license.setLicense(is);

            // 临时写法
            FileUtil.mkdir(com.kuma.boot.office.convert.config.Constants.DEFAULT_FOLDER_TMP_GENERATE);
        } catch (Exception e) {
            LogUtils.error("《`aspose-words`授权》 失败： {}", e.getMessage());
        }
    }
}
