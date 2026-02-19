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

package com.kuma.boot.data.datasource.druid;

import com.alibaba.druid.util.DruidPasswordCallback;
import com.alibaba.fastjson2.JSONObject;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.datasource.utils.RSAUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.stream.Collectors;

import org.springframework.core.io.ResourceLoader;

/**
 * RsaDBPasswordCallBack
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class RsaDBPasswordCallBack extends DruidPasswordCallback {

    // 构造方法，传入resourceLoader，用于读取RSA公私钥文件内容
    private final ResourceLoader resourceLoader;

    public RsaDBPasswordCallBack( ResourceLoader resourceLoader ) {
        this.resourceLoader = resourceLoader;
    }

    private static final String RSA_KEYS_FILE_PATH = "classpath:/druid/rsa/rsa_keys.txt";

    @Override
    public void setProperties( Properties properties ) {
        super.setProperties(properties);
        try (InputStream ism = resourceLoader.getResource(RSA_KEYS_FILE_PATH).getInputStream()) {
            String content =
                    new BufferedReader(new InputStreamReader(ism))
                            .lines()
                            .collect(Collectors.joining(System.lineSeparator()));
            JSONObject object = JSONObject.parseObject(content);
            String privateKey = object.getString("private");
            String password = properties.getProperty("password");
            char[] charPassword = RSAUtil.decryptByPrivateKey(password, privateKey).toCharArray();
            super.setPassword(charPassword);
        } catch (Exception e) {
            LogUtils.error("Druid密码回调报错：" + e.getMessage());
        }
    }
}
