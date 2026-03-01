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

package com.kuma.boot.security.spring.core;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.kuma.boot.security.spring.constants.BaseConstants;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <p>用户登录额外信息
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:03:57
 */
public class PrincipalDetails {

    /**
     * 开放id
     */
    private String openId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 角色
     */
    private Set<String> roles;

    /**
     * 雇员id
     */
    private String employeeId;

    /**
     * 《阿凡达》
     */
    private String avatar;

    /**
     * 得到开放id
     *
     * @return {@link String }
     * @since 2023-07-04 10:03:57
     */
    public String getOpenId() {
        return openId;
    }

    /**
     * 设置开放id
     *
     * @param openId 开放id
     * @since 2023-07-04 10:03:57
     */
    public void setOpenId(String openId) {
        this.openId = openId;
    }

    /**
     * 获得用户名
     *
     * @return {@link String }
     * @since 2023-07-04 10:03:57
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置用户名
     *
     * @param userName 用户名
     * @since 2023-07-04 10:03:57
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 得到角色
     *
     * @return {@link Set }<{@link String }>
     * @since 2023-07-04 10:03:57
     */
    public Set<String> getRoles() {
        return roles;
    }

    /**
     * 设置角色
     *
     * @param roles 角色
     * @since 2023-07-04 10:03:57
     */
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    /**
     * 让雇员id
     *
     * @return {@link String }
     * @since 2023-07-04 10:03:57
     */
    public String getEmployeeId() {
        return employeeId;
    }

    /**
     * 设置员工id
     *
     * @param employeeId 雇员id
     * @since 2023-07-04 10:03:58
     */
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    /**
     * 让《阿凡达》
     *
     * @return {@link String }
     * @since 2023-07-04 10:03:58
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * 集《阿凡达》
     *
     * @param avatar 《阿凡达》
     * @since 2023-07-04 10:03:58
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * 映射
     *
     * @return {@link Map }<{@link String }, {@link Object }>
     * @since 2023-07-04 10:03:58
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(BaseConstants.OPEN_ID, this.openId);
        map.put(BaseConstants.USERNAME, this.userName);
        map.put(BaseConstants.ROLES, this.roles);
        map.put(BaseConstants.EMPLOYEE_ID, this.employeeId);
        map.put(BaseConstants.AVATAR, this.avatar);
        return map;
    }

    /**
     * =
     *
     * @param o o
     * @return boolean
     * @since 2023-07-04 10:03:58
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PrincipalDetails that = (PrincipalDetails) o;
        return Objects.equal(openId, that.openId);
    }

    /**
     * 散列码
     *
     * @return int
     * @since 2023-07-04 10:03:58
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(openId);
    }

    /**
     * 字符串
     *
     * @return {@link String }
     * @since 2023-07-04 10:03:58
     */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("userId", openId)
                .add("userName", userName)
                .add("roles", roles)
                .add("employeeId", employeeId)
                .add("avatar", avatar)
                .toString();
    }
}
