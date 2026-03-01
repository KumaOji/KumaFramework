/*
 * Copyright © 2017-2023 Knife4j(xiaoymin@foxmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.kuma.boot.springdoc.knife4j.spring.configuration;

import com.github.xiaoymin.knife4j.core.extend.OpenApiExtendSetting;
import com.github.xiaoymin.knife4j.core.model.MarkdownProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/***
 * 配置文件
 * {@code @since } 1.9.6
 * @author <a href="mailto:xiaoymin@foxmail.com">xiaoymin@foxmail.com</a>
 * 2019/08/27 15:40
 */
@ConfigurationProperties(prefix = "knife4j")
public class Knife4jProperties {

    /**
     * 是否开启Knife4j增强模式
     */
    private boolean enable = false;
    /**
     * 是否开启默认跨域
     */
    private boolean cors = false;

    /**
     * 是否开启BasicHttp验证
     */
    private com.kuma.boot.springdoc.knife4j.spring.configuration.Knife4jHttpBasic basic;

    /**
     * 是否生产环境
     */
    private boolean production = false;

    /**
     * 个性化配置
     */
    private Knife4jSetting setting;

    /**
     * 分组文档集合
     */
    private List<MarkdownProperty> documents;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isCors() {
        return cors;
    }

    public void setCors(boolean cors) {
        this.cors = cors;
    }

    public com.kuma.boot.springdoc.knife4j.spring.configuration.Knife4jHttpBasic getBasic() {
        return basic;
    }

    public void setBasic(com.kuma.boot.springdoc.knife4j.spring.configuration.Knife4jHttpBasic basic) {
        this.basic = basic;
    }

    public boolean isProduction() {
        return production;
    }

    public void setProduction(boolean production) {
        this.production = production;
    }

    public Knife4jSetting getSetting() {
        return setting;
    }

    public void setSetting(Knife4jSetting setting) {
        this.setting = setting;
    }

    public List<MarkdownProperty> getDocuments() {
        return documents;
    }

    public void setDocuments(List<MarkdownProperty> documents) {
        this.documents = documents;
    }
}
