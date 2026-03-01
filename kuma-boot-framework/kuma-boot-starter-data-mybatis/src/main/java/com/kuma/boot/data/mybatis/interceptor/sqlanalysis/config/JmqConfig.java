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

package com.kuma.boot.data.mybatis.interceptor.sqlanalysis.config;

import java.util.Properties;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author huhaitao21
 * @Description jmq相关配置
 * @since 18:20 2023/2/9
 **/
public class JmqConfig {

    private static Logger logger = LoggerFactory.getLogger(JmqConfig.class);

    /**
     * 应用
     */
    private static String app;

    /**
     * 用户
     */
    private static String user;

    /**
     * 密码
     */
    private static String password;

    /**
     * mq地址
     */
    private static String address;

    /**
     * 发送topic
     */
    private static String topic;

    /**
     * 应用名称配置key
     */
    private static String MQ_APP = "mqApp";

    /**
     * 用户配置key
     */
    private static String MQ_USER = "mqUser";

    /**
     * 密码配置key
     */
    private static String MQ_PASSWORD = "mqPassword";

    /**
     * mq地址配置key
     */
    private static String MQ_ADDRESS = "mqAddress";

    /**
     * topic
     */
    private static String MQ_TOPIC = "mqTopic";

    /**
     * 初始化配置
     *
     * @param properties
     */
    public static void initConfig(Properties properties) {
        // 检查参数 初始化参数
        boolean result = checkConfig(properties);
        if (result) {
            app = properties.getProperty(MQ_APP);
            user = properties.getProperty(MQ_USER);
            password = properties.getProperty(MQ_PASSWORD);
            address = properties.getProperty(MQ_ADDRESS);
            topic = properties.getProperty(MQ_TOPIC);
        }
    }

    private static boolean checkConfig(Properties properties) {
        if (properties == null) {
            return false;
        }
        if (StringUtils.isBlank(properties.getProperty(MQ_APP))
                || StringUtils.isBlank(properties.getProperty(MQ_USER))
                || StringUtils.isBlank(properties.getProperty(MQ_PASSWORD))
                || StringUtils.isBlank(properties.getProperty(MQ_ADDRESS))
                || StringUtils.isBlank(properties.getProperty(MQ_TOPIC))) {
            return false;
        }

        return true;
    }

    /**
     * 启动生产者
     */
    public static boolean initMqProducer() {
        try {
            // todo 初始化生产者
            return true;
        } catch (Exception e) {
            logger.error("sql analysis mq config init error");
            return false;
        }
    }

    public static String getApp() {
        return app;
    }

    public static void setApp(String app) {
        JmqConfig.app = app;
    }

    public static String getUser() {
        return user;
    }

    public static void setUser(String user) {
        JmqConfig.user = user;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        JmqConfig.password = password;
    }

    public static String getAddress() {
        return address;
    }

    public static void setAddress(String address) {
        JmqConfig.address = address;
    }

    public static String getTopic() {
        return topic;
    }

    public static void setTopic(String topic) {
        JmqConfig.topic = topic;
    }
}
