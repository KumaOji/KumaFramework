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

package com.kuma.boot.security.spring.event.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>系统安全属性实体 </p>
 *
 */
@Schema(title = "系统安全属性数据")
public class KmcAttribute {

    @Schema(title = "元数据ID")
    private String attributeId;

    @Schema(title = "默认权限代码")
    private String attributeCode;

    @Schema(name = "请求方法")
    private String requestMethod;

    @Schema(name = "服务ID")
    private String serviceId;

    @Schema(name = "接口所在类")
    private String className;

    @Schema(name = "接口对应方法")
    private String methodName;

    @Schema(name = "请求URL")
    private String url;

    @Schema(title = "表达式", description = "Security表达式字符串，通过该值设置动态权限")
    private String webExpression;

    @Schema(name = "属性对应权限", title = "根据属性关联权限数据")
    private Set<KmcPermission> permissions = new HashSet<>();

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getAttributeCode() {
        return attributeCode;
    }

    public void setAttributeCode(String attributeCode) {
        this.attributeCode = attributeCode;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWebExpression() {
        return webExpression;
    }

    public void setWebExpression(String webExpression) {
        this.webExpression = webExpression;
    }

    public Set<KmcPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<KmcPermission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KmcAttribute that = (KmcAttribute) o;
        return Objects.equal(attributeId, that.attributeId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(attributeId);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("attributeId", attributeId)
                .add("attributeCode", attributeCode)
                .add("requestMethod", requestMethod)
                .add("serviceId", serviceId)
                .add("className", className)
                .add("methodName", methodName)
                .add("url", url)
                .add("webExpression", webExpression)
                .toString();
    }
}
