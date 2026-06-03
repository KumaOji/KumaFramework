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

package com.kuma.boot.dingtalk.spring;

import com.kuma.boot.dingtalk.session.DefaultDingerSessionFactory;
import com.kuma.boot.dingtalk.session.DingerSessionFactory;
import com.kuma.boot.dingtalk.session.SessionConfiguration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;

/**
 * DingerSessionFactoryBean
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-07-06 15:25:24
 */
public class DingerSessionFactoryBean implements FactoryBean<DingerSessionFactory>, InitializingBean {

    private DingerSessionFactory dingerSessionFactory;
    private SessionConfiguration sessionConfiguration;

    @Override
    public DingerSessionFactory getObject() throws Exception {
        if (this.dingerSessionFactory == null) {
            afterPropertiesSet();
        }
        return dingerSessionFactory;
    }

    @Override
    public Class<?> getObjectType() {
        return this.dingerSessionFactory == null ? DingerSessionFactory.class : this.dingerSessionFactory.getClass();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.dingerSessionFactory = buildDingerSessionFactory();
    }

    protected DingerSessionFactory buildDingerSessionFactory() throws IOException {
        return new DefaultDingerSessionFactory(sessionConfiguration);
    }

    public void setConfiguration(SessionConfiguration sessionConfiguration) {
        this.sessionConfiguration = sessionConfiguration;
    }
}
